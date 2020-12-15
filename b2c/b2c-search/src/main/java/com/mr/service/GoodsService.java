package com.mr.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.item.common.utils.JsonUtils;
import com.item.common.utils.PageResult;
import com.mr.GoodBo;
import com.mr.client.BrandClient;
import com.mr.client.CategoryClient;
import com.mr.client.GoodsClient;
import com.mr.client.SpecClient;
import com.mr.dao.GoodsRepository;
import com.mr.pojo.*;
import com.mr.util.HighLightUtil;
import com.mr.util.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GoodsService {
    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private SpecClient specClient;

    @Autowired
    private BrandClient brandClient;

    //填充good对象
    public Goods getGood(Long spuId){
        //查询spu对象
        Spu spu =goodsClient.querySpuById(spuId);

        // 查询spu下的 大字段 商品详情等
        SpuDetail detail = goodsClient.queryDetail(spuId);

        //获得sku集合
        List<Sku> skus = goodsClient.querySku(spuId);

        //填充all 需要品牌信息，和分类信息
       Brand brand = brandClient.queryBrandByBid(spu.getBrandId());
        //查询分类名称
      List<String> cateNameList = categoryClient.queryCategoryNameBycIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));

        //查询spu所属分类的规格（用于搜索的字段）
        List<SpecParam> params = specClient.querySpecParam(null, spu.getCid3(),null, true);

        //价格集合
        List<Long> prices =new ArrayList<>();
        //填充sku属性 sku用map不用实体类 装入价格，图片等页面需要的元素
        List<Map<String,Object>> skuList =new ArrayList<>();
        for(Sku sku : skus){
            prices.add(sku.getPrice());
            Map<String,Object> map =new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            //取第一张图片即可
            map.put("image",StringUtils.isBlank(sku.getImages()) ? "" :sku.getImages().split(",")[0]);
            map.put("price",sku.getPrice());
            skuList.add(map);
            }
        //处理规格参数
        Map<Long,String> genericMap = JsonUtils.parseMap(detail.getGenericSpec(),Long.class,String.class);
        Map<Long,List<String>> specialMap =JsonUtils.nativeRead(detail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {});

        Map<String,Object> specs = new HashMap<>();

        for (SpecParam param : params){
            if (param.getGeneric()){
                //通用参数
                String value = genericMap.get(param.getId());
                if(param.getNumeric()){
                    // 数值类型，需要存储一个分段例如5.0变成 4.8-5.2
                    //这样方法方便筛选，数字类型不用range区间查询，直接matchall就可以
                    value =this.chooseSegment(value,param);
                }
                specs.put(param.getName(),value);
            }else {
                //特有参数
                specs.put(param.getName(),specialMap.get(param.getId()));
            }
        }

        //通过spuid   查询数据 封装对象
        Goods goods  =  new Goods();
        goods.setId(spuId);
        goods.setAll(spu.getTitle()+"  "+brand.getName()+"  "+ StringUtils.join(cateNameList,"/"));
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setPrice(prices);
        goods.setSkus(JsonUtils.serialize(skuList));
        goods.setSpecs(specs);
        goods.setSubTitle(spu.getSubTitle());
        return goods;
    }



    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 查询
     * @param goodBo
     * @return
     */
    public SearchResult<Goods> searhGood(GoodBo goodBo) {
        // 创建查询构建器

        //搜索条件
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();




        //设置条件
        if (goodBo !=null && StringUtils.isNotEmpty(goodBo.getKey())) {
            builder.withQuery(QueryBuilders.boolQuery().must(
                    QueryBuilders.matchQuery("all", goodBo.getKey())

            ));
        }
        //由于不能影响成绩， 规格 filter 而不是match

        //存在规格筛选
        if(goodBo.getFilters() !=null && goodBo.getFilters().size()>0){
            Map<String,String> specMap =goodBo.getFilters();
            Set<String> specSet = specMap.keySet();
            //
            BoolQueryBuilder  boolQueryBuilder= QueryBuilders.boolQuery();
            for (String key : specSet){
                MatchQueryBuilder matchQueryBuilder =null;
                //条件不一样 cid 品牌 普通
                if(key.equals("cid3") || key.equals("brandId")) {
                    matchQueryBuilder = QueryBuilders.matchQuery(key, specMap.get(key));
                }else{
                    matchQueryBuilder = QueryBuilders.matchQuery("specs."+key+".keyword",specMap.get(key));
                }
                boolQueryBuilder.must(matchQueryBuilder);
            }
            //在循环外设置不会被覆盖
            builder.withFilter(boolQueryBuilder);
        }


            //分页
            builder.withPageable(PageRequest.of(goodBo.getPage()-1,goodBo.getRows()));

            //汇总分类数据
            builder.addAggregation(AggregationBuilders.terms("cateGro").field("cid3"));
            //汇总品牌数据
            builder.addAggregation(AggregationBuilders.terms("brandGro").field("brandId"));

            //普通分页数据
            Page<Goods> goodPage = goodsRepository.search(builder.build());
            //转为聚合数据
            AggregatedPage<Goods> aggGoodPage = (AggregatedPage<Goods>) goodPage;
            //返回值是long类型
           //分类
           LongTerms cateterms= (LongTerms) aggGoodPage.getAggregation("cateGro");
           //分类数据
           List<LongTerms.Bucket> cateBucket = cateterms.getBuckets();

           //分类数据，查询名称（这是当前搜索条件下所有商品的分类）从所有的分类中读取热度较高的分类（商品最多的分类）

        final List<Long> maxCountList=new ArrayList(1);
        maxCountList.add(0,0L);
        final List<Long> maxCateIdList=new ArrayList(1);
        maxCateIdList.add(0,0l);
        //求出最大值
        List<Category> categoryList = cateBucket.stream().map(bucket -> {
            if( maxCountList.get(0) < bucket.getDocCount()){
                maxCountList.set(0,bucket.getDocCount());
                maxCateIdList.set(0,bucket.getKeyAsNumber().longValue());
            }
            //该分类下的数量条款 找出count最多的那个分类

            return categoryClient.queryCategoryById(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());

        System.out.println("商品最多的分类："+maxCateIdList.get(0));
        //要根据id查询出要展示的筛选的规格
        //要从ES索引库，聚合出每个规格的值
        List<Map<String,Object>> specMapList =this.getSpecMapList(maxCateIdList.get(0),goodBo);


        //品牌数据
            //返回值是long类型
            LongTerms brandTerms= (LongTerms) aggGoodPage.getAggregation("brandGro");
            //分类数据
            List<LongTerms.Bucket> brandBucket = brandTerms.getBuckets();

            //品牌需要的是整个对象
//            brandBucket.forEach(bucket -> {
//                System.out.println("汇总出来的品牌id:"+bucket.getKeyAsNumber().longValue());
//            });
             List<Brand> brandList = brandBucket.stream().map(bucket -> {
            //查询品牌数据
                 return brandClient.queryBrandByBid(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());



            //设置高亮
            builder.withHighlightFields(new HighlightBuilder.Field("all").preTags("<font color='red'>").postTags("</font>"));
            Map<Long,String> hignMap = HighLightUtil.getHignLigntMap(elasticsearchTemplate,builder.build(),Goods.class,"all");
          //填充高亮
            goodPage.getContent().forEach(goods -> {
                goods.setAll(hignMap.get(goods.getId()));
            });


        goodPage.getContent().forEach(goods -> {
            System.out.println(goods.getAll());
        });
        //算出总页数
        Double totalPage = Double.valueOf(goodPage.getTotalElements())/goodBo.getRows();
        totalPage = Math.ceil(totalPage);


        //搜索商品，返回了 集合数据，总条数，总页数 还需要额外返回 分类筛选 品牌筛选 规格筛选
        return new SearchResult<Goods>(goodPage.getTotalElements(),goodPage.getContent(),
                Long.valueOf(totalPage.intValue()),categoryList,brandList,specMapList);
    }

    /**
     * 查询所有的规格和规格值
     * @param cId
     * @param goodBo
     * @return
     */
    public List<Map<String,Object>> getSpecMapList(Long cId,GoodBo goodBo){
//返回的集合
        List<Map<String,Object>> specParamMapList = new ArrayList<>();
        //要根据id查询出要展示的筛选的规格
       List<SpecParam> specParamList = specClient.querySpecParam(null,cId,null,true);
        //要从ES索引库，聚合出每个规格的值
        //先筛选符合关键字的数据
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        //
        BoolQueryBuilder  boolQueryBuilder= QueryBuilders.boolQuery();

        //关键字搜索
        if (StringUtils.isNotEmpty(goodBo.getKey())){

                boolQueryBuilder.must(QueryBuilders.matchQuery("all",goodBo.getKey()));

        }
        //不查询数据
        builder.withPageable(PageRequest.of(0,1));
        //循环聚合所有规格
        specParamList.forEach(specParam -> {


            builder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs."+specParam.getName()+".keyword"));
        });
        //存在规格筛选
        if(goodBo.getFilters() !=null && goodBo.getFilters().size()>0){
            Map<String,String> specMap =goodBo.getFilters();
            Set<String> specSet = specMap.keySet();

            for (String key : specSet){
                MatchQueryBuilder matchQueryBuilder =null;
                //条件不一样 cid 品牌 普通
                if(key.equals("cid3") || key.equals("brandId")) {
                    matchQueryBuilder = QueryBuilders.matchQuery(key, specMap.get(key));
                }else{
                    matchQueryBuilder = QueryBuilders.matchQuery("specs."+key+".keyword",specMap.get(key));
                }
                boolQueryBuilder.must(matchQueryBuilder);
            }

        }

        //在循环外设置不会被覆盖
        builder.withQuery(boolQueryBuilder);

        //查询聚合结果
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) goodsRepository.search(builder.build());
        //循环取聚合数据
        specParamList.forEach(specParam -> {


            //根据别名取值
           StringTerms specTerms = (StringTerms) goodsPage.getAggregation(specParam.getName());
           List<StringTerms.Bucket> list =specTerms.getBuckets();
           //只取值一列（key）
           List<String> valList = list.stream().map(bucket -> {
               return bucket.getKeyAsString();
           }).collect(Collectors.toList());

            Map<String,Object> specMap =new HashMap<>();
            specMap.put("key",specParam.getName());
            specMap.put("values",valList);
            specParamMapList.add(specMap);

        });
        return specParamMapList;
    }
    /**
     * 根据spuId新增/修改索引
     * @param spuId
     */
    public void  saveGoodForEs(Long spuId){
        Spu spu=  goodsClient.querySpuById(spuId);
        Goods goods=this.getGood(spu.getId());
        this.goodsRepository.save(goods);
    }

    /**
     * 根据spuId新增/修改索引
     * @param spuId
     */
    public void  deleteGoodForEs(Long spuId){
        this.goodsRepository.deleteById(spuId);
    }

}


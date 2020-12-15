package com.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.item.common.rebbitmq.MqMessageConstant;
import com.item.common.utils.PageResult;
import com.item.mapper.*;
import com.item.util.MqMessage;
import com.mr.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Resource
    private SpuMapper spuMapper;

    @Resource
    private CategoryService categoryService;

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private  SpuDetailMapper spuDetailMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Autowired
    private MqMessage mqMessage;


    public PageResult<SpuBo> querySpuPage(Integer page,Integer rows , Boolean saleable,String search){
        Spu s=null;

        PageHelper.startPage(page,rows);
        Example example=new Example(Spu.class);
        Example.Criteria criteria=example.createCriteria();

        criteria.andEqualTo("saleable",saleable);

        if(!StringUtil.isEmpty(search)){
            criteria.andLike("title","%"+search+"%");
        }

        Page<Spu> pageInfo= (Page<Spu>) spuMapper.selectByExample(example);

        //拉姆达表达式循环操作对象
        List<SpuBo> list=  pageInfo.getResult().stream().map(spu -> {

            //复制对象到bo中
            SpuBo spuBo=new SpuBo();
            BeanUtils.copyProperties(spu,spuBo);


            //填充属性
            spuBo.setBrandName(
                    this.brandMapper.selectByPrimaryKey(spu.getBrandId()).getName()
            );

            //直接使用mapper批量查询接口工具类查询
            List<Category> clist=this.categoryMapper.selectByIdList(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
            //循环提取Name列
            List<String > cNameList=clist.stream().map(category -> category.getName()).collect(Collectors.toList());
            //将List join逗号链接
            String cname=StringUtils.join(cNameList,",");
            //设置到bo中
            spuBo.setCategoryName(cname);

            return spuBo;
        }).collect(Collectors.toList());//转为List
        return   new PageResult<SpuBo>(pageInfo.getTotal(),list);
    }
    //保存spu
    @Transactional
    public void saveGoods(SpuBo bo){
        Spu spu=new Spu();
        //保存spu
        BeanUtils.copyProperties(bo,spu);
        Date now=new Date();
        spu.setSaleable(true);
        spu.setValid(true);
        spu.setCreateTime(now);
        spu.setLastUpdateTime(now);
        spuMapper.insert(spu);
        //保存spu详情
        SpuDetail spuDetail=bo.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        spuDetailMapper.insert(spuDetail);

        //库存
        List<Sku> skuList=bo.getSkus();

        this.saveSkus(skuList,spu.getId());
        mqMessage.sendMessage(MqMessageConstant.SPU_EXCHANGE_NAME,MqMessageConstant.SPU_ROUT_KEY_SAVE, spu.getId());
    }



    //修改回显 1
    public SpuDetail queryDetail(Long spuId) {
        return spuDetailMapper.selectByPrimaryKey(spuId);
    }



     //修改回显 2
     public List<Sku> querySku(Long spuId) {
         //创建过滤条件类  第-种方法;
            /*Example example = new Example(Sku.class);

            //通过过滤条件类,构建实例,字段和参数相等
            example.createCriteria().andEqualTo("spuId",spuId);
            //设置条件查询 查询sku表
             List<Sku> skuList = skuMapper.selectByExample(example);
             //查询stock表,赋值给sku中的stock
              skuList.forEach(sku -> {
                sku.setStock(stockMapper.selectByPrimaryKey(sku.getId()).getStock());
            });
            return skuList;*/


         //第二种写法
         //用实体类当查询条件
         Sku skuEx = new Sku();
         skuEx.setSpuId(spuId);
         //select是根据实体类有值的字段当做查询条件 skus集合
         List<Sku> skuList = skuMapper.select(skuEx);
         //循环集合 获得集合中的对象
         skuList.forEach(sku->{
             //给每个对象赋值库存值; 根据skuId 查询对应库存
             sku.setStock(this.stockMapper.selectByPrimaryKey(sku.getId()).getStock());
         });
         return skuList;

     }

    //保存spu
    @Transactional
    public void update(SpuBo bo){

        // 查询以前sku
        List<Sku> skus = this.querySku(bo.getId());
        //得到id集合
        List<Long> skuIds =skus.stream().map(s -> s.getId()).collect(Collectors.toList());

        //批量删除库存stock
        stockMapper.deleteByIdList(skuIds);

        //根据实体条件删除sku
        Sku sku=new Sku();
        sku.setSpuId(bo.getId());
        skuMapper.delete(sku);


        //修改spu
        Spu spu=new Spu();
        BeanUtils.copyProperties(bo,spu);
        Date now=new Date();

        spu.setSaleable(null);
        spu.setValid(null);
        spu.setCreateTime(null);
        spu.setLastUpdateTime(now);
        spuMapper.updateByPrimaryKeySelective(spu);
        //修改详情
        SpuDetail spuDetail=bo.getSpuDetail();
        spuDetailMapper.updateByPrimaryKey(spuDetail);


        //库存
        List<Sku> skuList=bo.getSkus();

        this.saveSkus(skuList,spu.getId());

        mqMessage.sendMessage(MqMessageConstant.SPU_EXCHANGE_NAME,MqMessageConstant.SPU_ROUT_KEY_UPDATE, spu.getId());
    }

    //保存sku
    public void saveSkus(List<Sku> skuList, Long spuId){

        Date now =new Date();
        skuList.forEach(sku ->{
            if(sku.getEnable()){
                //保存sku
                sku.setSpuId(spuId);
                sku.setCreateTime(now);
                sku.setLastUpdateTime(now);
                skuMapper.insert(sku);
                //保存库存
                Stock stock=new Stock();
                stock.setSkuId(sku.getId());
                stock.setStock(sku.getStock());
                stockMapper.insert(stock);
            }
        });
    }
    //上下架
    public void saleableGoods(Spu spu) {
        if (null != spu){
            if (spu.getSaleable()){//false:0下架，true:1上架 要么上架,要么下架,不能同时上下架
                spu.setSaleable(false);
                //spuMapper.updateByPrimaryKeySelective(spu);
            }else {
                spu.setSaleable(true);
                //spuMapper.updateByPrimaryKeySelective(spu);
            }
            //相同代码提出来
            spuMapper.updateByPrimaryKeySelective(spu);
        }
    }

    public Spu querySpu(Long spuId) {
        return spuMapper.selectByPrimaryKey(spuId);
    }

    public Stock queryStockBySkuId(Long skuId) {
        return stockMapper.selectByPrimaryKey(skuId);
    }

    public Sku querySkuByStuId(Long skuId) {
        return skuMapper.selectByPrimaryKey(skuId);
    }
}

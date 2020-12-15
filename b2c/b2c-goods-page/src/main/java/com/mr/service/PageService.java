package com.mr.service;

import com.mr.client.BrandClient;
import com.mr.client.CategoryClient;
import com.mr.client.GoodsClient;
import com.mr.client.SpecClient;
import com.mr.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PageService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecClient specClient;

    public Map<String,Object>  queryItemInfo(Long spuId){
        Spu spu = goodsClient.querySpuById(spuId);

        List<Category> categoryList = categoryClient.queryCateByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));

       Brand brand = brandClient.queryBrandByBid(spu.getBrandId());

       List<Sku> skuList =  goodsClient.querySku(spuId);

        skuList = skuList.stream().map(sku -> {
           Stock stock = goodsClient.queryStockBySkuId(sku.getId());
            sku.setStock(stock.getStock());
            return  sku;
       }).collect(Collectors.toList());

        List<SpecGroup> specGroupList =specClient.querySpecGroupList(spu.getCid3());

        specGroupList.forEach(specGroup -> {

            List<SpecParam> genList = specClient.querySpecParam( specGroup.getId(),null,null,null);
            specGroup.setSpecParamList(genList);
        });

        Map<Long,String> specParamMap=new HashMap<>();
        List<SpecParam> specParamList =specClient.querySpecParam(null,spu.getCid3(),null,false);
        specParamList.forEach(specParam -> {
            specParamMap.put(specParam.getId(),specParam.getName());
        });

        SpuDetail spuDetail =goodsClient.queryDetail(spuId);

        Map<String,Object> map =new HashMap<>();

        map.put("spu",spu);
        map.put("spuDetail",spuDetail);
        map.put("skuList",skuList);
        map.put("categoryList",categoryList);
        map.put("brand",brand);
        map.put("specGroupList",specGroupList);
        map.put("specParamMap",specParamMap);
        return  map;
    }

}

package com.mr.pojo;

import lombok.Data;

import java.util.List;

@Data
public class SpuBo extends Spu {

    String categoryName;// 商品分类名称
    
    String brandName;// 品牌名称

    SpuDetail spuDetail;// 商品详情

    List<Sku> skus;// sku列表
    // 略 。。
}
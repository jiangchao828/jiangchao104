package com.mr.api;

import com.item.common.utils.PageResult;
import com.mr.pojo.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("good")
public interface GoodsApi {

    @GetMapping("/page")
    public PageResult<SpuBo> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "search", required = false) String search);
    @PostMapping
    public Void saveGoods(@RequestBody SpuBo spuBo);

    @GetMapping("/spu/detail/{spuId}")
    public SpuDetail queryDetail(@PathVariable("spuId") Long spuId);


    @GetMapping("/skuList/{spuId}")
    public List<Sku> querySku(@PathVariable("spuId") Long spuId);

    @PutMapping
    public Void updateGoods(@RequestBody  SpuBo spuBo);

    @PutMapping("saleable")
    public ResponseEntity saleableGoods(@RequestBody Spu spu);

    /**
     * 查询spu
     * @param spuId
     * @return
     */
    @GetMapping("spu/{spuId}")
    public Spu querySpuById(@PathVariable("spuId") Long spuId);

    /**
     * 查询库存
     * @param skuId
     * @return
     */
    @GetMapping("stock")
    public Stock queryStockBySkuId(@RequestParam("skuId") Long skuId);

    @GetMapping("sku/{skuId}")
    public Sku querySkuByStuId(@RequestParam("skuId") Long skuId);

}
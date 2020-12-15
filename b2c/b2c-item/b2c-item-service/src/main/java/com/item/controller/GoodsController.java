package com.item.controller;

import com.item.common.utils.PageResult;
import com.item.service.GoodsService;
import com.mr.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "good")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @GetMapping("/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "search", required = false) String search) {
        PageResult<SpuBo> result = this.goodsService.querySpuPage(page, rows, saleable, search);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(result);
    }
    @PostMapping
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo){
        try {
            this.goodsService.saveGoods(spuBo);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //修改回显 1
    @GetMapping("/spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> queryDetail(@PathVariable("spuId") Long spuId){
        SpuDetail  spuDetail = goodsService.queryDetail(spuId);
        return ResponseEntity.ok(spuDetail);
    }

    //修改回显 2
    @GetMapping("/skuList/{spuId}")
    public ResponseEntity<List<Sku>> querySku(@PathVariable("spuId") Long spuId){
        return ResponseEntity.ok(goodsService.querySku(spuId));
    }

    //修改
    @PutMapping
    public ResponseEntity<Void> updateGoods(@RequestBody  SpuBo spuBo){
        System.out.println(spuBo);
        goodsService.update(spuBo);
        return  new ResponseEntity<>(HttpStatus.CREATED);
    };
    //上下架
    @PutMapping("saleable")
    public ResponseEntity saleableGoods(@RequestBody Spu spu){
        goodsService.saleableGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).body("成功");
    }

    @GetMapping("spu/{spuId}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("spuId") Long spuId){
        Spu  spu = goodsService.querySpu(spuId);
        return ResponseEntity.ok(spu);
    }

    /**
     * 查询库存
     * @param skuId
     * @return
     */
    @GetMapping("stock")
    public ResponseEntity <Stock> queryStockByStuId(@RequestParam("skuId") Long skuId){
        Stock stock =goodsService.queryStockBySkuId(skuId);
        return ResponseEntity.ok(stock);
    }

    @GetMapping("sku/{skuId}")
    public ResponseEntity <Sku> querySkuByStuId(@RequestParam("skuId") Long skuId){
        Sku sku =goodsService.querySkuByStuId(skuId);
        return ResponseEntity.ok(sku);
    }
}

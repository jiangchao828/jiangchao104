package com.mr.controller;

import com.item.common.utils.PageResult;
import com.mr.GoodBo;
import com.mr.pojo.Goods;
import com.mr.service.GoodsService;
import com.mr.util.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("goods")
public class SearchController {
    @Autowired
    private GoodsService goodsService;
    /**
     * 商品搜索分页
     * @param goodBo
     * @return
     */
    @RequestMapping("page")
    public ResponseEntity<SearchResult<Goods>> queryGoodPage(@RequestBody GoodBo goodBo){
        System.out.println(goodBo.getKey());
        //返回分页对象
        SearchResult<Goods> pageResult = goodsService.searhGood( goodBo);
        //返回值要增加 分类筛选条件，和品牌筛选条件，规格筛选条件
        return ResponseEntity.ok(pageResult);
    }
}

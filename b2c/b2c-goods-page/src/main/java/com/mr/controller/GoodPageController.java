package com.mr.controller;

import com.mr.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller//用Controller 可以返回页面
@RequestMapping("item")
public class GoodPageController {

    @Autowired
    private PageService pageService;

    /**
     * 跳转商品详情
     * @param id
     * @return
     */
    @GetMapping("{id}.html")
    public String toGoodPageInfo(@PathVariable("id") Long id, ModelMap map){
        System.out.println("商品详情："+id);
        Map<String,Object> infoMap = pageService.queryItemInfo(id);
        map.putAll(infoMap);
        //返回页面
        return "item";
    }

}

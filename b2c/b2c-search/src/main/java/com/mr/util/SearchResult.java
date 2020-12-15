package com.mr.util;

import com.item.common.utils.PageResult;
import com.mr.pojo.Brand;
import com.mr.pojo.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class SearchResult<T> extends PageResult {

    //封装的返回结果，搜索专用
    private List<Category> categoryList;
    private List<Brand> brandList;
    private List<Map<String,Object>> specMapList;
    public SearchResult(Long total, List items, Long totalPage, List<Category> categoryList, List<Brand> brandList,List<Map<String,Object>> specMapList) {
        super(total, items, totalPage);
        this.categoryList = categoryList;
        this.brandList = brandList;
        this.specMapList=specMapList;
    }
}

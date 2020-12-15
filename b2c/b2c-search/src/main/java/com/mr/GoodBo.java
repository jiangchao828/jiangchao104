package com.mr;

import lombok.Data;

import java.util.Map;

@Data
public class GoodBo {
    private String key;// 搜索条件
    private Integer page=1;// 默认第1页
    private Integer rows=10;// 每页默认10条
    //接受规格
    private Map<String,String> filters;
}

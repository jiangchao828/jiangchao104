package com.item.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum  ExceptionEnums {

    PRICE_CANNOT_IS_NULL(400,"价格为空"),
    NAME_CANNOT_IS_NULL(500,"名称为空"),
    CATEGORY_NULL(400,"数据为空"),
    SAVE_UPDATE_CATEGORY_ERROR(500,"数据报错"),
    SELECT_KEY_CATEGORY_ERROR(404,"查询单条数据报错"),
    ADD_UPDATE_CATEGORY_ERROR(500,"数据内部错误");

    /**
     * 定义提示信息     状态码
     */
    private Integer code;
    private String message;
}

package com.item.common.exception;

import com.item.common.enums.ExceptionEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义异常类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MrException  extends  RuntimeException{

    private ExceptionEnums exceptionEnums;

}

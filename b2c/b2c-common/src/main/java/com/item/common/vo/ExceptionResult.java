package com.item.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.item.common.enums.ExceptionEnums;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

@Data
@NoArgsConstructor
public class ExceptionResult {

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd : HH-MM-SS")
    private Date timestamp;
    private Integer status;
    private String error;
    private String  message;
    private String path;

    public ExceptionResult(ExceptionEnums exceptionEnums) {
        this.status = exceptionEnums.getCode();
        this.message = exceptionEnums.getMessage();
        this.timestamp = new Date();
        this.error = "有问题了玩你吗🐎";
    }
}

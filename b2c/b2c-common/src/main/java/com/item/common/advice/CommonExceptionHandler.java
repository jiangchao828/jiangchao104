package com.item.common.advice;

import com.item.common.exception.MrException;
import com.item.common.vo.ExceptionResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice//标明为控制层通知类
public class CommonExceptionHandler {

	//捕捉异常
    @ExceptionHandler(MrException.class)
    //改为返回对象的格式
    public ResponseEntity<ExceptionResult> handleException(MrException e){
        System.out.println("有异常,我被执行了");
        //规定返回的状态码400，然后异常的提示信息设置到body中
         return ResponseEntity.status(e.getExceptionEnums().getCode()).body(new ExceptionResult(e.getExceptionEnums()));
    }
}
package com.stu.helloserve.exception;

import com.stu.helloserve.common.Result;
import com.stu.helloserve.common.ResultCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        // 1. 使用系统异常枚举，并附加异常消息
        Result<String> result = Result.error(ResultCode.SYSTEM_ERROR);
        result.setMsg(ResultCode.SYSTEM_ERROR.getMsg() + "：" + e.getMessage());
        return result;
    }
}
package com.cnc.cloud.aop;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理类
 * Created by linrx1 on 2018/3/26.
 */
@ControllerAdvice
public class GlobleExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Map<String, Object> exception(Exception e, WebRequest request){
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("msg", e.getMessage());
        return result;

    }
}

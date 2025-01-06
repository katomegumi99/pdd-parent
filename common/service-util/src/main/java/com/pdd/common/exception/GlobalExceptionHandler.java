package com.pdd.common.exception;

import com.pdd.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
// AOP形式
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class) // 异常处理器
    @ResponseBody // 返回json数据
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.fail(null);
    }

    /**
     * 自定义异常处理方法
     * @param e
     * @return
     */
    @ExceptionHandler(PddException.class)
    @ResponseBody
    public Result error(PddException e) {
        return Result.build(null,e.getCode(), e.getMessage());
    }
}

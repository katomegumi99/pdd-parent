package com.pdd.common.result;

import lombok.Data;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@Data
public class Result<T> {

    // 状态码
    private Integer code;

    // 信息
    private String message;

    // 数据
    private T data;

    // 构造私有化
    private Result() { }

    // 构建返回值
    // 设置数据，返回对象和方法
    public static<T> Result<T> build(T data, ResultCodeEnum resultCodeEnum) {
        // 创建Result对象，设置值，返回对象
        Result<T> result = new Result<>();
        // 判断返回结果是否需要数据
        if (data != null) {
            // 设置数据到result对象
            result.setData(data);
        }
        // 设置其它值
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());

        // 返回设置值之后的结果
        // 返回构建的结果
        return result;
    }

    // 构建返回值
    // 设置数据，返回对象和方法
    public static<T> Result<T> build(T data, Integer code, String message) {
        // 创建Result对象，设置值，返回对象
        Result<T> result = new Result<>();
        // 判断返回结果是否需要数据
        if (data != null) {
            // 设置数据到result对象
            result.setData(data);
        }
        // 设置其它值
        result.setCode(code);
        result.setMessage(message);

        // 返回设置值之后的结果
        // 返回构建的结果
        return result;
    }

    // 成功时返回的方法
    public static<T> Result<T> ok(T data) {
        Result<T> result = build(data, ResultCodeEnum.SUCCESS);
        return result;
    }

    // 失败时返回的方法
    public static<T> Result<T> fail(T data) {
        return build(data,ResultCodeEnum.FAIL);
    }
}

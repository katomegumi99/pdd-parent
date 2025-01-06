package com.pdd.common.exception;

import com.pdd.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * @author youzairichangdawang
 * @version 1.0
 * 自定义的全局异常处理
 */

@Data
public class PddException extends RuntimeException{

    // 异常的状态码
    private Integer code;

    /**
     * 通过状态码和错误信息判断对象
     * @param message
     * @param code
     */
    public PddException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    /**
     * 接收枚举类型的对象
     * @param resultCodeEnum
     */
    public PddException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "Exception{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}

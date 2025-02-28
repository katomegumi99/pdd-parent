package com.pdd.controller;

import com.pdd.common.result.Result;
import com.pdd.common.result.ResultCodeEnum;
import com.pdd.enums.PaymentType;
import com.pdd.service.PaymentInfoService;
import com.pdd.service.WeixinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 微信支付 API
 * </p>
 */
@Api(tags = "微信支付接口")
@RestController
@RequestMapping("/api/payment/weixin")
public class WeixinController {

    @Autowired
    private WeixinService weixinService;

    @Autowired
    private PaymentInfoService paymentInfoService;

    @GetMapping("/createJsapi/{orderNo}")
    public Result createJsapi(@PathVariable("orderNo") String orderNo) {
        Map<String, String> map = weixinService.createJsapi(orderNo);
        return Result.ok(map);
    }

    @ApiOperation("查询订单支付状态")
    @GetMapping("/queryPayStatus/{orderNo}")
    public Result queryPayStatus(@PathVariable("orderNo") String orderNo) {
        // 1 调用微信支付系统查询订单支付状态
        Map<String,String> resultMap = weixinService.queryPayStatus(orderNo);

        // 2 如果微信支付系统的返回值是null，则支付失败
        if (resultMap == null) {
            return Result.build(null, ResultCodeEnum.PAYMENT_FAIL);
        }

        // 3 如果收到微信支付的返回值，则支付成功
        if ("SUCCESS".equals(resultMap.get("trade_state"))) {
            //更改订单状态，处理支付结果
            // 3.1 支付成功，修改支付记录表中的支付状态为：已支付
            String out_trade_no = resultMap.get("out_trade_no");
            // 3.2 修改订单记录为已支付，进行减库存操作
            paymentInfoService.paySuccess(out_trade_no, resultMap);
            return Result.ok(null);
        }

        // 4 若状态为支付中，则进行等待
        return Result.build(null,ResultCodeEnum.PAYMENT_WAITING);
    }

}

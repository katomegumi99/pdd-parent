package com.pdd.service;

import com.pdd.model.order.PaymentInfo;

import java.util.Map;

public interface PaymentInfoService {

    // 根据 orderId 查询支付记录
    PaymentInfo getPaymentInfoByOrderNo(String orderNo);

    // 添加支付记录
    PaymentInfo savePaymentInfo(String orderNo);

    // 3.2 修改订单记录为已支付，进行减库存操作
    void paySuccess(String outTradeNo, Map<String, String> resultMap);
}

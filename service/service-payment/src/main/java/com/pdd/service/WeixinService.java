package com.pdd.service;

import java.util.Map;

public interface WeixinService {

    // 调用微信支付系统生成预付单
    Map<String, String> createJsapi(String orderNo);

    // 1 调用微信支付系统查询订单支付状态
    Map<String, String> queryPayStatus(String orderNo);
}

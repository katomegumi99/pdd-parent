package com.pdd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pdd.common.exception.PddException;
import com.pdd.common.result.ResultCodeEnum;
import com.pdd.enums.PaymentStatus;
import com.pdd.enums.PaymentType;
import com.pdd.mapper.PaymentInfoMapper;
import com.pdd.model.order.OrderInfo;
import com.pdd.model.order.PaymentInfo;
import com.pdd.mq.constant.MqConst;
import com.pdd.mq.service.RabbitService;
import com.pdd.order.client.OrderFeignClient;
import com.pdd.service.PaymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private RabbitService rabbitService;

    // 根据 orderId 查询支付记录
    @Override
    public PaymentInfo getPaymentInfoByOrderNo(String orderNo) {
        PaymentInfo paymentInfo = baseMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>()
                .eq(PaymentInfo::getOrderNo, orderNo));
        return paymentInfo;
    }

    // 添加支付记录
    @Override
    public PaymentInfo savePaymentInfo(String orderNo) {

        // 1 远程调用，根据orderNo查询订单信息
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderNo);
        if (orderInfo == null) {
            throw new PddException(ResultCodeEnum.DATA_ERROR);
        }
        // 2 封装到paymentInfo对象
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(PaymentType.WEIXIN);
        paymentInfo.setUserId(orderInfo.getUserId());
        paymentInfo.setOrderNo(orderInfo.getOrderNo());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID);
        String subject = "userId" + orderInfo.getUserId() + "下订单";
        paymentInfo.setSubject(subject);

        // 统一支付总金额0.01元
        paymentInfo.setTotalAmount(new BigDecimal("0.01"));

        // 3 调用方法实现添加
        baseMapper.insert(paymentInfo);
        return paymentInfo;
    }

    // 修改订单记录为已支付，进行减库存操作
    // 修改支付记录表状态为：已支付
    // outTradeNo 就是 orderNo
    @Override
    public void paySuccess(String outTradeNo, Map<String, String> resultMap) {
        // 1 查询当前订单在支付记录表中的状态是否为:已支付
        PaymentInfo paymentInfo = baseMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>()
                .eq(PaymentInfo::getOrderNo, outTradeNo));

        if (paymentInfo.getPaymentStatus() == PaymentStatus.UNPAID) {
            return;
        }

        // 2 若支付记录表中状态没有更新，则对其进行更新
        paymentInfo.setPaymentStatus(PaymentStatus.PAID);
        paymentInfo.setTradeNo(resultMap.get("transaction_id"));
        paymentInfo.setCallbackContent(resultMap.toString());
        baseMapper.updateById(paymentInfo);

        // TODO 3 整合rabbitMQ实现修改订单记录为已支付，并进行库存扣减
        rabbitService.sendMessage(MqConst.EXCHANGE_PAY_DIRECT, MqConst.ROUTING_PAY_SUCCESS, outTradeNo);

    }
}

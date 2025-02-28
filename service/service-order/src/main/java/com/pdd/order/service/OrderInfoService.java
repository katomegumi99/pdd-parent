package com.pdd.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pdd.model.order.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pdd.vo.order.OrderConfirmVo;
import com.pdd.vo.order.OrderSubmitVo;
import com.pdd.vo.order.OrderUserQueryVo;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author pdd
 * @since 2025-02-24
 */
public interface OrderInfoService extends IService<OrderInfo> {

    // 确认订单
    OrderConfirmVo confirmOrder();

    // 生成订单
    Long submitOrder(OrderSubmitVo orderParamVo);

    // 获取订单详情
    OrderInfo getOrderInfoById(Long orderId);

    // 根据orderNo查询订单信息
    OrderInfo getOrderInfoByOrderNo(String orderNo);

    // 订单支付成功，更新订单状态，扣减库存
    void orderPay(String orderNo);

    // 分页查询订单信息
    IPage<OrderInfo> getOrderInfoByUserIdPage(Page<OrderInfo> pageParam, OrderUserQueryVo orderUserQueryVo);
}

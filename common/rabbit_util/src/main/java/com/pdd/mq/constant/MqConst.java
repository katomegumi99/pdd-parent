package com.pdd.mq.constant;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
public class MqConst {
    /**
     * 消息补偿
     */
    public static final String MQ_KEY_PREFIX = "pdd.mq:list";
    public static final int RETRY_COUNT = 3;

    /**
     * 商品上下架
     */
    public static final String EXCHANGE_GOODS_DIRECT = "pdd.goods.direct";
    public static final String ROUTING_GOODS_UPPER = "pdd.goods.upper";
    public static final String ROUTING_GOODS_LOWER = "pdd.goods.lower";
    //队列
    public static final String QUEUE_GOODS_UPPER  = "pdd.goods.upper";
    public static final String QUEUE_GOODS_LOWER  = "pdd.goods.lower";

    /**
     * 团长上下线
     */
    public static final String EXCHANGE_LEADER_DIRECT = "pdd.leader.direct";
    public static final String ROUTING_LEADER_UPPER = "pdd.leader.upper";
    public static final String ROUTING_LEADER_LOWER = "pdd.leader.lower";
    //队列
    public static final String QUEUE_LEADER_UPPER  = "pdd.leader.upper";
    public static final String QUEUE_LEADER_LOWER  = "pdd.leader.lower";

    //订单
    public static final String EXCHANGE_ORDER_DIRECT = "pdd.order.direct";
    public static final String ROUTING_ROLLBACK_STOCK = "pdd.rollback.stock";
    public static final String ROUTING_MINUS_STOCK = "pdd.minus.stock";

    public static final String ROUTING_DELETE_CART = "pdd.delete.cart";
    //解锁普通商品库存
    public static final String QUEUE_ROLLBACK_STOCK = "pdd.rollback.stock";
    public static final String QUEUE_SECKILL_ROLLBACK_STOCK = "pdd.seckill.rollback.stock";
    public static final String QUEUE_MINUS_STOCK = "pdd.minus.stock";
    public static final String QUEUE_DELETE_CART = "pdd.delete.cart";

    //支付
    public static final String EXCHANGE_PAY_DIRECT = "pdd.pay.direct";
    public static final String ROUTING_PAY_SUCCESS = "pdd.pay.success";
    public static final String QUEUE_ORDER_PAY  = "pdd.order.pay";
    public static final String QUEUE_LEADER_BILL  = "pdd.leader.bill";

    //取消订单
    public static final String EXCHANGE_CANCEL_ORDER_DIRECT = "pdd.cancel.order.direct";
    public static final String ROUTING_CANCEL_ORDER = "pdd.cancel.order";
    //延迟取消订单队列
    public static final String QUEUE_CANCEL_ORDER  = "pdd.cancel.order";

    /**
     * 定时任务
     */
    public static final String EXCHANGE_DIRECT_TASK = "pdd.exchange.direct.task";
    public static final String ROUTING_TASK_23 = "pdd.task.23";
    //队列
    public static final String QUEUE_TASK_23  = "pdd.queue.task.23";
}

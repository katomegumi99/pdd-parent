package com.pdd.receiver;

import com.pdd.Service.CartInfoService;
import com.pdd.mq.constant.MqConst;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@Component
public class CartReceiver {

    @Resource
    private CartInfoService cartInfoService;

    /**
     * 删除购物车选项
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_DELETE_CART, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_ORDER_DIRECT),
            key = {MqConst.ROUTING_DELETE_CART}
    ))
    public void deleteCart(Long userId, Message message, Channel channel) throws IOException {
        if (userId != null) {
            cartInfoService.deleteCartChecked(userId);
        }
        // 手动确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}

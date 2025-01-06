package com.pdd.mq.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@Service
public class RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     * @param exchange 交换机
     * @param routingKey 路由键
     * @param message 消息
     * @return
     */
    public boolean sendMessage(String exchange, String routingKey, Object message) {
        //  调用发送数据的方法
        rabbitTemplate.convertAndSend(exchange, routingKey, message);

        return true;
    }
}

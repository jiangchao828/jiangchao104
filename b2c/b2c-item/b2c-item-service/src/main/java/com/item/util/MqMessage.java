package com.item.util;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqMessage {
    /**
     * 注入ampq工具类
     *
     */
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 发送工具类 指定交换机
     */
    public void sendMessage(String exchangeName,String routingKey , Object message){
        //发送消息
        this.amqpTemplate.convertAndSend(exchangeName,routingKey,message);
    }
}

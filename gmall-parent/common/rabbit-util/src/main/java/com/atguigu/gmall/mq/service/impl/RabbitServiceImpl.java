package com.atguigu.gmall.mq.service.impl;

import com.atguigu.gmall.mq.service.RabbitService;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Service
public class RabbitServiceImpl implements RabbitService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Override
    public void sendMessage(String exchange,String routingKey,String message ) {
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
    }

    @Override
    public void sendDeadMessage(String exchange, String routingKey, Object messageStr, Long ttl, TimeUnit timeUnit) {

        rabbitTemplate.convertAndSend(exchange,routingKey,messageStr, messagePostProcessor-> {
            messagePostProcessor.getMessageProperties().setDelay(Integer.parseInt(ttl*1000+""));
            return messagePostProcessor;
        });
    }

    @Override
    public void sendDelayMessage(String exchange_dead, String routing_1, String messageStr, Long l, TimeUnit timeUnit) {
         rabbitTemplate.convertAndSend(exchange_dead,routing_1,messageStr,messagePostProcessor ->{
             messagePostProcessor.getMessageProperties().setDelay(Integer.parseInt(l*1000+""));
             return messagePostProcessor;
         });
    }


}

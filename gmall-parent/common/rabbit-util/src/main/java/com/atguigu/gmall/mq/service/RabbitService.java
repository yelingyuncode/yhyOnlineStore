package com.atguigu.gmall.mq.service;

import java.util.concurrent.TimeUnit;

public interface RabbitService {
    void sendMessage(String exchange,String routingKey,String message);
    void sendDeadMessage(String exchange, String routingKey, Object messageStr, Long ttl, TimeUnit timeUnit);
    void sendDelayMessage(String exchange_dead,String routing_1,String messageStr,Long l,TimeUnit timeUnit);

}

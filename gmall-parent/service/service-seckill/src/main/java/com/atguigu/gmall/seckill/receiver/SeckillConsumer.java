package com.atguigu.gmall.seckill.receiver;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.model.activity.OrderRecode;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserRecode;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SeckillConsumer {
    @Autowired
    private RedisTemplate redisTemplate;
    //监听rabbit

    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(value = "exchange.direct.seckill.user",autoDelete ="false" ),
    value = @Queue(value = "queue.seckill.user",autoDelete = "false"),
    key = {"seckill.user"}))
    public void seckillConsumer(Channel channel, Message message,String json) throws IOException {
        UserRecode userRecode = JSON.parseObject(json, UserRecode.class);

        //去消耗并弹出库存
        Object seckillStock = redisTemplate.opsForList().rightPop("seckill:stock:" + userRecode.getSkuId());
        if ( seckillStock == null ){
            //发送请求通知redis发布  下架
            redisTemplate.convertAndSend("seckillpush",userRecode.getSkuId()+":0" );

        }else {
            //去生成预订单 ---> 应该去
            //String seckillSkuId = (String) seckillStock;
            OrderRecode orderRecode = new OrderRecode();
            orderRecode.setNum(1);
            orderRecode.setUserId(userRecode.getUserId());
            SeckillGoods seckillGoods = (SeckillGoods)redisTemplate.opsForHash().get("seckill:goods", userRecode.getSkuId()+"");
            orderRecode.setSeckillGoods(seckillGoods);
            //把预订单放到redis
            redisTemplate.opsForHash().put("seckill:orders", orderRecode.getUserId(), orderRecode);

        }


        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }
}

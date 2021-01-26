package com.atguigu.gmall.mq.receiver;

import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TestMqConsumer {
    //第一种注解配置式
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = "exchange-test",autoDelete = "false"),
    value = @Queue(value = "abc",autoDelete = "false"),
    key = {"dfe"}))
    public void consumerMessage(Channel channel, Message message,String str) throws IOException {
        //消费消息
        String messageStr = message.getBody().toString();
        System.out.println(messageStr);
        System.out.println(str);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag,false);

    }

  // @RabbitListener(queues = "queue.dead.1")
    public void consumerDelayMessage(Channel channel, Message message,String str) throws IOException {

        //        public static final String exchange_dead = "exchange.dead";
//        public static final String routing_dead_1 = "routing.dead.1";
//        public static final String routing_dead_2 = "routing.dead.2"; 死信队列
//        public static final String queue_dead_1 = "queue.dead.1";  死信
//        public static final String queue_dead_2 = "queue.dead.2";
        //消费消息
        String messageStr = message.getBody().toString();
        System.out.println(messageStr);
        System.out.println(str);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
//        channel.basicAck(deliveryTag,false);

    }
   // @RabbitListener(queues = "queue.dead.2")
    public void consumerDelayMessage1(Channel channel, Message message,String str) throws IOException {

        //        public static final String exchange_dead = "exchange.dead";
//        public static final String routing_dead_1 = "routing.dead.1";
//        public static final String routing_dead_2 = "routing.dead.2"; 死信队列
//        public static final String queue_dead_1 = "queue.dead.1";  死信
//        public static final String queue_dead_2 = "queue.dead.2";
        //消费消息
        String messageStr = message.getBody().toString();
        System.out.println(messageStr);
        System.out.println(str);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
//        channel.basicAck(deliveryTag,false);

    }
     @RabbitListener(queues = "queue.payment.pay")
    public void test1(Channel channel, Message message,String str) throws IOException {
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}

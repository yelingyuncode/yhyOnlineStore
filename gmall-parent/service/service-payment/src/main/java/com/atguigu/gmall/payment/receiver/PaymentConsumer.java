package com.atguigu.gmall.payment.receiver;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.mq.service.RabbitService;
import com.atguigu.gmall.payment.service.AlipaymentService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.java2d.pipe.ValidatePipe;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class PaymentConsumer {
    @Autowired
    private AlipaymentService alipaymentService;
    @Autowired
    private RabbitService rabbitService;

    @SneakyThrows
    @RabbitListener(queues = "queue.delay.11")
    public void paymentConsumer(Channel channel, Message message ,String json){

        Map<String, Object> map = new HashMap<>();
        Map jsonMap = JSON.parseObject(json, map.getClass());
        String out_trade_no  =  (String)jsonMap.get("out_trade_no");
        Integer count = (Integer) jsonMap.get("count");
        System.out.println("延迟队列检查支付结果，剩余检查次数："+count);
        //向阿里发送查询请求
        Map<String,Object> checkMap = alipaymentService.checkPayment(out_trade_no);
        count--;
        //拿到查询信息
       boolean success =  (boolean)checkMap.get("success");
        String trade_status = (String)checkMap.get("trade_status");
        //判断
        if(count>0){//发送机会还没过期
            //判断是否支付成功
            if(success == false || trade_status.equals("WAIT_BUYER_PAY")){
                //支付失败
                //设置数据 并且 重新设置消息队列
                System.out.println("当前支付状态为："+trade_status+"继续检查");
                jsonMap.put("count", count);
                rabbitService.sendDelayMessage("exchange.delay1", "routing.delay1", JSON.toJSONString(jsonMap), 20l, TimeUnit.SECONDS);

            }else {
                //支付成功
                System.out.println("当前支付状态"+trade_status+"不再检查");
           String status= "未支付";
           if(!status.equals("PAID")){
               //修改支付状态，发送支付成功队列，更新支付信息更新订单信息()

           }


            }
        }else{
            //无发送机会 且没有支付

            System.out.println("没有机会了 ， 需要重新下订单");
        }

     channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}

package com.atguigu.gmall.order.receiver;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.model.ware.WareOrderTask;
import com.atguigu.gmall.model.ware.WareOrderTaskDetail;
import com.atguigu.gmall.mq.service.RabbitService;
import com.atguigu.gmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class OrderConsumer {

    @Autowired
    private OrderService orderService;
    @Autowired
    private RabbitService rabbitService;

     @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(value = "exchange.payment.pay"),
                    value = @Queue(value = "queue.payment.pay",autoDelete = "false"),
                     key = "{routing.payment.pay}"))
    public void orderConsumer(Channel channel, Message message,String json) throws IOException {
         //拿到信息
         PaymentInfo paymentInfo = JSON.parseObject(json, PaymentInfo.class);
         //更新状态 已支付
         Long orderId1 = paymentInfo.getOrderId();//测试用  看里面是不是有orderid

         Long orderId =  orderService.updateOrderPay(paymentInfo);
       if (orderId != null && orderId>0){
           //拿到之后更新数据库
           OrderInfo orderInfo = orderService.getOrderInfoById(orderId);
           List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
           WareOrderTask wareOrderTask = new WareOrderTask();
           wareOrderTask.setDeliveryAddress(orderInfo.getDeliveryAddress());
           wareOrderTask.setPaymentWay(orderInfo.getPaymentWay());
           wareOrderTask.setCreateTime(new Date());
           wareOrderTask.setConsigneeTel(orderInfo.getConsigneeTel());
           wareOrderTask.setConsignee(orderInfo.getConsignee());
           wareOrderTask.setOrderId(orderId+"");
           ArrayList<WareOrderTaskDetail> wareOrderTaskDetails = new ArrayList<>();
           for (OrderDetail orderDetail : orderDetailList) {
               WareOrderTaskDetail wareOrderTaskDetail = new WareOrderTaskDetail();
               wareOrderTaskDetail.setSkuId(orderDetail.getSkuId()+"");
               wareOrderTaskDetail.setSkuName(orderDetail.getSkuName());
               wareOrderTaskDetail.setSkuNum(orderDetail.getSkuNum());
               wareOrderTaskDetails.add(wareOrderTaskDetail);
           }
           wareOrderTask.setDetails(wareOrderTaskDetails);
           //去下一个锁库存
           rabbitService.sendMessage("exchange.direct.ware.stock", "ware.stock", JSON.toJSONString(wareOrderTask));

       }


           //确认签收本次消息
         channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

     }

}

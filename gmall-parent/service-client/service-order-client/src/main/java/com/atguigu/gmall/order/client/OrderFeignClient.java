package com.atguigu.gmall.order.client;

import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("service-order")
public interface OrderFeignClient {

   @RequestMapping("api/order/trade")
    List<OrderDetail> trade();
    @RequestMapping("api/order/genTradeNo/{userId}")
    String genTradeNo(@PathVariable("userId") String userId);
    @RequestMapping("api/order/getOrderInfoById/{orderId}")
    OrderInfo getOrderInfoById(@PathVariable("orderId") Long orderId);
    @RequestMapping("api/order/submitOrder")
    String submitOrder(@RequestBody OrderInfo orderInfo);
}

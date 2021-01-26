package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;

public interface OrderService {


    boolean checkTradeNo(String userId, String tradeNo);

    String genTradeNo(String userId);

    String submitOrder(OrderInfo orderInfo);

    OrderInfo getOrderInfoById(Long orderId);

    Long updateOrderPay(PaymentInfo paymentInfo);
}

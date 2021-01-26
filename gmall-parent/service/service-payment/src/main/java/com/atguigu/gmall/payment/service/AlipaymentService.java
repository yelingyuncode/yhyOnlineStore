package com.atguigu.gmall.payment.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;

import java.util.Map;

public interface AlipaymentService {
    String aliPaySubmit(OrderInfo orderInfoById);

    void savePaymentInfo(PaymentInfo paymentInfo);

    void updatePaymentInfo(PaymentInfo paymentInfo);

    Map<String, Object> query(String out_trade_no);

    Map<String, Object> checkPayment(String out_trade_no);
}

package com.atguigu.gmall.payment.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.mq.service.RabbitService;
import com.atguigu.gmall.order.client.OrderFeignClient;
import com.atguigu.gmall.payment.service.AlipaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api/payment/alipay")
public class PaymentApiController {
    //http://api.gmall.com/api/payment/alipay/query/{out_trade_no}
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private AlipaymentService alipaymentService;
    @Autowired
    private RabbitService rabbitService;

    //订单状态查询接口<从阿里>
 @RequestMapping("query/{out_trade_no}")
 public Result query(@PathVariable("out_trade_no")String out_trade_no){

    Map<String,Object> map = alipaymentService.query(out_trade_no);
     return Result.ok(map);
 }

    @RequestMapping("submit/{orderId}")
    public String aliPaySubmit(@PathVariable("orderId") Long orderId){
        //应该查询用户信息提交给阿里
        OrderInfo orderInfoById = orderFeignClient.getOrderInfoById(orderId);
        //发送给阿里 返回一个跳转页面
        String form = alipaymentService.aliPaySubmit(orderInfoById);
        //在自己的数据库应该生成对应支付信息
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.toString());
        paymentInfo.setOutTradeNo(orderInfoById.getOutTradeNo());
        paymentInfo.setPaymentType("在线支付");
        paymentInfo.setOrderId(orderId);
        paymentInfo.setTotalAmount(orderInfoById.getTotalAmount());
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setSubject(orderInfoById.getOrderDetailList().get(0).getSkuName());
        alipaymentService.savePaymentInfo(paymentInfo);

        //发送延迟任务去 检查支付状态
        Map<String,Object> map = new HashMap<>();
        map.put("count", 5);
        map.put("out_trade_no", orderInfoById.getOutTradeNo());


        //发送mq信息
         rabbitService.sendDelayMessage("exchange.delay1","routing.delay1",JSON.toJSONString(map),20l, TimeUnit.SECONDS);



         return form;
    }

    @RequestMapping("callback/return")
    public String callbackReturn(HttpServletRequest request){
         //同步回调
        // 支付宝支付系统会回调到这个接口

        String trade_no = request.getParameter("trade_no");
        String out_trade_no = request.getParameter("out_trade_no");
        String callback_content = request.getQueryString();

        //接下来更新数据库，填入支付单号啥的
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(out_trade_no);
        paymentInfo.setPaymentStatus(PaymentStatus.PAID.toString());
        paymentInfo.setCallbackContent(callback_content);
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setTradeNo(trade_no);

           String status = "未支付";
            if (!status.equals("PAID")){
                alipaymentService.updatePaymentInfo(paymentInfo);
                //更新订单信息
                rabbitService.sendMessage("exchange.payment.pay", "{routing.payment.pay}", JSON.toJSONString(paymentInfo));

            }


        return "<form action=\"http://payment.gmall.com/paySuccess.html\">\n" +
                "</form>\n" +
                "<script>\n" +
                "document.forms[0].submit();\n" +
                "</script>";

    }

    @RequestMapping("callback/notify")
    public String callbackNotify(){
     //异步回调


        return null;
    }

    public static void main(String[] args) {
        boolean equals = new Integer(5).equals(5);
        System.out.println(equals);

    }
}

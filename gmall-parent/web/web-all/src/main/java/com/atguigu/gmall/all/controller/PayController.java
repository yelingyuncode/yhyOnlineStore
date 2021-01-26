package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@Controller
public class PayController {

    //http://payment.gmall.com/pay.html?orderId=4

    @Autowired
     private OrderFeignClient orderFeignClient;

    @RequestMapping("pay.html")
    public String pay(Long orderId, Model model){
       OrderInfo orderInfo =  orderFeignClient.getOrderInfoById(orderId);
       model.addAttribute("orderInfo", orderInfo);
        return "payment/pay";
    }
    //http://payment.gmall.com/paySuccess.html
    @RequestMapping("paySuccess.html")
        public String paySuccess(Model model){
            return "payment/success";
    }
}

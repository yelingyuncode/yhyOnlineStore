package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.client.cart.CartFeginClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("api/order")
@RestController
public class OrderApiController {
    @Autowired
    private CartFeginClient cartFeignClient;
    @Autowired
    private OrderService orderService;

    @RequestMapping("getOrderInfoById/{orderId}")
    OrderInfo getOrderInfoById(@PathVariable("orderId") Long orderId){
        OrderInfo orderInfo = orderService.getOrderInfoById(orderId);
        return orderInfo;
    }

    @RequestMapping("trade")
    List<OrderDetail> trade(HttpServletRequest request){
        String userId = request.getHeader("userId");
         List<CartInfo> cartInfoList =  cartFeignClient.cartList(userId);
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartInfo cartInfo : cartInfoList) {
            if (cartInfo.getIsChecked()==1){
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setSkuId(cartInfo.getSkuId());
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetail.setSkuNum(cartInfo.getSkuNum());
                orderDetail.setOrderPrice(cartInfo.getSkuPrice());
                orderDetails.add(orderDetail);
            }

        }
           return orderDetails;
    }

//    http://api.gmall.com/api/order/auth/submitOrder?tradeNo=null
    @RequestMapping("auth/submitOrder")
    public Result submitOrder(String tradeNo, @RequestBody OrderInfo orderInfo, Model model,HttpServletRequest request){
        String userId = request.getHeader("userId");
        //去cache验证tradeNo
           boolean b = orderService.checkTradeNo(userId,tradeNo);
        if (b){
            //存在  可以提交
            orderInfo.setUserId(Long.parseLong(userId));
            String orderId = orderService.submitOrder(orderInfo);
            return Result.ok(orderId);
        }else {
            //不一致  不能提交
             return Result.fail();

        }

    }
    @RequestMapping("submitOrder")
    String submitOrder(@RequestBody OrderInfo orderInfo){
        return orderService.submitOrder(orderInfo);

    }

    @RequestMapping("genTradeNo/{userId}")
    String genTradeNo(@PathVariable("userId")String userId){
       String tradeNo =  orderService.genTradeNo(userId);

       return tradeNo;
    }

}

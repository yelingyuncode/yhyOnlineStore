package com.atguigu.gmall.all.controller;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.order.client.OrderFeignClient;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class OrderController {
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private UserFeignClient userFeignClient;


    @RequestMapping("myOrder.html")
    public String myOrder() {
        return "order/myOrder";
    }

    @RequestMapping("trade.html")
    public String trade(Model model, HttpServletRequest request) {
        String userId = request.getHeader("userId");


        //订单详情
        List<OrderDetail> detailArrayList = orderFeignClient.trade();
       //地址集合
        List<UserAddress> userAddressList = userFeignClient.findUserAddressListByUserId(userId);

        BigDecimal totalAmount = getTotalAmount(detailArrayList);
        model.addAttribute("userAddressList", userAddressList);
        model.addAttribute("detailArrayList", detailArrayList);
        model.addAttribute("totalAmount", totalAmount);
       String tradeNo =  orderFeignClient.genTradeNo(userId);
        model.addAttribute("tradeNo",tradeNo);

        return "order/trade";
    }

    private BigDecimal getTotalAmount(List<OrderDetail> detailArrayList) {
        BigDecimal bigDecimal = new BigDecimal("0");
        for (OrderDetail orderDetail : detailArrayList) {
            BigDecimal orderPrice = orderDetail.getOrderPrice();
            bigDecimal = bigDecimal.add(orderPrice);
        }
                 return bigDecimal;
    }

}

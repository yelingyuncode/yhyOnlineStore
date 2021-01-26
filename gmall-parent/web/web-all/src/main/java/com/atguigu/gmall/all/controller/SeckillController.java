package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.activity.OrderRecode;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.seckill.client.SeckillFeignClient;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SeckillController {

    @Autowired
    private SeckillFeignClient seckillFeignClient;
    //http://activity.gmall.com/seckill/trade.html
    @Autowired
    private UserFeignClient userFeignClient;

    @RequestMapping("seckill/trade.html")
    public String seckillTrade(Model model,HttpServletRequest request){
        String userId = request.getHeader("userId");
        //拿到redis上的预订单
       OrderRecode orderRecode = seckillFeignClient.getOrderRecode(userId);
       //拿到用户地址
        List<UserAddress> userAddressListByUserId = userFeignClient.findUserAddressListByUserId(userId);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setDeliveryAddress(userAddressListByUserId.get(0).getUserAddress());
        orderInfo.setConsignee(userAddressListByUserId.get(0).getConsignee());
        orderInfo.setConsigneeTel(userAddressListByUserId.get(0).getPhoneNum());
        //订单详情
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();

        OrderDetail orderDetail = new OrderDetail();
        SeckillGoods seckillGoods = orderRecode.getSeckillGoods();
        orderDetail.setSkuNum(seckillGoods.getNum());
        orderDetail.setOrderPrice(seckillGoods.getPrice());
        orderDetail.setImgUrl(seckillGoods.getSkuDefaultImg());
        orderDetail.setSkuName(seckillGoods.getSkuName());
        orderDetail.setSkuId(seckillGoods.getSkuId());
        orderDetails.add(orderDetail);

        model.addAttribute("detailArrayList",orderDetails );
        model.addAttribute("userAddressList",userAddressListByUserId );
        model.addAttribute("order", orderInfo);
        model.addAttribute("totalAmount",seckillGoods.getPrice() );

  return "seckill/trade";
    }

    @RequestMapping("seckill/queue.html")
    public String seckill(String skuId,String skuIdStr,Model model, HttpServletRequest request){
        String userId = request.getHeader("userId");
        String skuIdStrCheck = MD5.encrypt(userId);
        if (null != skuIdStrCheck && skuIdStrCheck.equals(skuIdStr)){
            model.addAttribute("skuId",skuId );
            model.addAttribute("skuIdStr", skuIdStr);
            return "seckill/queue";
        }
        else {
            return "seckill/fail";
        }



    }

    //秒杀列表、
    @RequestMapping("seckill.html")
    public String index(Model model){
        List<SeckillGoods> list =  seckillFeignClient.getSeckillList();

        model.addAttribute("list", list);
        return "seckill/index";
    }
    //http://activity.gmall.com/seckill/30.html
    @RequestMapping("seckill/{skuId}.html")
    public String skuItem(@PathVariable Long skuId,Model model){
        SeckillGoods seckillGoods = seckillFeignClient.getSeckillItem(skuId);
        model.addAttribute("item", seckillGoods);
            return "seckill/item";
    }

}

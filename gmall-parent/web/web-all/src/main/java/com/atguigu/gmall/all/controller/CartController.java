package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.client.cart.CartFeginClient;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class CartController {
//http://cart.gmall.com/addCart.html?skuId=2&skuNum=1 加入购物车

    @Autowired
    CartFeginClient cartFeginClient;
    @RequestMapping("addCart.html")
    public String addCart( CartInfo cartInfo){

        String userId = "1";
        cartInfo.setUserId(userId);
        cartFeginClient.addCart(cartInfo);
        return "redirect:http://cart.gmall.com/cart/addCart.html?skuNum="+cartInfo.getSkuNum();
    }
    @RequestMapping("cart/cart.html")
    public String cartList(){
        String userId = "1";// 写死userId，通过sso系统获得
        return "cart/index";
    }
    @RequestMapping("cart.html")
    public String cartList1(){
        String userId = "1";// 写死userId，通过sso系统获得
        return "cart/index";
    }
}

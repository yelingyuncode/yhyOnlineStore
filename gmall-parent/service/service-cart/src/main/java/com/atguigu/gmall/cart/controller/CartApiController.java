package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("api/cart")
public class CartApiController {
    @Autowired
    CartService cartService;

    @RequestMapping("addCart")
    void addCart(@RequestBody CartInfo cartInfo){
        cartService.addCart(cartInfo);

    }
    @RequestMapping("cartList")
    public Result cartList(HttpServletRequest request){
        String userId = request.getHeader("userId");
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        List<CartInfo> list =  cartService.cartList(cartInfo);
        return  Result.ok(list);
    }
    @RequestMapping("cartList/{userId}")
    public List<CartInfo> cartList(@PathVariable("userId") String userId){
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        List<CartInfo> cartInfoList = cartService.cartList(cartInfo);
        return cartInfoList;
    }

}


package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;

public interface CartService {
    void addCart(CartInfo cartInfo);

    List<CartInfo> cartList(CartInfo cartInfo);
}

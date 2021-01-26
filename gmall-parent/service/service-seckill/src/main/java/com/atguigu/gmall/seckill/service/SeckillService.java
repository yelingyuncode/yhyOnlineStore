package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.model.activity.OrderRecode;
import com.atguigu.gmall.model.activity.SeckillGoods;

import java.util.List;
import java.util.Map;

public interface SeckillService  {
    List<SeckillGoods> getSeckillList();

    SeckillGoods getSeckillItem(Long skuId);

    void putGoods(String skuId);

    Map<String, Object> seckillOrder(String userId, String skuId);

    String checkTrueOrder(String userId);

    OrderRecode checkOrderRecode(String userId);

    void deleteOrderRecode(String userId);

    void generateOrderUsers(String userId, String orderId);
}

package com.atguigu.gmall.seckill.client;

import com.atguigu.gmall.model.activity.OrderRecode;
import com.atguigu.gmall.model.activity.SeckillGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(value = "service-activity")
public interface SeckillFeignClient {
    @RequestMapping("api/activity/seckill/findAll")
    List<SeckillGoods> getSeckillList();

    @RequestMapping("api/activity/seckill/getSeckillItem/{skuId}")
    SeckillGoods getSeckillItem(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/activity/seckill/getOrderRecode/{userId}")
    OrderRecode getOrderRecode(@PathVariable("userId") String userId);
}

package com.atguigu.gmall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.model.activity.OrderRecode;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.user.UserRecode;
import com.atguigu.gmall.mq.service.RabbitService;
import com.atguigu.gmall.seckill.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.seckill.service.SeckillService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitService rabbitService;

    @Override
    public List<SeckillGoods> getSeckillList() {

        List<SeckillGoods> list  =  (List<SeckillGoods>)redisTemplate.opsForHash().values("seckill:goods");

        return list;

    }

    @Override
    public SeckillGoods getSeckillItem(Long skuId) {

        SeckillGoods  seckillGoods = (SeckillGoods)redisTemplate.opsForHash().get("seckill:goods", skuId+"");
        return seckillGoods;
    }

    @Override
    public void putGoods(String skuId) {
        //先通过skuId查询数据
        QueryWrapper<SeckillGoods> seckillGoodsQueryWrapper = new QueryWrapper<>();
                    seckillGoodsQueryWrapper.eq("sku_id", skuId);
        SeckillGoods seckillGood = seckillGoodsMapper.selectOne(seckillGoodsQueryWrapper);
        //把数据放到redis上面
        for (int i = 0; i < seckillGood.getStockCount();i++){
            //发布到redis中的list里面
            redisTemplate.opsForList().leftPush("seckill:stock:"+seckillGood.getSkuId(), seckillGood.getSkuId());
        }
        //把商品详情放到redis
        redisTemplate.opsForHash().put("seckill:goods", seckillGood.getSkuId()+"", seckillGood);
        //通知每个服务  即发布消息
        redisTemplate.convertAndSend("seckillpush", seckillGood.getSkuId()+":1");
    }

    @Override
    public Map<String, Object> seckillOrder(String userId, String skuId) {

        //先判断该用户是否之前生成过订单
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("seckill:user" + userId, 1, 60, TimeUnit.SECONDS);
        HashMap<String, Object> map = new HashMap<>();
        if (aBoolean){
            //无用户 可以下单
            UserRecode userRecode = new UserRecode();
            userRecode.setSkuId(Long.parseLong(skuId));
            userRecode.setUserId(userId);
            rabbitService.sendMessage("exchange.direct.seckill.user", "seckill.user", JSON.toJSONString(userRecode));
            map.put("success", true);
        }else {
            //该用户已经下过单了  不准下单
            map.put("success", false);
        }
        return map;
    }

    @Override
    public String checkTrueOrder(String userId) {
        String o = (String)redisTemplate.opsForHash().get("seckill:orders:users", userId);
        return o;
    }

    @Override
    public OrderRecode checkOrderRecode(String userId) {
        OrderRecode o = (OrderRecode)redisTemplate.opsForHash().get("seckill:orders", userId);
        return o;
    }

    @Override
    public void deleteOrderRecode(String userId) {
        redisTemplate.opsForHash().delete("seckill:orders", userId);
    }

    @Override
    public void generateOrderUsers(String userId, String orderId) {
      redisTemplate.opsForHash().put("seckill:orders:users", userId, orderId);
    }

}


package com.atguigu.gmall.seckill.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.activity.OrderRecode;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.user.UserRecode;
import com.atguigu.gmall.mq.service.RabbitService;
import com.atguigu.gmall.order.client.OrderFeignClient;
import com.atguigu.gmall.seckill.service.SeckillService;
import com.atguigu.gmall.seckill.util.CacheHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequestMapping("api/activity/seckill")
@RestController
public class SeckillApiController {
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private OrderFeignClient orderFeignClient;
    // http://api.gmall.com/api/activity/seckill/auth/submitOrder
    @RequestMapping("auth/submitOrder")
    public Result submitOrder( @RequestBody OrderInfo orderInfo, HttpServletRequest request){
        String userId = request.getHeader("userId");
        String orderId = orderFeignClient.submitOrder(orderInfo);
        //去删除redis订单
        seckillService.deleteOrderRecode(userId);
        //生成已提交用户订单
        seckillService.generateOrderUsers(userId,orderId);
        return Result.ok(orderId);
    }


    @RequestMapping("getOrderRecode/{userId}")
    OrderRecode getOrderRecode(@PathVariable("userId") String userId){

       return  (OrderRecode)redisTemplate.opsForHash().get("seckill:orders", userId);

    }

    //http://api.gmall.com/api/activity/seckill/auth/checkOrder/30
    @RequestMapping("auth/checkOrder/{skuId}")
    public Result checkOrder(@PathVariable("skuId")String skuId,HttpServletRequest request){
        String userId = request.getHeader("userId");
        //是否已经下单
        String orderId  = seckillService.checkTrueOrder(userId);
        if (orderId != null){
            return Result.build(orderId, ResultCodeEnum.SECKILL_ORDER_SUCCESS);
        }
        //是否已经下预订单
        OrderRecode orderRecode = seckillService.checkOrderRecode(userId);
        if (orderRecode != null){
            return Result.build(orderRecode, ResultCodeEnum.SECKILL_SUCCESS);
        }
        //是否售罄
      String skuIdFromCacheHelper = (String) CacheHelper.get(skuId);
        if (skuIdFromCacheHelper == null || skuIdFromCacheHelper.equals("0")){
            return Result.build(orderRecode, ResultCodeEnum.SECKILL_FINISH);
        }

        //是否在排队
        return Result.build(null, ResultCodeEnum.SECKILL_RUN);
    }
    @RequestMapping("/findAll")
    List<SeckillGoods> getSeckillList(){
        List<SeckillGoods> list  =  seckillService.getSeckillList();
        return list;
    }
    @RequestMapping("getSeckillItem/{skuId}")
    SeckillGoods getSeckillItem(@PathVariable("skuId") Long skuId){
        SeckillGoods seckillGoods =   seckillService.getSeckillItem(skuId);
        return seckillGoods;
    }
    //异步请求生成验证码 去验证必须由页面点击过来的秒杀
    //'/auth/getSeckillSkuIdStr/' + skuId
    @RequestMapping("auth/getSeckillSkuIdStr/{skuId}")
    public Result getSeckillSkuIdStr(@PathVariable("skuId") String skuId, HttpServletRequest request){
        String userId = request.getHeader("userId");
        //生成秒杀码的前提是必须能够秒杀 即有库存
        //先从缓存map里面拿到数据 看有没有库存
        String status = (String)CacheHelper.get(skuId);
        if (status!=null && status.equals("1")){
            //有库存  生成验证码
            String skuIdStr = MD5.encrypt(userId);
            return Result.ok(skuIdStr);
        }else {
            //无库存
            return Result.fail();
        }
    }


    //生成预订单  发送mq
   //api.gmall.com/api/activity/seckill/auth/seckillOrder/30?skuIdStr=
    @RequestMapping("auth/seckillOrder/{skuId}")
    public Result seckillOrder(@PathVariable("skuId") String skuId, String skuIdStr,HttpServletRequest request){
        String userId = request.getHeader("userId");

       Map<String,Object> map =  seckillService.seckillOrder(userId,skuId);
       return Result.ok();

    }





    //无页面，网址把商品推送到redis
    @RequestMapping("putGoods/{skuId}")
    public Result putGoods(@PathVariable("skuId")String skuId){
        seckillService.putGoods(skuId);
        return Result.ok();
    }

}







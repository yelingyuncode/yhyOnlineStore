package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.cart.service.mapper.CartMapper;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public void addCart(CartInfo cartInfo) {
        String userId = cartInfo.getUserId();
        Integer skuNum = cartInfo.getSkuNum();
        Long skuId = cartInfo.getSkuId();
        QueryWrapper<CartInfo> cartInfoQueryWrapper = new QueryWrapper<>();
        cartInfoQueryWrapper.eq("user_id",userId );
        cartInfoQueryWrapper.eq("sku_id", skuId);
        CartInfo cartInfoFromDb = cartMapper.selectOne(cartInfoQueryWrapper);
        if (cartInfoFromDb == null) {
            SkuInfo skuInfoById = productFeignClient.getSkuInfoById(cartInfo.getSkuId());
            cartInfo.setCartPrice(skuInfoById.getPrice().multiply(new BigDecimal(cartInfo.getSkuNum())));
            cartInfo.setImgUrl(skuInfoById.getSkuDefaultImg());
            cartInfo.setSkuName(skuInfoById.getSkuName());
            cartInfo.setUserId(userId);
            cartInfo.setIsChecked(1);
            cartMapper.insert(cartInfo);
        }else {
            cartInfo= cartInfoFromDb;
            cartInfo.setSkuNum(cartInfoFromDb.getSkuNum()+skuNum);
            cartMapper.update(cartInfo, cartInfoQueryWrapper);
        }
        //同步缓存
        redisTemplate.opsForHash().put(RedisConst.USER_KEY_PREFIX+cartInfo.getUserId()+RedisConst.USER_CART_KEY_SUFFIX,
                cartInfo.getSkuId()+"",cartInfo);
    }

    @Override
//     @GmallCache
    public List<CartInfo> cartList(CartInfo cartInfo) {
        List<CartInfo> list= (List<CartInfo>)redisTemplate.opsForHash().values(RedisConst.USER_KEY_PREFIX + cartInfo.getUserId()+"" + RedisConst.USER_CART_KEY_SUFFIX);
        //查询数据库
        if (null == list &&list.size()<=0 ){
            HashMap<String, Object> cacheMap = new HashMap<>();
            String userId = cartInfo.getUserId();
            QueryWrapper<CartInfo> cartInfoQueryWrapper = new QueryWrapper<>();
            cartInfoQueryWrapper.eq("user_id", userId);
            list = cartMapper.selectList(cartInfoQueryWrapper);
            if (list != null && list.size() >0){

                for (CartInfo info : list) {
                    cacheMap.put(info.getSkuId()+"", info);
                }
                //同步缓存

                redisTemplate.opsForHash().putAll(RedisConst.USER_KEY_PREFIX + cartInfo.getUserId() + RedisConst.USER_CART_KEY_SUFFIX,cacheMap);
            }

        }


        if (list != null && list.size() >0){
            for (CartInfo info : list) {
                BigDecimal price = productFeignClient.getPrice(info.getSkuId());
                info.setSkuPrice(price);

            }
        }
        return list;
    }
}

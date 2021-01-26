package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.mapper.UserMapper;
import com.atguigu.gmall.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserAddressMapper userAddressMapper;
    @Override
    public Map<String, Object> verify(String token) {
        HashMap<String, Object> map = new HashMap<>();
        //从nosql取数据
        UserInfo userInfo= (UserInfo)redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_KEY_PREFIX + token);
        map.put("user", userInfo);
        return map;
    }

    @Override
    public UserInfo login(UserInfo userInfo) {
            //先去数据库查询
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("passwd",  MD5.encrypt(userInfo.getPasswd()));
        userInfoQueryWrapper.eq("login_name",userInfo.getLoginName());
         userInfo = userMapper.selectOne(userInfoQueryWrapper);
         if (userInfo == null){
             return null;
         }else {
             //登录成功 查到数据
             //生成token 然后再放入
             String string = UUID.randomUUID().toString();
             userInfo.setToken(string);
             //同步到redis
             redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX + string, userInfo);
         }
        return userInfo;
    }

    @Override
    public List<UserAddress> findUserAddressListByUserId(String userId) {
        QueryWrapper<UserAddress> userAddressQueryWrapper = new QueryWrapper<>();
        userAddressQueryWrapper.eq("user_id", userId);
        List<UserAddress> userAddressList = userAddressMapper.selectList(userAddressQueryWrapper);


        return userAddressList;
    }
}

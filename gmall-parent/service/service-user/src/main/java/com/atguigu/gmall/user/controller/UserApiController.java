package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/user/passport")
public class UserApiController {
    @Autowired
    private UserService userService;

    @RequestMapping("verify/{token}")
    Map<String, Object> verify(@PathVariable("token") String token){
        Map<String, Object> map = userService.verify(token);

        return map;

    }
    @RequestMapping("login")
    public Result login(@RequestBody UserInfo userInfo){
        userInfo= userService.login(userInfo);
        if (userInfo != null){
            return Result.ok(userInfo);
        }
        return Result.fail();

    }


    @RequestMapping("findUserAddressListByUserId/{userId}")
    List<UserAddress> findUserAddressListByUserId(@PathVariable("userId") String userId){
       return userService.findUserAddressListByUserId(userId);

    }
}

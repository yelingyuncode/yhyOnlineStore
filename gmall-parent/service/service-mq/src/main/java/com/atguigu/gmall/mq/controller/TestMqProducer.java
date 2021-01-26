package com.atguigu.gmall.mq.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.mq.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestMqProducer {
    @Autowired
    private RabbitService rabbitService;

    @RequestMapping("api/mq/testSendMessage/{message}")
    public Result sendMessage(@PathVariable("message")String message) {

        rabbitService.sendMessage("exchange-test","dfe",message);
        return Result.ok();
    }
    @RequestMapping("api/mq/sendDelayMessage/{message}")
    public Result sendDelayMessage(@PathVariable("message") String message){
        int time = 10000;



        return Result.ok();
    }
}

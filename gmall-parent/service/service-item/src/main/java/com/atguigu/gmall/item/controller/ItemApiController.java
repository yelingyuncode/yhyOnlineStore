package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("api/item")
public class ItemApiController {
    @Autowired
    ItemService itemService;

    @RequestMapping("getItem/{skuId}")
    Map<String,Object> getItem(@PathVariable("skuId") Long skuId, HttpServletRequest request){
        String userId = request.getHeader("userId");
       Map<String,Object> map =  itemService.getItem(skuId);

       return map;
    }
}

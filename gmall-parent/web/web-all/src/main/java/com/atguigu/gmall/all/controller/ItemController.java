package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.item.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ItemController {

    @Autowired
    private ItemFeignClient itemFeignClient;
   @RequestMapping("{skuId}.html")
    public String item(@PathVariable("skuId") Long skuId, Model model, HttpServletRequest request){
       String userId = request.getHeader("userId");

       Map<String, Object> map = new HashMap<>();
       map= itemFeignClient.getItem(skuId);
       model.addAllAttributes(map);
       return "item/index";
   }





}

package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.item.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ItemApiController {

    @Autowired
    private ItemFeignClient itemFeignClient;
   @RequestMapping("{skuId}.html")
    public String item(@PathVariable("skuId") long skuId, Model model){
       Map<String, Object> map = new HashMap<>();
       map= itemFeignClient.getItem(skuId);
       model.addAllAttributes(map);
       return "item/index";
   }





}

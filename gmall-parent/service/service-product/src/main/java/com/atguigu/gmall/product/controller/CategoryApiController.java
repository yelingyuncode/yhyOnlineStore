package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin/product")
public class CategoryApiController {
    @RequestMapping("getCategory1")
    public Result list(){
        return Result.ok();
    }
}

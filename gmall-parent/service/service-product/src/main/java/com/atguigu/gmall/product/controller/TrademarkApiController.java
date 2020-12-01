package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class TrademarkApiController {
     @Autowired
     private BaseTrademarkService baseTrademarkService;
//http://api.gmall.com/admin/product/baseTrademark/{page}/{limit}
    @RequestMapping("baseTrademark/{page}/{limit}")
    public Result baseTrademark(@PathVariable long page,
                                @PathVariable long limit){
        Page<BaseTrademark> page1 = new Page<>(page,limit);
       IPage<BaseTrademark> baseTrademarkIPage =  baseTrademarkService.baseTrademark(page1);
        return Result.ok(baseTrademarkIPage);
    }



    //http://localhost:8080/admin/product/baseTrademark/getTrademarkList
    @RequestMapping("baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        List<BaseTrademark> baseTrademarkList =  baseTrademarkService.getTrademarkList();
        return Result.ok(baseTrademarkList);
    }

}

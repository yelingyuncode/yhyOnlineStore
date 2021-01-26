package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class SkuApiController {
    //http://api.gmall.com/admin/product/saveSkuInfo
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private ListFeignClient listFeignClient;
    @RequestMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){

        skuInfoService.saveSkuInfo(skuInfo);

        return Result.ok();
    }
    //http://api.gmall.com/admin/product/list/{page}/{limit}
    @RequestMapping("list/{page}/{limit}")
    public Result list(@PathVariable long page,
                       @PathVariable long limit){
        IPage<SkuInfo> page1 = new Page<>(page, limit);
        IPage<SkuInfo> skuInfoIPage=skuInfoService.list(page1);

     return Result.ok(skuInfoIPage);
    }

//    http://api.gmall.com/admin/product/onSale/{skuId} 上架
    @RequestMapping("onSale/{skuId}")
    public Result onSale(@PathVariable long skuId){
        // todo  es相关操作没做
        skuInfoService.onSale(skuId);
        listFeignClient.onSale(skuId);
        return Result.ok();

    }
//http://api.gmall.com/admin/product/cancelSale/{skuId}下架
    @RequestMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable long skuId){
        // todo  es相关操作没做
        skuInfoService.cancelSale(skuId);
        listFeignClient.cancelSale(skuId);
        return Result.ok();
    }


}

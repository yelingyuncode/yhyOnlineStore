package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.SpuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
@CrossOrigin
public class SpuApiController {
    //http://api.gmall.com/admin/product/ {page}/{limit}?category3Id=61
    @Autowired
    private SpuService spuService;
    @Autowired
    private BaseSaleAttrService baseSaleAttrService;
    @RequestMapping("{pageNo}/{limit}")
    public Result pageSpuList(@PathVariable long pageNo,
                              @PathVariable long limit,
                              long category3Id){
        IPage<SpuInfo> page = new Page(pageNo, limit);
       IPage<SpuInfo> spuInfos = spuService.pageSpuList(page,category3Id);
        return Result.ok(spuInfos);


    }
    //http://localhost:8080/admin/product/baseSaleAttrList
    @RequestMapping("baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrList =  baseSaleAttrService.baseSaleAttrList();
        return Result.ok(baseSaleAttrList);
    }
    //http://localhost:8080/admin/product/saveSpuInfo
    @RequestMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
       boolean x = spuService.saveSpuInfo(spuInfo);
        return Result.ok("添加成功");
    }

}

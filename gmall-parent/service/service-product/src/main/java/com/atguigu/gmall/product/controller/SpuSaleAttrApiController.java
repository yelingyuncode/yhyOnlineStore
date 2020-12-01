package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class SpuSaleAttrApiController {
    @Autowired
    private SpuSaleAttrService spuSaleAttrService;

//    http://api.gmall.com/admin/product/spuSaleAttrList/{spuId}
    @RequestMapping("spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable long spuId){
      List<SpuSaleAttr> saleAttrs =  spuSaleAttrService.spuSaleAttrList(spuId);
        return Result.ok(saleAttrs);
    }

}

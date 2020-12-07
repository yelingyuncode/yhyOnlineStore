package com.atguigu.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.BaseCategoryViewService;
import com.atguigu.gmall.product.service.CategoryService;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.mapper.SpuSaleAttrMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/product")
public class ProductApiController {
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;
    @Autowired
    BaseCategoryViewService baseCategoryViewService;
    @Autowired
    private CategoryService categoryService;

    @RequestMapping("getPrice/{skuId}")
    BigDecimal getPrice(@PathVariable("skuId") Long skuId){

        BigDecimal bigDecimal = skuInfoService.getPrice(skuId);
        return bigDecimal;
    }
    @RequestMapping("getSkuInfoById/{skuId}")
    SkuInfo getSkuInfoById(@PathVariable Long skuId){
       SkuInfo skuInfo = skuInfoService.getSkuInfoById(skuId);
        return skuInfo;
    }
    @RequestMapping("getSpuSaleAttrListBySpuId/{spuId}/{skuId}")
    List<SpuSaleAttr> getSpuSaleAttrListBySpuId(@PathVariable("spuId") Long spuId,
                                                @PathVariable("skuId") Long skuId){
        List<SpuSaleAttr> saleAttrs = spuSaleAttrService.getSpuSaleAttrListBySpuId(spuId,skuId);
        return saleAttrs;
    }
    @RequestMapping("/getCategoryViewByCategory3Id/{category3Id}")
    BaseCategoryView getCategoryViewByCategory3Id(@PathVariable("category3Id") Long category3Id){
        BaseCategoryView baseCategoryView = baseCategoryViewService.getCategoryViewByCategory3Id(category3Id);

        return baseCategoryView;
    }
    @RequestMapping("getSaleAttrValuesBySpu/{spuId}")
    Map<String, Long> getSaleAttrValuesBySpu(@PathVariable("spuId") Long spuId){
       Map<String,Long> map = spuSaleAttrService.getSaleAttrValuesBySpu(spuId);
        return map;
    }

    @RequestMapping("getBaseCategoryList")
    List<JSONObject> getBaseCategoryList(){
        List<JSONObject> list = categoryService.getBaseCategoryList();
        return list;
    }
}

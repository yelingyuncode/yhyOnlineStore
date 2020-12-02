package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {


    @Autowired
    ProductFeignClient productFeignClient;
    @Override
    public Map<String, Object> getItem(long skuId) {
        HashMap<String, Object> map = new HashMap<>();
        BigDecimal bigDecimal = productFeignClient.getPrice(skuId);
        map.put("price",bigDecimal);
       SkuInfo skuInfo =  productFeignClient.getSkuInfoById(skuId);
       map.put("skuInfo",skuInfo);
       List<SpuSaleAttr> saleAttrs =  productFeignClient.getSpuSaleAttrListBySpuId(skuInfo.getSpuId());
        map.put("spuSaleAttrList",saleAttrs);
       BaseCategoryView baseCategoryView =  productFeignClient.getCategoryViewByCategory3Id(skuInfo.getCategory3Id());
       map.put("categoryView",baseCategoryView);
        return map;
    }
}

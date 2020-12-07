package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Override
    public Map<String, Object> getItem(Long skuId) {
        long start = System.currentTimeMillis();
        //单线程
//        Map<String, Object> map = getItemSingle(skuId);
        //多线程
        Map<String, Object> map = getItemByMultiThread(skuId);
        long end = System.currentTimeMillis();
        System.out.println("运行时间：" + (end-start));
        return map;
    }
    private Map<String, Object> getItemSingle(Long skuId) {
        HashMap<String, Object> map = new HashMap<>();
        BigDecimal bigDecimal = productFeignClient.getPrice(skuId);
        map.put("price",bigDecimal);
        SkuInfo skuInfo =  productFeignClient.getSkuInfoById(skuId);
        map.put("skuInfo",skuInfo);
        List<SpuSaleAttr> saleAttrs =  productFeignClient.getSpuSaleAttrListBySpuId(skuInfo.getSpuId(),skuId);
        map.put("spuSaleAttrList",saleAttrs);
        BaseCategoryView baseCategoryView =  productFeignClient.getCategoryViewByCategory3Id(skuInfo.getCategory3Id());
        map.put("categoryView",baseCategoryView);
        //根据Spuid查到对应关系hash表给前台
        Map<String,Long> jsonMap =productFeignClient.getSaleAttrValuesBySpu(skuInfo.getSpuId());
        String json = JSON.toJSONString(jsonMap);
        map.put("valuesSkuJson",json);
        return map;
    }

    private Map<String, Object> getItemByMultiThread(Long skuId) {
        HashMap<String, Object> map = new HashMap<>();
        CompletableFuture<SkuInfo> completableFutureSkuInfo = CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                SkuInfo skuInfo =  productFeignClient.getSkuInfoById(skuId);
                map.put("skuInfo",skuInfo);
                return skuInfo;
            }
        },threadPoolExecutor);
        //价格
        CompletableFuture<Void> completableFuturePrice = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                BigDecimal bigDecimal = productFeignClient.getPrice(skuId);
                map.put("price",bigDecimal);
            }
        },threadPoolExecutor);
     //属性值
        CompletableFuture<Void> completableFutureSaleAttrs =completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                List<SpuSaleAttr> saleAttrs =  productFeignClient.getSpuSaleAttrListBySpuId(skuInfo.getSpuId(),skuId);
                map.put("spuSaleAttrList",saleAttrs);
            }
        } ,threadPoolExecutor);
        //类目
        CompletableFuture<Void> completableFutureBaseCategoryView = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                BaseCategoryView baseCategoryView =  productFeignClient.getCategoryViewByCategory3Id(skuInfo.getCategory3Id());
                map.put("categoryView",baseCategoryView);
            }
        },threadPoolExecutor);
      //前台对应表
        CompletableFuture<Void> completableFutureJson = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                //根据Spuid查到对应关系hash表给前台
                Map<String,Long> jsonMap =productFeignClient.getSaleAttrValuesBySpu(skuInfo.getSpuId());
                String json = JSON.toJSONString(jsonMap);
                map.put("valuesSkuJson",json);
            }
        },threadPoolExecutor);
     CompletableFuture.allOf(completableFutureSkuInfo,completableFuturePrice,completableFutureBaseCategoryView,completableFutureJson,completableFutureSaleAttrs).join();
        return map;
    }
}

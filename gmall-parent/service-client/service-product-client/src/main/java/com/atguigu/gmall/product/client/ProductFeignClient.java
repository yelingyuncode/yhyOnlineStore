package com.atguigu.gmall.product.client;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(value = "service-product")
public interface ProductFeignClient {

    @RequestMapping("api/product/getPrice/{skuId}")
    BigDecimal getPrice(@PathVariable("skuId") Long skuId);
    @RequestMapping("api/product/getSkuInfoById/{skuId}")
    SkuInfo getSkuInfoById(@PathVariable Long skuId);
    @RequestMapping("api/product/getSpuSaleAttrListBySpuId/{spuId}/{skuId}")
    List<SpuSaleAttr> getSpuSaleAttrListBySpuId(@PathVariable("spuId") Long spuId,@PathVariable("skuId") Long skuId);
    @RequestMapping("api/product/getCategoryViewByCategory3Id/{category3Id}")
    BaseCategoryView getCategoryViewByCategory3Id(@PathVariable("category3Id") Long category3Id);
    @RequestMapping("api/product/getSaleAttrValuesBySpu/{spuId}")
    Map<String, Long> getSaleAttrValuesBySpu(@PathVariable("spuId") Long spuId);
}

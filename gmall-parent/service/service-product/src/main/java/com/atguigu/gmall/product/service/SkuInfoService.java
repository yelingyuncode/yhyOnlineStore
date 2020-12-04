package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.math.BigDecimal;

public interface SkuInfoService {
    IPage<SkuInfo> list(IPage<SkuInfo> page1);

    void saveSkuInfo(SkuInfo skuInfo);

    void onSale(long skuId);

    void cancelSale(long skuId);

    BigDecimal getPrice(Long skuId);

    SkuInfo getSkuInfoById(Long skuId);
}

package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuSaleAttr;

import java.util.List;
import java.util.Map;

public interface SpuSaleAttrService {
    List<SpuSaleAttr> spuSaleAttrList(long spuId);

    Map<String, Long> getSaleAttrValuesBySpu(Long spuId);

    List<SpuSaleAttr> getSpuSaleAttrListBySpuId(Long spuId,Long skuId);
}

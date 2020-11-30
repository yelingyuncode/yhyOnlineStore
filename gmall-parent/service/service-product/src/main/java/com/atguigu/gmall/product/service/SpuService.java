package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;


public interface SpuService {
    IPage<SpuInfo> pageSpuList(IPage<SpuInfo> page, long category3Id);

    boolean saveSpuInfo(SpuInfo spuInfo);
}

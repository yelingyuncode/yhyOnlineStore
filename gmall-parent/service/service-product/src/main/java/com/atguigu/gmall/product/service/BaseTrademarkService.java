package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface BaseTrademarkService {
    List<BaseTrademark> getTrademarkList();

    IPage<BaseTrademark> baseTrademark(Page<BaseTrademark> page1);

    BaseTrademark getTrademarkById(Long tmId);
}

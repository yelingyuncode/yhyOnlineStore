package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.atguigu.gmall.product.service.mapper.BaseTrademarkMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseTrademarkServiceImpl implements BaseTrademarkService {
    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;
    @Override
    public List<BaseTrademark> getTrademarkList() {

        return baseTrademarkMapper.selectList(null);
    }

    @Override
    public IPage<BaseTrademark> baseTrademark(Page<BaseTrademark> page1) {
        return  baseTrademarkMapper.selectPage(page1,null);
    }

    @Override
    public BaseTrademark getTrademarkById(Long tmId) {
        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(tmId);

        return baseTrademark;
    }
}

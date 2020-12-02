package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.BaseCategoryViewService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseCategoryViewServiceImpl implements BaseCategoryViewService {
    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;
    @Override
    public BaseCategoryView getCategoryViewByCategory3Id(Long category3Id) {
        QueryWrapper<BaseCategoryView> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id", category3Id);
        BaseCategoryView baseCategoryView = baseCategoryViewMapper.selectOne(wrapper);
        return baseCategoryView;

    }
}

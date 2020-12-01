package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.mapper.SpuSaleAttrMapper;
import com.atguigu.gmall.product.service.mapper.SpuSaleAttrValueMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpuSaleAttrServiceImpl implements SpuSaleAttrService {
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Override
    public List<SpuSaleAttr> spuSaleAttrList(long spuId) {
        QueryWrapper<SpuSaleAttr> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id", spuId);
        List<SpuSaleAttr> saleAttrs = spuSaleAttrMapper.selectList(wrapper);
        //应该把属性值放进去在返回去
        for (SpuSaleAttr saleAttr : saleAttrs) {
            QueryWrapper<SpuSaleAttrValue> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("spu_id",saleAttr.getSpuId());
            wrapper1.eq("base_sale_attr_id",saleAttr.getBaseSaleAttrId());
            List<SpuSaleAttrValue> spuSaleAttrValues = spuSaleAttrValueMapper.selectList(wrapper1);
            saleAttr.setSpuSaleAttrValueList(spuSaleAttrValues);
        }


        return saleAttrs;
    }
}

package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.mapper.SpuSaleAttrMapper;
import com.atguigu.gmall.product.service.mapper.SpuSaleAttrValueMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public Map<String, Long> getSaleAttrValuesBySpu(Long spuId) {
        List<Map> list = spuSaleAttrMapper.selectSaleAttrValuesBySpu(spuId);
        //  拿到数据应该处理一下 然后封装到map返回
        HashMap<String, Long> map = new HashMap<>();
        for (Map map1 : list) {
          //            value_Ids
            String value_Ids =  (String) map1.get("value_Ids");
          //sku_id
             Long sku_id =  (Long)map1.get("sku_id");
         map.put(value_Ids,sku_id);
        }

        return map;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListBySpuId(Long spuId,Long skuId) {
        //逻辑重写查询页面的 信息
        List<SpuSaleAttr> list = spuSaleAttrMapper.selectSpuSaleAttrListBySku(spuId,skuId);
        return list;
    }
}

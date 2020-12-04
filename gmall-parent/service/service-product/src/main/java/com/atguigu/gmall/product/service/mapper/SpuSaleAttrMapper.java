package com.atguigu.gmall.product.service.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {
    List<Map> selectSaleAttrValuesBySpu(@Param("spuId") Long spuId);

    List<SpuSaleAttr> selectSpuSaleAttrListBySku(@Param("spuId") Long spuId,@Param("skuId") Long skuId);
}

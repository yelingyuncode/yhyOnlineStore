package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.product.service.mapper.SkuImageMapper;
import com.atguigu.gmall.product.service.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.service.mapper.SkuSaleAttrValueMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SkuInfoServiceImpl implements SkuInfoService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Override
    public IPage<SkuInfo> list(IPage<SkuInfo> page1) {
        page1.setSize(50);
        IPage<SkuInfo> skuInfoIPage = skuInfoMapper.selectPage(page1, null);
        return skuInfoIPage;
    }

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //先插入sku_info
        skuInfoMapper.insert(skuInfo);
        //得到 sku_id
        Long sku_id = skuInfo.getId();
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (null != skuImageList) {
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(sku_id);
                skuImageMapper.insert(skuImage);

            }
        }
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (null != skuAttrValueList) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(sku_id);
                skuAttrValueMapper.insert(skuAttrValue);
            }
            List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            if (null != skuSaleAttrValueList) {
                for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                    skuSaleAttrValue.setSkuId(sku_id);
                    skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                    skuSaleAttrValueMapper.insert(skuSaleAttrValue);
                }
            }
        }


    }

    @Override
    public void onSale(long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
    }

    @Override
    public void cancelSale(long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);
    }

    @Override
    public BigDecimal getPrice(long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        BigDecimal price = skuInfo.getPrice();
        return price;
    }

    @Override
    public SkuInfo getSkuInfoById(long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
           //把图片查到返回给前端
        QueryWrapper<SkuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("sku_id", skuId);
        List<SkuImage> skuImages = skuImageMapper.selectList(wrapper);
        skuInfo.setSkuImageList(skuImages);
        return skuInfo;
    }
}

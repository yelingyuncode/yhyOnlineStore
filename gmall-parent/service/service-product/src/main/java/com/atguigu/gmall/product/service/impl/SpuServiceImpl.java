package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SpuMapper;
import com.atguigu.gmall.product.service.SpuService;
import com.atguigu.gmall.product.service.mapper.SpuImageMapper;
import com.atguigu.gmall.product.service.mapper.SpuSaleAttrMapper;
import com.atguigu.gmall.product.service.mapper.SpuSaleAttrValueMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SpuServiceImpl implements SpuService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    private SpuImageMapper spuImageMapper;
    @Override
    public IPage<SpuInfo> pageSpuList(IPage<SpuInfo> page, long category3Id) {
        QueryWrapper<SpuInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("category3_id", category3Id);
        IPage<SpuInfo> infoIPage = spuMapper.selectPage(page, wrapper);

        return infoIPage;

    }

    @Override
    public boolean saveSpuInfo(SpuInfo spuInfo) {
             //先直接插入
        spuMapper.insert(spuInfo);
        //返回的该表的id
        Long spu_id = spuInfo.getId();
        //拿到属性类里面所有的图片信息
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (spuImageList != null){
            //说明有值 ，应该遍历插入
            for (SpuImage spuImage : spuImageList) {
                //现在没弄上传  所以不写
                spuImage.setSpuId(spu_id);
                spuImageMapper.insert(spuImage);
            }

        }
        //再去插入售卖属性相关的
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (spuSaleAttrList != null ) {
            //遍历售卖属性，拿到售卖属性里面各个属性值 遍历插入
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spu_id);
                spuSaleAttrMapper.insert(spuSaleAttr);
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (null != spuSaleAttrValueList){
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spu_id);
                        spuSaleAttrValue.setBaseSaleAttrId(spuSaleAttr.getBaseSaleAttrId());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);

                    }

                }
            }
        }

        return true;
    }
}

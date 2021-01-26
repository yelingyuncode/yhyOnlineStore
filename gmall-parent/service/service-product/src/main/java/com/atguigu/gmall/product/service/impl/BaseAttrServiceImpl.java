package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.mapper.BaseAttrMapper;
import com.atguigu.gmall.product.service.BaseAttrService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class BaseAttrServiceImpl implements BaseAttrService {
    @Autowired
    private BaseAttrMapper baseAttrValueMapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_id",attrId);
        List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.selectList(wrapper);
        return baseAttrValues;
    }

    @Override
    public List<BaseAttrInfo> attrInfoList(Long category3Id) {
        List<BaseAttrInfo> baseAttrInfos =baseAttrInfoMapper.selectAttrInfoList(3,category3Id);
//        QueryWrapper<BaseAttrInfo> wrapper = new QueryWrapper<>();
//             wrapper.eq("category_level",3);
//             wrapper.eq("category_id",category3Id);
//        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.selectList(wrapper);
//        for (BaseAttrInfo baseAttrInfo : baseAttrInfos) {
//            Long attr_id = baseAttrInfo.getId();
//            QueryWrapper<BaseAttrValue> wrapper1 = new QueryWrapper<>();
//            wrapper1.eq("attr_id",attr_id);
//            List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.selectList(wrapper1);
//            baseAttrInfo.setAttrValueList(baseAttrValues);

//        }
        return baseAttrInfos;
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //先拿到id
        Long attr_id = baseAttrInfo.getId();
        if (attr_id == null || attr_id <= 0){
            //插入
            baseAttrInfoMapper.insert(baseAttrInfo);
            Long id = baseAttrInfo.getId();
            attr_id = id;
        }else{
            //更新
            baseAttrInfoMapper.updateById(baseAttrInfo);
            QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id",attr_id);
            baseAttrValueMapper.delete(wrapper);
        }
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue baseAttrValue : attrValueList) {
            baseAttrValue.setAttrId(attr_id);
            baseAttrValueMapper.insert(baseAttrValue);
        }
    }

    @Override
    public List<SearchAttr> getSearchAttrList(long skuId) {
    List<SearchAttr> list =  baseAttrInfoMapper.selectSearchAttrList(skuId);

        return list;
    }
}

package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.config.GmallCache;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jdk.nashorn.internal.ir.LiteralNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private BaseCategory1Mapper category1Mapper;
    @Autowired
    private BaseCategory2Mapper category2Mapper;
    @Autowired
    private BaseCategory3Mapper category3Mapper;

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;


    @Override
    public List<BaseCategory1> getCategory1() {

        return category1Mapper.selectList(null);
    }

    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        QueryWrapper<BaseCategory2> wrapper = new QueryWrapper<>();
        wrapper.eq("category1_id",category1Id);
        return category2Mapper.selectList(wrapper);
    }

    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        QueryWrapper<BaseCategory3> wrapper = new QueryWrapper<>();
        wrapper.eq("category2_id",category2Id);
        return category3Mapper.selectList(wrapper);
    }

    @GmallCache
    @Override
    public List<JSONObject> getBaseCategoryList() {
        List<BaseCategoryView> baseCategoryViews = baseCategoryViewMapper.selectList(null);
        //最后返回的东西要封装到这个集合
        List<JSONObject> list = new ArrayList<>();
        Map<Long, List<BaseCategoryView>> category1Map = baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        for (Map.Entry<Long, List<BaseCategoryView>> category1Object : category1Map.entrySet()) {
            //遍历每一个一级分类
            Long category1Id = category1Object.getKey();
            String category1Name = category1Object.getValue().get(0).getCategory1Name();
            JSONObject category1Json = new JSONObject();
            category1Json.put("categoryId", category1Id);
            category1Json.put("categoryName", category1Name);
            //二级分类集合
            ArrayList<JSONObject> category2List = new ArrayList<>();
            List<BaseCategoryView> category2Views = category1Object.getValue();
            Map<Long, List<BaseCategoryView>> category2Map = category2Views.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            for (Map.Entry<Long, List<BaseCategoryView>> category2Object : category2Map.entrySet()) {
                Long category2Id = category2Object.getKey();
                String category2Name = category2Object.getValue().get(0).getCategory2Name();
                JSONObject category2Json = new JSONObject();
                category2Json.put("categoryId",category2Id);
                category2Json.put("categoryName",category2Name);
                //二级分类下的三级分类
                List<JSONObject> category3list = new ArrayList<>();
                List<BaseCategoryView> category3Views = category2Object.getValue();
                Map<Long, List<BaseCategoryView>> category3Map = category3Views.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id));
                for (Map.Entry<Long, List<BaseCategoryView>> category3Object : category3Map.entrySet()) {
                    Long category3Id = category3Object.getKey();
                    String category3Name = category3Object.getValue().get(0).getCategory3Name();
                    JSONObject category3Json = new JSONObject();
                    category3Json.put("categoryId",category3Id);
                    category3Json.put("categoryName",category3Name);

                    category3list.add(category3Json);

                }
                category2Json.put("categoryChild", category3list);
                category2List.add(category2Json);
            }
            category1Json.put("categoryChild",category2List);
            list.add(category1Json);


        }

        return list;
    }
}

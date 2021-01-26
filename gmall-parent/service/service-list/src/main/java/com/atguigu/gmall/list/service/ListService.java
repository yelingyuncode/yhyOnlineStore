package com.atguigu.gmall.list.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;

import java.util.List;

public interface ListService {
    List<JSONObject> getBaseCategoryList();

    void createGoodsIndex();

    void onSale(long skuId);

    void cancelSale(long skuId);

    SearchResponseVo list(SearchParam searchParam);

}

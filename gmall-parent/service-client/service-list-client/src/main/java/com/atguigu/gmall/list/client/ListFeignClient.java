package com.atguigu.gmall.list.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(value = "service-list")
public interface ListFeignClient {
    @RequestMapping("api/list/getBaseCategoryList")
     List<JSONObject> getBaseCategoryList();
}

package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class BaseAttrApiController {
    @Autowired
    private BaseAttrService baseAttrService;

 //修改前的回显
    @RequestMapping ("/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable Long attrId){

        List<BaseAttrValue> baseAttrValues =  baseAttrService.getAttrValueList(attrId);
    return Result.ok(baseAttrValues);
    }
    // http://api.gmall.com/admin/product/attrInfoList/{category1Id}/{category2Id}/{category3Id}得到三级分类的详细信息
    @RequestMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable Long category3Id){
       List<BaseAttrInfo> baseAttrInfos =  baseAttrService.attrInfoList(category3Id);
       return Result.ok(baseAttrInfos);
    }
    //添加新的属性和属性值
    //http://api.gmall.com/admin/product/saveAttrInfo
    @RequestMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

}

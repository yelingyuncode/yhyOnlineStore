package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.product.service.SpuImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class SpuImageController {
    //http://api.gmall.com/admin/product/spuImageList/{spuId}


    @Autowired
    private SpuImageService spuImageService;

    @RequestMapping("spuImageList/{spuId}")
    public Result spuImageList(@PathVariable long spuId) {
        List<SpuImage> spuImages = spuImageService.spuImageList(spuId);
        return Result.ok(spuImages);
    }
}

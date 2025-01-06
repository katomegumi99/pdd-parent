package com.pdd.controller;

import com.pdd.client.product.ProductFeignClient;
import com.pdd.common.result.Result;
import com.pdd.model.product.Category;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@Api(tags = "商品分类")
@RestController
@RequestMapping("api/home")
public class CategoryApiController {

    @Autowired
    private ProductFeignClient productFeignClient;

    @ApiOperation(value = "获取分类列表")
    @GetMapping("category")
    public Result getCategory() {
        List<Category> categoryList = productFeignClient.findAllCategoryList();
        return Result.ok(categoryList);
    }
}

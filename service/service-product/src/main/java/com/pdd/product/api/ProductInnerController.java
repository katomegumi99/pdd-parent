package com.pdd.product.api;

import com.pdd.model.product.Category;
import com.pdd.model.product.SkuInfo;
import com.pdd.product.service.CategoryService;
import com.pdd.product.service.SkuInfoService;
import com.pdd.vo.product.SkuInfoVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@RestController
@RequestMapping("/api/product")
public class ProductInnerController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuInfoService skuInfoService;

    @ApiOperation(value = "根据分类id获取分类信息")
    @GetMapping("/inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable("categoryId") Long categoryId) {
        Category category = categoryService.getById(categoryId);
        return category;
    }

    @ApiOperation(value = "根据skuId获取sku信息")
    @GetMapping("/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId) {
        return skuInfoService.getById(skuId);
    }

    // 根据skuId列表获取sku信息列表
    @PostMapping("/inner/findSkuInfoList")
    public List<SkuInfo> findSkuInfoList(@RequestBody List<Long> skuIdList) {
        List<SkuInfo> skuInfoList = skuInfoService.findSkuInfoList(skuIdList);
        return skuInfoList;
    }

    // 根据分类id获取分类列表
    @PostMapping("/inner/findCategoryList")
    public List<Category> findCategoryList(@RequestBody List<Long> categoryIdList) {
        List<Category> categoryList = categoryService.listByIds(categoryIdList);
        return categoryList;
    }

    // 根据关键字匹配sku列表
    @GetMapping("/inner/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoByKeyword(@PathVariable("keyword") String keyword) {
        return skuInfoService.findSkuInfoByKeyword(keyword);
    }

    // 获取所有分类列表
    @GetMapping("/inner/findAllCategoryList")
    public List<Category> findAllCategoryList() {
        List<Category> list = categoryService.list();
        return list;
    }

    // 获取新人专享的商品数据
    @GetMapping("/inner/findNewPersonSkuInfoList")
    public List<SkuInfo> findNewPersonSkuInfoList() {
        List<SkuInfo> skuInfoList = skuInfoService.findNewPersonSkuInfoList();
        return skuInfoList;
    }

    // 根据skuId获取sku信息
    @GetMapping("inner/getSkuInfoVo/{skuId}")
    public SkuInfoVo getSkuInfoVo(@PathVariable("skuId") Long skuId) {
        return skuInfoService.getSkuInfoVo(skuId);
    }
}

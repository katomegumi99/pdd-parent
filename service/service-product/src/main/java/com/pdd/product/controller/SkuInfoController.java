package com.pdd.product.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pdd.common.result.Result;
import com.pdd.model.product.SkuInfo;
import com.pdd.product.service.SkuInfoService;
import com.pdd.vo.product.SkuInfoQueryVo;
import com.pdd.vo.product.SkuInfoVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * sku信息 前端控制器
 * </p>
 *
 * @author pdd
 * @since 2024-12-06
 */
@RestController
@RequestMapping("/admin/product/skuInfo")
//
public class SkuInfoController {

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 分页查询sku列表
     * url: `${api_name}/${page}/${limit}`,
     *       method: 'get',
     *       params: searchObj
     */
    @ApiOperation("分页查询sku列表")
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable("page") Long page,
                       @PathVariable("limit")Long limit,
                       SkuInfoQueryVo skuInfoQueryVo) {
        Page<SkuInfo> pageParam = new Page<>(page, limit);
        IPage<SkuInfo> pageModel = skuInfoService.selectPageSkuInfo(pageParam, skuInfoQueryVo);

        return Result.ok(pageModel);
    }

    //商品添加方法
    @ApiOperation(value = "新增商品")
    @PostMapping("save")
    public Result save(@RequestBody SkuInfoVo skuInfoVo) {
        skuInfoService.saveSkuInfo(skuInfoVo);
        return Result.ok(null);
    }

    @ApiOperation("获取sku信息")
    @GetMapping("get/{id}")
    public Result get(@PathVariable("id") Long id) {
        SkuInfoVo skuInfoVo = skuInfoService.getSkuInfo(id);
        return Result.ok(skuInfoVo);
    }

    @ApiOperation("修改sku")
    @PutMapping("update")
    public Result update(@RequestBody SkuInfoVo skuInfoVo) {
        skuInfoService.updateSkuInfo(skuInfoVo);
        return Result.ok(null);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        skuInfoService.removeById(id);
        return Result.ok(null);
    }

    @ApiOperation(value = "根据id列表删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        skuInfoService.removeByIds(idList);
        return Result.ok(null);
    }

//    url: `${api_name}/check/${id}/${status}`,
//    method: 'get'
    @ApiOperation("商品审核")
    @GetMapping("check/{id}/{status}")
    public Result check(@PathVariable("id") Long id,
                        @PathVariable("status") Integer status) {
        skuInfoService.check(id, status);
        return Result.ok(null);
    }




    /**
     * url: `${api_name}/publish/${id}/${status}`,
     *             method: 'get'
     * @param id skuId
     * @param status 状态
     * @return
     */
    @ApiOperation("商品上下架")
    @GetMapping("publish/{id}/{status}")
    public Result publish(@PathVariable("id") Long id,
                          @PathVariable("status") Integer status) {
        skuInfoService.publish(id, status);
        return Result.ok(null);
    }



    /**
     *  url: `${api_name}/isNewPerson/${id}/${status}`,
     *     method: 'get'
     * @param id skuId
     * @param status 状态
     * @return
     */
    @ApiOperation("新人专享")
    @GetMapping("isNewPerson/{id}/{status}")
    public Result isNewPerson(@PathVariable("id") Long id,
                              @PathVariable("status") Integer status) {
        skuInfoService.isNewPerson(id, status);
        return Result.ok(null);
    }
}


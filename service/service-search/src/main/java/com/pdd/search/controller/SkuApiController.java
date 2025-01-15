package com.pdd.search.controller;

/**
 * @author youzairichangdawang
 * @version 1.0
 */

import com.pdd.common.result.Result;
import com.pdd.model.search.SkuEs;
import com.pdd.search.service.SkuService;
import com.pdd.vo.search.SkuEsQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 商品搜索列表接口
 * </p>
 */
@RestController
@RequestMapping("/api/search/sku")
public class SkuApiController {
    @Autowired
    private SkuService skuService;

    @ApiOperation(value = "上架商品")
    @GetMapping("inner/upperSku/{skuId}")
    public Result upperSku(@PathVariable("skuId") Long skuId) {
        skuService.upperSku(skuId);
        return Result.ok(null);
    }

    @ApiOperation(value = "下架商品")
    @GetMapping("inner/lowerSku/{skuId}")
    public Result lowerSku(@PathVariable("skuId") Long skuId) {
        skuService.lowerSku(skuId);
        return Result.ok(null);
    }

    @ApiOperation(value = "获取爆品商品")
    @GetMapping("/inner/findHotSkuList")
    public List<SkuEs> findHotSkuList() {
        List<SkuEs> skuEsList = skuService.findHotSkuList();
        return skuEsList;
    }

    @ApiOperation(value = "根据商品分类分页搜索商品")
    @GetMapping("{page}/{limit}")
    public Result search(@PathVariable("page") Integer page,
                         @PathVariable("limit") Integer limit,
                         SkuEsQueryVo skuEsQueryVo) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<SkuEs> pageModel = skuService.search(pageable, skuEsQueryVo);
        return Result.ok(pageModel);
    }

    // 更新商品热度
    @GetMapping("inner/incrHotScore/{skuId}")
    public Boolean incrHotScore(@PathVariable("skuId") Long skuId) {
        skuService.incrHotScore(skuId);
        return true;
    }
}

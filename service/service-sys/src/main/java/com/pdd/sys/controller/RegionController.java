package com.pdd.sys.controller;


import com.pdd.common.result.Result;
import com.pdd.model.sys.Region;
import com.pdd.sys.service.RegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 地区表 前端控制器
 * </p>
 *
 * @author pdd
 * @since 2024-12-03
 */
@Api(tags = "地区接口")
@RestController
@RequestMapping("/admin/sys/region")
//
public class RegionController {

    @Autowired
    private RegionService regionService;

    @ApiOperation("根据区域关键字查询区域列表信息")
    @GetMapping("findRegionByKeyword/{keyword}")
    public Result findRegionByKeyword(@PathVariable("keyword") String keyword) {
        List<Region> list = regionService.getRegionByKeyword(keyword);
        return Result.ok(list);
    }
}


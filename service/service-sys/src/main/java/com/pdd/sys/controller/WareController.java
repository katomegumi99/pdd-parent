package com.pdd.sys.controller;


import com.pdd.common.result.Result;
import com.pdd.model.sys.Ware;
import com.pdd.sys.service.WareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 仓库表 前端控制器
 * </p>
 *
 * @author pdd
 * @since 2024-12-03
 */
@Api(tags = "仓库接口")
@RestController
@RequestMapping("/admin/sys/ware")
//
public class WareController {

    @Autowired
    private WareService wareService;

    // 查询所有仓库列表
    @ApiOperation("查询所有仓库列表")
    @GetMapping("findAllList")
    public Result findAllList() {
        List<Ware> list = wareService.list();
        return Result.ok(list);
    }

}


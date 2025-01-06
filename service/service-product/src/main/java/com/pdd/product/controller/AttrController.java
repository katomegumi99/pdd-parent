package com.pdd.product.controller;


import com.pdd.common.result.Result;
import com.pdd.model.product.Attr;
import com.pdd.product.service.AttrService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品属性 前端控制器
 * </p>
 *
 * @author pdd
 * @since 2024-12-06
 */
@Api(tags = "商品属性接口")
@RestController
@RequestMapping("/admin/product/attr")
//
public class AttrController {

    @Autowired
    private AttrService attrService;

    // 根据平台属性分组id查询
    /**
     * url: `${api_name}/${groupId}`,
     *       method: 'get'
     */
    @ApiOperation("根据平台属性分组id查询")
    @GetMapping("{groupId}")
    public Result list(@PathVariable("groupId") Long groupId) {
        List<Attr> list = attrService.getAttrListByGroupId(groupId);
        return Result.ok(list);
    }

    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable("id") Long id) {
        Attr attr = attrService.getById(id);
        return Result.ok(attr);
    }

    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody Attr attr) {
        attrService.save(attr);
        return Result.ok(null);
    }

    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody Attr attr) {
        attrService.updateById(attr);
        return Result.ok(null);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable("id") Long id) {
        attrService.removeById(id);
        return Result.ok(null);
    }

    @ApiOperation(value = "根据id列表删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idLists) {
        attrService.removeByIds(idLists);
        return Result.ok(null);
    }
}


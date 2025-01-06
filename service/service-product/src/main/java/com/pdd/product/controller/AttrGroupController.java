package com.pdd.product.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pdd.common.result.Result;
import com.pdd.model.product.AttrGroup;
import com.pdd.product.service.AttrGroupService;
import com.pdd.vo.product.AttrGroupQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 属性分组 前端控制器
 * </p>
 *
 * @author pdd
 * @since 2024-12-06
 */
@Api(tags = "商品属性分组")
@RestController
@RequestMapping("/admin/product/attrGroup")
//
public class AttrGroupController {

    @Autowired
    private AttrGroupService attrGroupService;

    /**
     * url: `${api_name}/${page}/${limit}`,
     *       method: 'get',
     *       params: searchObj
     */
    @ApiOperation("分页获取平台属性分组")
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable("page") Long page,
                       @PathVariable("limit") Long limit,
                       AttrGroupQueryVo attrGroupQueryVo) {
        Page<AttrGroup> pageParam = new Page<>(page, limit);
        IPage<AttrGroup> pageModel = attrGroupService.selectPageList(pageParam, attrGroupQueryVo);

        return Result.ok(pageModel);
    }

    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        AttrGroup attrGroup = attrGroupService.getById(id);
        return Result.ok(attrGroup);
    }

    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody AttrGroup attrGroup) {
        attrGroupService.save(attrGroup);
        return Result.ok(null);
    }

    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody AttrGroup attrGroup) {
        attrGroupService.updateById(attrGroup);
        return Result.ok(null);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        attrGroupService.removeById(id);
        return Result.ok(null);
    }

    @ApiOperation(value = "根据id列表删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        attrGroupService.removeByIds(idList);
        return Result.ok(null);
    }

    // 查询所有平台属性分组列表
    @ApiOperation("查询所有平台属性分组列表")
    @GetMapping("findAllList")
    public Result findAllList() {
        //List<AttrGroup> list = attrGroupService.list();
        List<AttrGroup> list = attrGroupService.findAllList();
        return Result.ok(list);
    }
}


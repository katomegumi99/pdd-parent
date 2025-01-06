package com.pdd.sys.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pdd.common.result.Result;
import com.pdd.model.sys.RegionWare;
import com.pdd.sys.service.RegionWareService;
import com.pdd.vo.sys.RegionWareQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.omg.CORBA.INTERNAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 城市仓库关联表 前端控制器
 * </p>
 *
 * @author pdd
 * @since 2024-12-03
 */
@Api(tags = "开通区域接口")
@RestController
@RequestMapping("/admin/sys/regionWare")
//
public class RegionWareController {

    @Autowired
    private RegionWareService regionWareService;

    /**
     * getPageList(page, limit,searchObj) {
     *     return request({
     *       url: `${api_name}/${page}/${limit}`,
     *       method: 'get',
     *       params: searchObj
     *     })
     *   },
     */
    /**
     *
     * @param page 当前页
     * @param limit 每页页数
     * @param regionWareQueryVo
     * @return
     */
    @ApiOperation("开通区域列表")
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable("page") Long page,
                       @PathVariable("limit") Long limit,
                       RegionWareQueryVo regionWareQueryVo) {
        Page<RegionWare> pageParam = new Page<>();
        IPage<RegionWare> pageModel = regionWareService.selectPageRegionWare(pageParam, regionWareQueryVo);

        return Result.ok(pageModel);
    }

    // 添加开通区域
    @ApiOperation("添加开通区域")
    @PostMapping("save")
    public Result save(@RequestBody RegionWare regionWare) {
        regionWareService.saveRegionWare(regionWare);
        return Result.ok(null);
    }

    /**
     * removeById(id) {
     *     return request({
     *       url: `${api_name}/remove/${id}`,
     *       method: 'delete'
     *     })
     *   },
     */
    @ApiOperation("删除开通区域")
    @DeleteMapping("remove/{id}")
    public Result removeById(@PathVariable("id") Long id) {
        regionWareService.removeById(id);
        return Result.ok(null);
    }

    /**
     * 取消开通区域
     * updateStatus(id, status) {
     *     return request({
     *       url: `${api_name}/updateStatus/${id}/${status}`,
     *       method: 'post'
     *     })
     *   },
     */
    @ApiOperation("取消开通区域")
    @PostMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable("id") Long id,
                               @PathVariable("status") Integer status) {
        regionWareService.updateStatus(id, status);
        return Result.ok(null);
    }
}


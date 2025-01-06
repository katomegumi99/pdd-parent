package com.pdd.activity.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pdd.activity.service.ActivityInfoService;
import com.pdd.common.result.Result;
import com.pdd.model.activity.ActivityInfo;
import com.pdd.model.product.SkuInfo;
import com.pdd.vo.activity.ActivityRuleVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 活动表 前端控制器
 * </p>
 *
 * @author pdd
 * @since 2024-12-18
 */
@RestController
@RequestMapping("/admin/activity/activityInfo")
//
public class ActivityInfoController {

    @Autowired
    private ActivityInfoService activityInfoService;

    // 分页查询活动列表
    /**
     * url: `${api_name}/${page}/${limit}`,
     *       method: 'get'
     */
    @ApiOperation("分页查询活动列表")
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable("page") Long page,
                       @PathVariable("limit") Long limit) {
        Page<ActivityInfo> pageParam = new Page<>(page, limit);
        IPage<ActivityInfo> pageModel = activityInfoService.selectPage(pageParam);
        return Result.ok(pageModel);
    }

    @ApiOperation("通过id获取活动规则")
    @GetMapping("get/{id}")
    public Result get(@PathVariable("id") Long id) {
        ActivityInfo activityInfo = activityInfoService.getById(id);
        activityInfo.setActivityTypeString(activityInfo.getActivityType().getComment());
        return Result.ok(activityInfo);
    }

    /**
     * 添加规则
     * url: `${api_name}/save`,
     *       method: 'post',
     *       data: role
     */
    @ApiOperation("添加规则")
    @PostMapping("save")
    public Result save(@RequestBody ActivityInfo activityInfo) {
        activityInfoService.save(activityInfo);
        return Result.ok(null);
    }

    /**
     * 修改规则
     * url: `${api_name}/update`,
     *       method: 'put',
     *       data: role
     */
    @ApiOperation("修改规则")
    @PutMapping("update")
    public Result update(@RequestBody ActivityInfo activityInfo) {
        activityInfoService.updateById(activityInfo);
        return Result.ok(null);
    }

    /**
     * 根据id删除活动
     * url: `${api_name}/remove/${id}`,
     *       method: 'delete'
     */
    @ApiOperation("根据id删除活动")
    @DeleteMapping("remove/{id}")
    public Result removeById(@PathVariable("id") Long id) {
        activityInfoService.removeById(id);
        return Result.ok(null);
    }

    /**
     * 批量删除活动
     * url: `${api_name}/batchRemove`,
     *       method: 'delete',
     *       data: idList
     */
    @ApiOperation("批量删除活动")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        activityInfoService.removeByIds(idList);
        return Result.ok(null);
    }

    /**
     * 1 根据活动id获取活动规则数据
     * url: `${api_name}/findActivityRuleList/${id}`,
     *       method: 'get'
     */
    @ApiOperation("根据活动id获取活动规则数据")
    @GetMapping("findActivityRuleList/{id}")
    public Result findActivityRuleList(@PathVariable("id") Long id) {
        Map<String, Object> activityRuleMap = activityInfoService.findActivityRuleList(id);
        return Result.ok(activityRuleMap);
    }

    /**
     * 2 活动规则添加
     * url: `${api_name}/saveActivityRule`,
     *       method: 'post',
     *       data: rule
     */
    @ApiOperation("活动规则添加")
    @PostMapping("saveActivityRule")
    public Result saveActivityRule(@RequestBody ActivityRuleVo activityRuleVo) {
        activityInfoService.saveActivityRule(activityRuleVo);
        return Result.ok(null);
    }

    /**
     * 3 根据关键字查询匹配的sku信息
     * url: `${api_name}/findSkuInfoByKeyword/${keyword}`,
     *       method: 'get'
     */
    @ApiOperation("根据关键字查询匹配的sku信息")
    @GetMapping("findSkuInfoByKeyword/{keyword}")
    public Result findSkuInfoByKeyword(@PathVariable("keyword") String keyword) {
        List<SkuInfo> skuInfoList = activityInfoService.findSkuInfoByKeyword(keyword);
        return Result.ok(skuInfoList);
    }
}


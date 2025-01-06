package com.pdd.activity.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pdd.activity.service.CouponInfoService;
import com.pdd.common.result.Result;
import com.pdd.model.activity.CouponInfo;
import com.pdd.vo.activity.CouponRuleVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠券信息 前端控制器
 * </p>
 *
 * @author pdd
 * @since 2024-12-18
 */
@RestController
@RequestMapping("/admin/activity/couponInfo")
//
public class CouponInfoController {

    @Autowired
    private CouponInfoService couponInfoService;

    // 1 优惠券的分页查询
    /**
     * url: `${api_name}/${page}/${limit}`,
     *       method: 'get'
     */
    @ApiOperation("1 优惠券的分页查询")
    @GetMapping("{page}/{limit}")
    public Result getPageList(@PathVariable("page") Long page,
                              @PathVariable("limit") Long limit) {
        IPage<CouponInfo> pageModel = couponInfoService.selectPageCouponInfo(page,limit);
        return Result.ok(pageModel);
    }

    // 2 添加优惠券
    /**
     * url: `${api_name}/save`,
     *       method: 'post',
     *       data: role
     */
    @ApiOperation("2 添加优惠券")
    @PostMapping("save")
    public Result save(@RequestBody CouponInfo couponInfo) {
        couponInfoService.save(couponInfo);
        return Result.ok(null);
    }

    // 3 根据id查询优惠券
    /**
     * url: `${api_name}/get/${id}`,
     *       method: 'get'
     */
    @ApiOperation("3 根据id查询优惠券")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable("id") Long id) {
        CouponInfo couponInfo = couponInfoService.getCouponInfo(id);
        return Result.ok(couponInfo);
    }

    // 4 根据优惠券id查询规则数据
    /**
     * url: `${api_name}/findCouponRuleList/${id}`,
     *       method: 'get'
     */
    @ApiOperation("4 根据优惠券id查询规则数据")
    @GetMapping("findCouponRuleList/{id}")
    public Result findCouponRuleList(@PathVariable("id") Long id) {
        Map<String, Object> map = couponInfoService.findCouponRuleList(id);
        return Result.ok(map);
    }

    // 5 添加优惠券规则数据
    /**
     * url: `${api_name}/saveCouponRule`,
     *       method: 'post',
     *       data: rule
     */
    @ApiOperation("5 添加优惠券规则数据")
    @PostMapping("saveCouponRule")
    public Result saveCouponRule(@RequestBody CouponRuleVo couponRuleVo) {
        couponInfoService.saveCouponRule(couponRuleVo);
        return Result.ok(null);
    }
    // 6 修改优惠券
    /**
     * url: `${api_name}/update`,
     *       method: 'put',
     *       data: role
     */
    @ApiOperation("6 修改优惠券")
    @PutMapping("update")
    public Result update(@RequestBody CouponInfo couponInfo) {
        couponInfoService.updateById(couponInfo);
        return Result.ok(null);
    }

    // 7 删除优惠券
    /**
     * url: `${api_name}/remove/${id}`,
     *       method: 'delete'
     */
    @ApiOperation("7 删除优惠券")
    @DeleteMapping("remove/{id}")
    public Result removeById(@PathVariable("id") Long id) {
        couponInfoService.removeById(id);
        return Result.ok(null);
    }

    /**
     * 8 批量删除优惠券
     * url: `${api_name}/batchRemove`,
     *       method: 'delete',
     *       data: idList
     */
    @ApiOperation("8 批量删除优惠券")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        couponInfoService.removeByIds(idList);
        return Result.ok(null);
    }

    // 根据关键字获取sku列表，活动使用
    /**
     * url: `${api_name}/findCouponByKeyword/${keyword}`,
     *       method: 'get'
     */
    @ApiOperation("根据关键字获取sku列表，活动使用")
    @GetMapping("findCouponByKeyword/{keyword}")
    public Result findCouponByKeyword(@PathVariable("keyword") String keyword) {
        List<CouponInfo> list = couponInfoService.findCouponByKeyword(keyword);
        return Result.ok(list);
    }
}


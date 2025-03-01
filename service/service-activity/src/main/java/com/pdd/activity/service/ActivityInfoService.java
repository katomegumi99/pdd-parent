package com.pdd.activity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pdd.model.activity.ActivityInfo;
import com.pdd.model.activity.ActivityRule;
import com.pdd.model.order.CartInfo;
import com.pdd.model.product.SkuInfo;
import com.pdd.vo.activity.ActivityRuleVo;
import com.pdd.vo.order.CartInfoVo;
import com.pdd.vo.order.OrderConfirmVo;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 活动表 服务类
 * </p>
 *
 * @author pdd
 * @since 2024-12-18
 */
public interface ActivityInfoService extends IService<ActivityInfo> {

    // 分页查询活动列表
    IPage<ActivityInfo> selectPage(Page<ActivityInfo> pageParam);

    // 2 活动规则添加
    void saveActivityRule(ActivityRuleVo activityRuleVo);

    // 根据活动id获取活动规则数据
    Map<String, Object> findActivityRuleList(Long id);

    // 3 根据关键字查询匹配的sku信息
    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    // 根据skuId列表获取促销信息
    Map<Long, List<String>> findActivity(List<Long> skuIdList);

    // 根据skuId获取营销数据和优惠券信息
    Map<String, Object> findActivityAndCoupon(Long skuId, Long userId);

    // 根据skuId 获取活动规则数据
    List<ActivityRule> findActivityRuleListBySkuId(Long skuId);

    // 查询购物车中的优惠信息
    OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList, Long userId);

    // 获取购物项对应的规则数据
    List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList);
}

package com.pdd.activity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pdd.model.activity.ActivityInfo;
import com.pdd.model.product.SkuInfo;
import com.pdd.vo.activity.ActivityRuleVo;

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
}

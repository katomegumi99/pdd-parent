package com.pdd.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pdd.activity.mapper.ActivityInfoMapper;
import com.pdd.activity.mapper.ActivityRuleMapper;
import com.pdd.activity.mapper.ActivitySkuMapper;
import com.pdd.activity.service.ActivityInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pdd.activity.service.CouponInfoService;
import com.pdd.client.product.ProductFeignClient;
import com.pdd.enums.ActivityType;
import com.pdd.model.activity.ActivityInfo;
import com.pdd.model.activity.ActivityRule;
import com.pdd.model.activity.ActivitySku;
import com.pdd.model.activity.CouponInfo;
import com.pdd.model.product.SkuInfo;
import com.pdd.vo.activity.ActivityRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 活动表 服务实现类
 * </p>
 *
 * @author pdd
 * @since 2024-12-18
 */
@Service
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo> implements ActivityInfoService {

    @Autowired
    private ActivityRuleMapper activityRuleMapper;

    @Autowired
    private ActivitySkuMapper activitySkuMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private CouponInfoService couponInfoService;

    // 分页查询活动列表
    @Override
    public IPage<ActivityInfo> selectPage(Page<ActivityInfo> pageParam) {
        Page<ActivityInfo> activityInfoPage = baseMapper.selectPage(pageParam, null);
        // 分页查询对象获取列表数据
        List<ActivityInfo> activityInfoList = activityInfoPage.getRecords();
        // 遍历 activityInfoList 集合，得到每个 ActivityInfo 对象，
        // 向 ActivityInfo 对象封装活动类型到 activityTypeString 属性里面
        activityInfoList.stream().forEach(item -> {
            item.setActivityTypeString(item.getActivityType().getComment());
        });

        return activityInfoPage;
    }

    // 1 根据活动id获取活动规则数据
    @Override
    public Map<String, Object> findActivityRuleList(Long id) {
        Map<String, Object> result = new HashMap<>();

        // 1 根据活动id查询规则列表 activity_rule 表
        LambdaQueryWrapper<ActivityRule> activityRuleWrapper = new LambdaQueryWrapper<>();
        activityRuleWrapper.eq(ActivityRule::getActivityId, id);
        List<ActivityRule> activityRuleList = activityRuleMapper.selectList(activityRuleWrapper);
        result.put("activityRuleList", activityRuleList);

        // 2 根据活动id查询使用该规则的商品skuid列表 activity_sku表
        LambdaQueryWrapper<ActivitySku> activitySkuWrapper = new LambdaQueryWrapper<>();
        activitySkuWrapper.eq(ActivitySku::getActivityId, id);
        List<ActivitySku> activitySkuList = activitySkuMapper.selectList(activitySkuWrapper);
        result.put("activitySkuList", activitySkuList);

        // 获取所有的skuId
        List<Long> skuIdList = activitySkuList.
                stream().map(ActivitySku::getSkuId).collect(Collectors.toList());

        // 2.1 通过远程调用 service-product 模块接口，根据skuid列表得到商品信息
        if (!skuIdList.isEmpty()) {
            List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoList(skuIdList);
            result.put("skuInfoList", skuInfoList);
        }
        return result;
    }

    // 2 活动规则添加
    @Override
    public void saveActivityRule(ActivityRuleVo activityRuleVo) {
        // 第一步 根据活动id删除之前的规则数据
        Long activityId = activityRuleVo.getActivityId();
        // ActivityRule 数据删除
        activityRuleMapper.delete(
                new LambdaQueryWrapper<ActivityRule>().eq(ActivityRule::getActivityId, activityId));
        // ActivitySku 数据删除
        activitySkuMapper.delete(
                new LambdaQueryWrapper<ActivitySku>().eq(ActivitySku::getActivityId, activityId)
        );

        // 第二步 获取规则列表数据
        List<ActivityRule> activityRuleList = activityRuleVo.getActivityRuleList();
        ActivityInfo activityInfo = baseMapper.selectById(activityId);
        for (ActivityRule activityRule : activityRuleList) {
            activityRule.setActivityId(activityId);// 活动id
            activityRule.setActivityType(activityInfo.getActivityType());// 活动类型
            activityRuleMapper.insert(activityRule);
        }

        // 第三步 获取规则范围数据
        List<ActivitySku> activitySkuList = activityRuleVo.getActivitySkuList();
        for (ActivitySku activitySku : activitySkuList) {
            activitySku.setActivityId(activityId);
            activitySkuMapper.insert(activitySku);
        }


    }

    // 3 根据关键字查询匹配的sku信息
    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        // 第一步 根据关键字查询sku匹配内容的列表
        // 1 service-product 模块创建接口，根据关键字查询sku匹配内容列表
        // 2 service-activity 远程调用得到sku内容列表
        List<SkuInfo> skuInfoList =
                productFeignClient.findSkuInfoByKeyword(keyword);
        // 判断：如果根据关键字查询不到匹配的内容，则直接返回空集合
        if (skuInfoList.size() == 0) {
            return skuInfoList;
        }
        // 获取所有的skuId
        List<Long> skuIdList = skuInfoList.stream().map(SkuInfo::getId).collect(Collectors.toList());

        // 第二步 判断添加的商品是否已经参加活动，如果已经参加活动则排除该商品
        // 1 查询 activity_info 和 activity_sku 两张数据库表，编写sql语句实现
        List<Long> existSkuIdList = baseMapper.selectSkuIdListExist(skuIdList);
        // 2 判断
        // 存储没有正在参加活动的商品
        ArrayList<SkuInfo> finalSkuList = new ArrayList<>();
        // 遍历所有sku列表，检查商品是否正在参加活动
        for (SkuInfo skuInfo : skuInfoList) {
            // 若商品没有正在参加活动，则将商品加入到 finalSkuList 集合中
            if (!existSkuIdList.contains(skuInfo.getId())) {
                finalSkuList.add(skuInfo);
            }
        }
        // 将没有正在参加活动的商品集合返回
        return finalSkuList;
    }

    // 根据skuId列表获取促销信息
    @Override
    public Map<Long, List<String>> findActivity(List<Long> skuIdList) {
        Map<Long, List<String>> result = new HashMap<>();
        // 遍历 skuIdList，得到每个skuId
        skuIdList.forEach(skuId -> {
            // 根据 skuId 进行查询，获取sku对应活动里的规则列表
            List<ActivityRule> activityRuleList =
                    baseMapper.findActivityRule(skuId);

            // 对数据进行封装
            if (!CollectionUtils.isEmpty(activityRuleList)) {
                List<String> ruleList = new ArrayList<>();
                // 由于数据库中的规则没有中文描述，所以需要将其改造成中文描述
                for (ActivityRule activityRule : activityRuleList) {
                    ruleList.add(this.getRuleDesc(activityRule));
                }
                result.put(skuId, ruleList);
            }
        });
        return result;
    }

    // 根据skuId获取营销数据和优惠券信息
    @Override
    public Map<String, Object> findActivityAndCoupon(Long skuId, Long userId) {
        // 根据skuId获取sku营销活动，一个活动有多个规则
        List<ActivityRule> activityRuleList = this.findActivityRuleListBySkuId(skuId);

        // 根据skuId + userId查询优惠券信息
        List<CouponInfo> couponInfoList = couponInfoService.findCouponInfoList(skuId,userId);

        // 封装到map集合
        Map<String, Object> map = new HashMap<>();
        map.put("couponInfoList", couponInfoList);
        map.put("activityRuleList", activityRuleList);
        return map;
    }

    // 根据skuId 获取活动规则数据
    @Override
    public List<ActivityRule> findActivityRuleListBySkuId(Long skuId) {
        List<ActivityRule> activityRuleList = baseMapper.findActivityRule(skuId);
        for (ActivityRule activityRule : activityRuleList) {
            String ruleDesc = this.getRuleDesc(activityRule);
            activityRule.setRuleDesc(ruleDesc);
        }

        return activityRuleList;
    }

    //构造规则名称的方法
    private String getRuleDesc(ActivityRule activityRule) {
        ActivityType activityType = activityRule.getActivityType();
        StringBuffer ruleDesc = new StringBuffer();
        if (activityType == ActivityType.FULL_REDUCTION) {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionAmount())
                    .append("元减")
                    .append(activityRule.getBenefitAmount())
                    .append("元");
        } else {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionNum())
                    .append("元打")
                    .append(activityRule.getBenefitDiscount())
                    .append("折");
        }
        return ruleDesc.toString();
    }
}

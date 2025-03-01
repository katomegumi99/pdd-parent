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
import com.pdd.model.order.CartInfo;
import com.pdd.model.product.SkuInfo;
import com.pdd.vo.activity.ActivityRuleVo;
import com.pdd.vo.order.CartInfoVo;
import com.pdd.vo.order.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
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

    // 查询购物车中的优惠信息
    @Override
    public OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList, Long userId) {
        // 1 获取购物车每个购物项参与活动的规则并根据活动规则进行分组
        // 一个活动规则可能对应多个商品
        // cartInfoVo
        List<CartInfoVo> cartInfoVoList = this.findCartActivityList(cartInfoList);

        // 2 计算参与活动后的金额
        BigDecimal activityReduceAmount = cartInfoVoList.stream()
                .filter(cartInfoVo -> cartInfoVo.getActivityRule() != null)
                .map(cartInfoVo -> cartInfoVo.getActivityRule().getReduceAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3 获取购物车可以使用的优惠券列表
        List<CouponInfo> couponInfoList = couponInfoService.findCartCouponInfo(cartInfoList,userId);



        // 4 计算商品使用优惠券后的金额，一次只能使用一张优惠券
        BigDecimal couponReduceAmount = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(couponInfoList)) {
            couponReduceAmount = couponInfoList.stream()
                    .filter(couponInfo -> couponInfo.getIsOptimal().intValue() == 1)
                    .map(couponInfo -> couponInfo.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // 5 计算没有参与活动，没有使用优惠券的原始金额
        BigDecimal originalTotalAmount = cartInfoList.stream()
                .filter(cartInfo -> cartInfo.getIsChecked() == 1)
                .map(cartInfo -> cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 6 参与活动，使用优惠券的最终总金额
        BigDecimal totalAmount = originalTotalAmount.subtract(activityReduceAmount).subtract(couponReduceAmount);

        // 7 封装结果到 OrderConfirmVo
        OrderConfirmVo orderTradeVo = new OrderConfirmVo();

        orderTradeVo.setCarInfoVoList(cartInfoVoList);
        orderTradeVo.setActivityReduceAmount(activityReduceAmount);
        orderTradeVo.setCouponInfoList(couponInfoList);
        orderTradeVo.setCouponReduceAmount(couponReduceAmount);
        orderTradeVo.setOriginalTotalAmount(originalTotalAmount);
        orderTradeVo.setTotalAmount(totalAmount);
        // 8 返回数据
        return orderTradeVo;
    }

    // 获取购物项对应的活动规则数据
    @Override
    public List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList) {
        /**
         * 先获取skuId
         * 然后获取activityId
         * 再获取详细规则
         * 最后进行分组
         */

        // 创建最终返回的集合
        List<CartInfoVo> cartInfoVoList = new ArrayList<>();

        // 获取所有的skuId
        List<Long> skuIdList = cartInfoList.stream().map(CartInfo::getSkuId).collect(Collectors.toList());

        // 根据skuId列表获取参加活动的sku的activityId
        List<ActivitySku> activitySkuList = baseMapper.selectCartActivity(skuIdList);

        // 根据活动进行分组，每个活动有哪些skuId信息
        // map里面的key是分组字段：活动id
        // value是每组里面的skuId列表数据，set集合（不会有重复，因为每个商品只能参加一个活动）
        Map<Long, Set<Long>> activityIdToSkuIdListMap = activitySkuList.stream().collect(
                Collectors.groupingBy(
                        ActivitySku::getActivityId,
                        Collectors.mapping(ActivitySku::getSkuId, Collectors.toSet())
                )
        );

        // 获取活动里的详细规则数据
        Map<Long,List<ActivityRule>> activityIdToActivityRuleListMap = new HashMap<>();

        // 获取所有activityId
        Set<Long> activityIdSet =
                activitySkuList.stream().map(ActivitySku::getActivityId).collect(Collectors.toSet());

        if (!CollectionUtils.isEmpty(activityIdSet)) {
            // 查询activity_rule表
            LambdaQueryWrapper<ActivityRule> wrapper = new LambdaQueryWrapper<>();
            wrapper.orderByDesc(ActivityRule::getConditionAmount,ActivityRule::getConditionNum);
            wrapper.in(ActivityRule::getActivityId, activityIdSet);
            // 查询具体的规则数据
            List<ActivityRule> activityRuleList = activityRuleMapper.selectList(wrapper);

            // 将数据封装到activityIdToActivityRuleListMap集合中
            // 根据activityId进行分组
            activityIdToActivityRuleListMap = activityRuleList.stream().collect(
                    Collectors.groupingBy(activityRule -> activityRule.getActivityId())
            );
        }

        // 有活动的购物项的skuId
        Set<Long> activitySkuIdSet = new HashSet<>();
        if (!CollectionUtils.isEmpty(activityIdToSkuIdListMap)) {
            // 遍历 activityIdToSkuIdListMap 集合
            Iterator<Map.Entry<Long, Set<Long>>> iterator = activityIdToSkuIdListMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, Set<Long>> entry = iterator.next();
                // 活动id
                Long activityId = entry.getKey();
                // 每个活动对应的skuId列表
                Set<Long> currentActivitySkuIdSet = entry.getValue();
                // 获取当前活动对应的购物项列表
                List<CartInfo> currentActivityCartInfoList = cartInfoList.stream()
                        .filter(cartInfo ->
                                currentActivitySkuIdSet.contains(cartInfo.getSkuId())).collect(Collectors.toList());
                // 计算购物项总金额和总数量
                BigDecimal activityTotalAmount = this.computeTotalAmount(currentActivityCartInfoList);
                int activityTotalNum = this.computeCartNum(currentActivityCartInfoList);

                // 获取活动规则
                List<ActivityRule> currentActivityRuleList =
                        activityIdToActivityRuleListMap.get(activityId);
                // 获取活动类型
                ActivityType activityType = currentActivityRuleList.get(0).getActivityType();
                // 判断活动类型：满减或满量
                ActivityRule activityRule = null;
                if (activityType == ActivityType.FULL_REDUCTION) {
                    activityRule = this.computeFullReduction(activityTotalAmount, currentActivityRuleList);
                } else {
                    activityRule = this.computeFullDiscount(activityTotalNum,activityTotalAmount,currentActivityRuleList);
                }

                // 封装cartInfoVo
                CartInfoVo cartInfoVo = new CartInfoVo();
                cartInfoVo.setActivityRule(activityRule);
                cartInfoVo.setCartInfoList(currentActivityCartInfoList);
                cartInfoVoList.add(cartInfoVo);

                // 记录哪些购物项参与了活动
                activitySkuIdSet.addAll(currentActivitySkuIdSet);
            }
        }

        // 没有参加活动的skuId
        skuIdList.removeAll(activitySkuIdSet);
        if (!CollectionUtils.isEmpty(skuIdList)) {
            // 获取没参加活动的购物项数据
            Map<Long, CartInfo> skuIdCartInfoMap =
                    cartInfoList.stream().collect(Collectors.toMap(CartInfo::getSkuId, CartInfo -> CartInfo));
            for (Long skuId : skuIdList) {
                CartInfoVo cartInfoVo = new CartInfoVo();
                cartInfoVo.setActivityRule(null);// 没有活动

                List<CartInfo> cartInfos = new ArrayList<>();
                cartInfos.add(skuIdCartInfoMap.get(skuId));
                cartInfoVo.setCartInfoList(cartInfos);

                // 将结果存入 cartInfoVoList
                cartInfoVoList.add(cartInfoVo);
            }

        }

        return cartInfoVoList;
    }

    /**
     * 计算满量打折最优规则
     * @param totalNum
     * @param activityRuleList //该活动规则skuActivityRuleList数据，已经按照优惠折扣从大到小排序了
     */
    private ActivityRule computeFullDiscount(Integer totalNum, BigDecimal totalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            //如果订单项购买个数大于等于满减件数，则优化打折
            if (totalNum.intValue() >= activityRule.getConditionNum()) {
                BigDecimal skuDiscountTotalAmount = totalAmount.multiply(activityRule.getBenefitDiscount().divide(new BigDecimal("10")));
                BigDecimal reduceAmount = totalAmount.subtract(skuDiscountTotalAmount);
                activityRule.setReduceAmount(reduceAmount);
                optimalActivityRule = activityRule;
                break;
            }
        }
        if(null == optimalActivityRule) {
            //如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size()-1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionNum())
                    .append("元打")
                    .append(optimalActivityRule.getBenefitDiscount())
                    .append("折，还差")
                    .append(totalNum-optimalActivityRule.getConditionNum())
                    .append("件");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionNum())
                    .append("元打")
                    .append(optimalActivityRule.getBenefitDiscount())
                    .append("折，已减")
                    .append(optimalActivityRule.getReduceAmount())
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }

    /**
     * 计算满减最优规则
     * @param totalAmount
     * @param activityRuleList //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
     */
    private ActivityRule computeFullReduction(BigDecimal totalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            //如果订单项金额大于等于满减金额，则优惠金额
            if (totalAmount.compareTo(activityRule.getConditionAmount()) > -1) {
                //优惠后减少金额
                activityRule.setReduceAmount(activityRule.getBenefitAmount());
                optimalActivityRule = activityRule;
                break;
            }
        }
        if(null == optimalActivityRule) {
            //如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size()-1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionAmount())
                    .append("元减")
                    .append(optimalActivityRule.getBenefitAmount())
                    .append("元，还差")
                    .append(totalAmount.subtract(optimalActivityRule.getConditionAmount()))
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionAmount())
                    .append("元减")
                    .append(optimalActivityRule.getBenefitAmount())
                    .append("元，已减")
                    .append(optimalActivityRule.getReduceAmount())
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }

    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if(cartInfo.getIsChecked().intValue() == 1) {
                BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    private int computeCartNum(List<CartInfo> cartInfoList) {
        int total = 0;
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if(cartInfo.getIsChecked().intValue() == 1) {
                total += cartInfo.getSkuNum();
            }
        }
        return total;
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

package com.pdd.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pdd.activity.mapper.CouponInfoMapper;
import com.pdd.activity.mapper.CouponRangeMapper;
import com.pdd.activity.mapper.CouponUseMapper;
import com.pdd.activity.service.CouponInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pdd.client.product.ProductFeignClient;
import com.pdd.enums.CouponRangeType;
import com.pdd.enums.CouponStatus;
import com.pdd.model.activity.CouponInfo;
import com.pdd.model.activity.CouponRange;
import com.pdd.model.activity.CouponUse;
import com.pdd.model.order.CartInfo;
import com.pdd.model.product.Category;
import com.pdd.model.product.SkuInfo;
import com.pdd.vo.activity.CouponRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sun.awt.IconInfo;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 优惠券信息 服务实现类
 * </p>
 *
 * @author pdd
 * @since 2024-12-18
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {

    @Autowired
    private CouponRangeMapper couponRangeMapper;

    @Autowired
    private CouponUseMapper couponUseMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    // 1 优惠券的分页查询
    @Override
    public IPage<CouponInfo> selectPageCouponInfo(Long page, Long limit) {
        Page<CouponInfo> pageParam = new Page<>(page, limit);
        IPage<CouponInfo> couponInfoPage = baseMapper.selectPage(pageParam, null);
        List<CouponInfo> couponInfoList = couponInfoPage.getRecords();
        couponInfoList.stream().forEach(item -> {
            // 设置优惠券类型
            item.setCouponTypeString(item.getCouponType().getComment());
            // 设置优惠券使用范围
            CouponRangeType rangeType = item.getRangeType();
            if (rangeType != null) {
                item.setRangeTypeString(rangeType.getComment());
            }
        });
        return couponInfoPage;
    }

    // 3 根据id查询优惠券
    @Override
    public CouponInfo getCouponInfo(Long id) {
        CouponInfo couponInfo = baseMapper.selectById(id);
        // 补全优惠券的类型信息
        couponInfo.setCouponTypeString(couponInfo.getCouponType().getComment());
        if (couponInfo.getRangeType() != null) {
            // 补全优惠券使用范围的信息
            couponInfo.setRangeTypeString(couponInfo.getRangeType().getComment());
        }
        return couponInfo;
    }

    // 4 根据优惠券id查询规则数据
    @Override
    public Map<String, Object> findCouponRuleList(Long id) {
        // 第一步：根据优惠券id查询优惠券基本信息 coupon_info 表
        CouponInfo couponInfo = baseMapper.selectById(id);

        // 第二步：根据优惠券id查询 coupon_range 表，查询表中的 range_id 字段
        List<CouponRange> couponRangeList =
                couponRangeMapper.selectList(new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, id));

        // 从 couponRangeList 中获取所有的 range_id
        // 如果规则类型为 sku 则 range_id 就是 skuId
        // 如果规则类型为 category 则 range_id 就是 categoryId
        List<Long> rangeIdList = couponRangeList.stream().map(CouponRange::getRangeId).collect(Collectors.toList());


        // 第三步：分别判断封装不同的数据
        Map<String, Object> result = new HashMap<>();
        if (!CollectionUtils.isEmpty(rangeIdList)) {
            if (couponInfo.getRangeType() == CouponRangeType.SKU) {
                // 如果规则类型为 sku，得到skuId，远程调用根据多个skuId获取对应的sku信息
                List<SkuInfo> skuInfoList =
                        productFeignClient.findSkuInfoList(rangeIdList);
                result.put("skuInfoList", skuInfoList);
            } else if (couponInfo.getRangeType() == CouponRangeType.CATEGORY) {
                // 如果规则类型为 category，得到 categoryId，远程调用根据多个categoryId获取对应分类信息
                List<Category> categoryList =
                        productFeignClient.findCategoryList(rangeIdList);
                result.put("categoryList", categoryList);
            }
        }

        return result;
    }

    // 5 添加优惠券规则数据
    @Override
    public void saveCouponRule(CouponRuleVo couponRuleVo) {
        // 1：根据优惠券id删除原来的优惠券信息
        couponRangeMapper.delete(
                new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, couponRuleVo.getCouponId()));

        // 更新优惠券基本信息
        CouponInfo couponInfo = baseMapper.selectById(couponRuleVo.getCouponId());
        couponInfo.setRangeType(couponRuleVo.getRangeType());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setAmount(couponRuleVo.getAmount());
        couponInfo.setRangeDesc(couponRuleVo.getRangeDesc());

        baseMapper.updateById(couponInfo);

        // 添加优惠券新的规则数据
        List<CouponRange> couponRangeList = couponRuleVo.getCouponRangeList();
        for (CouponRange couponRange : couponRangeList) {
            couponRange.setCouponId(couponRuleVo.getCouponId());
            // 插入数据
            couponRangeMapper.insert(couponRange);
        }
    }

    // 根据关键字获取sku列表，活动使用
    @Override
    public List<CouponInfo> findCouponByKeyword(String keyword) {
        LambdaQueryWrapper<CouponInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(CouponInfo::getCouponName, keyword);
        List<CouponInfo> list = baseMapper.selectList(wrapper);
        return list;
    }

    // 根据skuId和userId查询优惠券信息
    @Override
    public List<CouponInfo> findCouponInfoList(Long skuId, Long userId) {
        // 远程调用：根据skuId查询skuInfo
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        // 根据条件查询优惠券
        List<CouponInfo> couponInfoList = baseMapper.selectCouponInfoList(skuId, skuInfo.getCategoryId(), userId);

        return couponInfoList;
    }


    // 3 获取购物车可以使用的优惠券列表
    @Override
    public List<CouponInfo> findCartCouponInfo(List<CartInfo> cartInfoList, Long userId) {
        // 1 根据userId获取用户的全部优惠券
        List<CouponInfo> userAllCouponInfoList =  baseMapper.selectCartCouponInfoList(userId);
        if (CollectionUtils.isEmpty(userAllCouponInfoList)) {
            return new ArrayList<CouponInfo>();
        }

        // 2 从第一步中获取的优惠券集合中获取所有优惠券id
        List<Long> couponIdList =
                userAllCouponInfoList.stream().map(couponInfo -> couponInfo.getId()).collect(Collectors.toList());

        // 3 查询优惠券对应的使用范围
        LambdaQueryWrapper<CouponRange> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CouponRange::getCouponId,couponIdList);
        List<CouponRange> couponRangeList = couponRangeMapper.selectList(wrapper);

        // 4 获取优惠券id对应的skuId列表
        // 对优惠券进行分组得到map集合
        Map<Long,List<Long>> couponIdToSkuIdMap = this.findCouponIdToSkuIdMap(cartInfoList, couponRangeList);

        // 5 遍历全部优惠券集合，判断优惠券类型
        // 全场通用或者是对应的商品优惠
        BigDecimal reduceAmount = new BigDecimal(0);
        // 记录最优的优惠券
        CouponInfo optimalCouponInfo = null;
        for(CouponInfo couponInfo : userAllCouponInfoList) {
            if(CouponRangeType.ALL == couponInfo.getRangeType()) {
                //全场通用
                //判断是否满足优惠使用门槛
                //计算购物车商品的总价
                BigDecimal totalAmount = computeTotalAmount(cartInfoList);
                if(totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0){
                    couponInfo.setIsSelect(1);
                }
            } else {
                //优惠券id对应的满足使用范围的购物项skuId列表
                List<Long> skuIdList = couponIdToSkuIdMap.get(couponInfo.getId());
                //当前满足使用范围的购物项
                List<CartInfo> currentCartInfoList = cartInfoList.stream().filter(cartInfo -> skuIdList.contains(cartInfo.getSkuId())).collect(Collectors.toList());
                BigDecimal totalAmount = computeTotalAmount(currentCartInfoList);
                if(totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0){
                    couponInfo.setIsSelect(1);
                }
            }
            if (couponInfo.getIsSelect().intValue() == 1 && couponInfo.getAmount().subtract(reduceAmount).doubleValue() > 0) {
                reduceAmount = couponInfo.getAmount();
                optimalCouponInfo = couponInfo;
            }
        }
        if(null != optimalCouponInfo) {
            optimalCouponInfo.setIsOptimal(1);
        }

        // 6 返回结果集合
        return userAllCouponInfoList;
    }

    // 获取购物车的优惠券
    @Override
    public CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId) {

        // 根据优惠券id查询
        CouponInfo couponInfo = baseMapper.selectById(couponId);
        if (couponInfo == null) {
            return null;
        }

        // 根据couponId查询对应的CouponRange数据
        List<CouponRange> couponRangeList =
                couponRangeMapper.selectList(new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, couponId));

        // 获取对应的sku信息
        // List<Long> 就是对应的sku信息
        Map<Long, List<Long>> couponIdToSkuIdMap = this.findCouponIdToSkuIdMap(cartInfoList, couponRangeList);

        // 遍历map集合，获取skuIdList，封装到couponInfo对象
        List<Long> skuIdList = couponIdToSkuIdMap.entrySet().iterator().next().getValue();
        couponInfo.setSkuIdList(skuIdList);

        return couponInfo;
    }

    // 更新优惠券使用状态
    @Override
    public void updateCouponInfoUseStatus(Long couponId, Long userId, Long orderId) {
        //根据 couponId 查询优惠券信息
        CouponUse couponUse = couponUseMapper.selectOne(new LambdaQueryWrapper<CouponUse>()
                .eq(CouponUse::getCouponId, couponId)
                .eq(CouponUse::getUserId, userId)
                .eq(CouponUse::getOrderId, orderId));

        if (couponUse == null) {
            return;
        }
        // 设置修改值
        couponUse.setCouponStatus(CouponStatus.USED);

        // 调用方法进行修改
        couponUseMapper.updateById(couponUse);
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

    // 4 获取优惠券id对应的skuId列表
    private Map<Long, List<Long>> findCouponIdToSkuIdMap(List<CartInfo> cartInfoList, List<CouponRange> couponRangeList) {
        Map<Long, List<Long>> couponIdToSkuIdMap = new HashMap<>();

        // couponRangeList数据处理，根据优惠券进行分组
        Map<Long, List<CouponRange>> couponIdToCouponRangeListMap = couponRangeList.stream()
                .collect(Collectors.groupingBy(couponRange -> couponRange.getCouponId()));
        Iterator<Map.Entry<Long, List<CouponRange>>> iterator = couponIdToCouponRangeListMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, List<CouponRange>> entry = iterator.next();
            Long couponId = entry.getKey();
            List<CouponRange> rangeList = entry.getValue();

            // 创建集合set，对优惠券进行分类
            Set<Long> skuIdSet = new HashSet<>();

            for (CartInfo cartInfo : cartInfoList) {
                for (CouponRange couponRange : rangeList) {
                    //判断
                    if (couponRange.getRangeType() == CouponRangeType.SKU 
                            && couponRange.getRangeId().longValue() == cartInfo.getSkuId().intValue()) {
                        skuIdSet.add(cartInfo.getSkuId());
                    } else if (CouponRangeType.CATEGORY == couponRange.getRangeType()
                            && couponRange.getRangeId().longValue() == cartInfo.getCategoryId().intValue()) {
                        skuIdSet.add(cartInfo.getSkuId());
                    }
                }
            }
            couponIdToSkuIdMap.put(couponId, new ArrayList<>(skuIdSet));
        }

        return couponIdToSkuIdMap;
    }
}

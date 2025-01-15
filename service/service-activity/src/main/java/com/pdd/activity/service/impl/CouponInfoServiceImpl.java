package com.pdd.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pdd.activity.mapper.CouponInfoMapper;
import com.pdd.activity.mapper.CouponRangeMapper;
import com.pdd.activity.service.CouponInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pdd.client.product.ProductFeignClient;
import com.pdd.enums.CouponRangeType;
import com.pdd.model.activity.CouponInfo;
import com.pdd.model.activity.CouponRange;
import com.pdd.model.product.Category;
import com.pdd.model.product.SkuInfo;
import com.pdd.vo.activity.CouponRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sun.awt.IconInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
}

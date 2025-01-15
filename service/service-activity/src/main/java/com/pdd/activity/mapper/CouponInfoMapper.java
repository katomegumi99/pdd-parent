package com.pdd.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pdd.model.activity.CouponInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 优惠券信息 Mapper 接口
 * </p>
 *
 * @author pdd
 * @since 2024-12-18
 */
public interface CouponInfoMapper extends BaseMapper<CouponInfo> {

    // 根据skuId和userId查询优惠券信息
    List<CouponInfo> selectCouponInfoList(@Param("skuId") Long skuId, @Param("categoryId") Long categoryId,
                                          @Param("userId") Long userId);
}

package com.pdd.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pdd.model.activity.ActivityInfo;
import com.pdd.model.activity.ActivityRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 活动表 Mapper 接口
 * </p>
 *
 * @author pdd
 * @since 2024-12-18
 */
public interface ActivityInfoMapper extends BaseMapper<ActivityInfo> {

    List<Long> selectSkuIdListExist(@Param("skuIdList")List<Long> skuIdList);

    List<ActivityRule> findActivityRule(@Param("skuId") Long skuId);
}

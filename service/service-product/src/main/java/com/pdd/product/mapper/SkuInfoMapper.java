package com.pdd.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pdd.model.product.SkuInfo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * sku信息 Mapper 接口
 * </p>
 *
 * @author pdd
 * @since 2024-12-06
 */
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {


    // 解锁库存
    void unlockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    // 查询库存
    SkuInfo checkStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    // 锁定库存
    Integer lockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    // 遍历集合，得到每个对象，进行扣减库存操作
    void minusStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);
}

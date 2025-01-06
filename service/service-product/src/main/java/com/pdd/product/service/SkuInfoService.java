package com.pdd.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pdd.model.product.SkuInfo;
import com.pdd.vo.product.SkuInfoQueryVo;
import com.pdd.vo.product.SkuInfoVo;

import java.util.List;

/**
 * <p>
 * sku信息 服务类
 * </p>
 *
 * @author pdd
 * @since 2024-12-06
 */
public interface SkuInfoService extends IService<SkuInfo> {

    IPage<SkuInfo> selectPageSkuInfo(Page<SkuInfo> pageParam, SkuInfoQueryVo skuInfoQueryVo);

    //商品添加方法
    void saveSkuInfo(SkuInfoVo skuInfoVo);

    SkuInfoVo getSkuInfo(Long id);

    void updateSkuInfo(SkuInfoVo skuInfoVo);

    void check(Long id, Integer status);

    void publish(Long id, Integer status);

    void isNewPerson(Long id, Integer status);

    // 根据skuId列表获取sku信息列表
    List<SkuInfo> findSkuInfoList(List<Long> skuIdList);

    // 根据关键字匹配sku列表
    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    // 获取新人专享的商品数据
    List<SkuInfo> findNewPersonSkuInfoList();

}

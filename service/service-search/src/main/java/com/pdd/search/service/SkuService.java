package com.pdd.search.service;

import com.pdd.model.search.SkuEs;
import com.pdd.vo.search.SkuEsQueryVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SkuService {
    // 上架
    void upperSku(Long skuId);

    // 下架
    void lowerSku(Long skuId);

    // 获取爆品商品
    List<SkuEs> findHotSkuList();

    // 根据商品分类查询商品
    Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo);
}

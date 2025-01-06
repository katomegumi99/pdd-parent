package com.pdd.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pdd.model.product.SkuImage;

import java.util.List;

/**
 * <p>
 * 商品图片 服务类
 * </p>
 *
 * @author pdd
 * @since 2024-12-06
 */
public interface SkuImageService extends IService<SkuImage> {
    // 根据id查询商品图片列表
    List<SkuImage> getImageListBySkuId(Long id);
}

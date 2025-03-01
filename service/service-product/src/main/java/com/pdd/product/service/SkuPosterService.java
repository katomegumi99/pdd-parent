package com.pdd.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pdd.model.product.SkuPoster;

import java.util.List;

/**
 * <p>
 * 商品海报表 服务类
 * </p>
 *
 * @author pdd
 * @since 2024-12-06
 */
public interface SkuPosterService extends IService<SkuPoster> {

    List<SkuPoster> getPosterListBySkuId(Long id);

}

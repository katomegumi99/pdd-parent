package com.pdd.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pdd.model.product.SkuAttrValue;

import java.util.List;

/**
 * <p>
 * spu属性值 服务类
 * </p>
 *
 * @author pdd
 * @since 2024-12-06
 */
public interface SkuAttrValueService extends IService<SkuAttrValue> {

    List<SkuAttrValue> getAttrValueListBySkuId(Long id);
}

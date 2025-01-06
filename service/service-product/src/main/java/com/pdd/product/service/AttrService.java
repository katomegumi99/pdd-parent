package com.pdd.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pdd.model.product.Attr;

import java.util.List;

/**
 * <p>
 * 商品属性 服务类
 * </p>
 *
 * @author pdd
 * @since 2024-12-06
 */
public interface AttrService extends IService<Attr> {

    List<Attr> getAttrListByGroupId(Long groupId);
}

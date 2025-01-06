package com.pdd.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pdd.model.product.AttrGroup;
import com.pdd.vo.product.AttrGroupQueryVo;

import java.util.List;

/**
 * <p>
 * 属性分组 服务类
 * </p>
 *
 * @author pdd
 * @since 2024-12-06
 */
public interface AttrGroupService extends IService<AttrGroup> {
    // 分页获取平台属性分组
    IPage<AttrGroup> selectPageList(Page<AttrGroup> pageParam, AttrGroupQueryVo attrGroupQueryVo);

    // 查询所有平台属性分组列表
    List<AttrGroup> findAllList();

}

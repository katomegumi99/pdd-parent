package com.pdd.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pdd.model.product.Category;
import com.pdd.product.mapper.CategoryMapper;
import com.pdd.product.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pdd.vo.product.CategoryQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 商品三级分类 服务实现类
 * </p>
 *
 * @author pdd
 * @since 2024-12-06
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public IPage<Category> selectPageCategory(Page<Category> pageParam, CategoryQueryVo categoryQueryVo) {
        String name = categoryQueryVo.getName();

        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();

        if (!StringUtils.isEmpty(name)) {
            wrapper.like(Category::getName, name);
        }

        IPage<Category> categoryPage = baseMapper.selectPage(pageParam, wrapper);

        return categoryPage;
    }


}

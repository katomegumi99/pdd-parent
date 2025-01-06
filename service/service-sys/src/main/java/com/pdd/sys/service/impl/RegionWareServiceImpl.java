package com.pdd.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pdd.common.exception.PddException;
import com.pdd.common.result.ResultCodeEnum;
import com.pdd.model.sys.RegionWare;
import com.pdd.sys.mapper.RegionWareMapper;
import com.pdd.sys.service.RegionWareService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pdd.vo.sys.RegionWareQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 城市仓库关联表 服务实现类
 * </p>
 *
 * @author pdd
 * @since 2024-12-03
 */
@Service
public class RegionWareServiceImpl extends ServiceImpl<RegionWareMapper, RegionWare> implements RegionWareService {

    @Override
    public IPage<RegionWare> selectPageRegionWare(Page<RegionWare> pageParam, RegionWareQueryVo regionWareQueryVo) {
        // 1 获取条件值
        String keyword = regionWareQueryVo.getKeyword();

        // 2 判断条件值是否为空
        LambdaQueryWrapper<RegionWare> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(keyword)) {
            // 封装条件为：根据区域名称或者仓库名查询
            wrapper.like(RegionWare::getRegionName, keyword)
                    .or().like(RegionWare::getWareName, keyword);
        }

        // 3 调用方法实现条件分页查询
        IPage<RegionWare> regionWarePage = baseMapper.selectPage(pageParam, wrapper);

        return regionWarePage;
    }

    // 添加开通区域
    @Override
    public void saveRegionWare(RegionWare regionWare) {
        // 判断该区域是否已经开通
        LambdaQueryWrapper<RegionWare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RegionWare::getRegionId, regionWare.getRegionId());
        // 在数据库中查找是否存在该区域
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            // 若count> 0表示已经开通
            // 抛出自定义异常
            throw new PddException(ResultCodeEnum.REGION_OPEN);
        }
        // 若还没开通，则向数据库中添加区域数据
        baseMapper.insert(regionWare);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        RegionWare regionWare = baseMapper.selectById(id);
        regionWare.setStatus(status);
        baseMapper.updateById(regionWare);
    }
}

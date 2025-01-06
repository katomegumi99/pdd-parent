package com.pdd.sys.service;

import com.pdd.model.sys.Region;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 地区表 服务类
 * </p>
 *
 * @author pdd
 * @since 2024-12-03
 */
public interface RegionService extends IService<Region> {

    List<Region> getRegionByKeyword(String keyword);
}

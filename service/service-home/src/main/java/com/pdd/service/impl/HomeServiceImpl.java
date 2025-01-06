package com.pdd.service.impl;

import com.pdd.client.product.ProductFeignClient;
import com.pdd.client.search.SearchFeignClient;
import com.pdd.client.user.UserFeignClient;
import com.pdd.model.product.Category;
import com.pdd.model.product.SkuInfo;
import com.pdd.model.search.SkuEs;
import com.pdd.service.HomeService;
import com.pdd.vo.user.LeaderAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private SearchFeignClient searchFeignClient;
    /**
     * 首页数据显示接口
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> homeData(Long userId) {
        Map<String, Object> result = new HashMap<>();

        // 1.根据 userId 获取当前登录用户的提货地址信息
        // 远程调用 service-user 模块接口获取数据
        LeaderAddressVo leaderAddressVo = userFeignClient.getUserAddressByUserId(userId);
        result.put("leaderAddressVo", leaderAddressVo);

        // 2.获取所有商品的分类信息
        // 远程调用 service-product 模块
        List<Category> categoryList = productFeignClient.findAllCategoryList();
        result.put("categoryList", categoryList);

        // 3.获取新人专享商品
        // 远程调用 service-product 模块
        // 查询前三个
        List<SkuInfo> newPersonSkuInfoList = productFeignClient.findNewPersonSkuInfoList();
        result.put("newPersonSkuInfoList", newPersonSkuInfoList);

        // 4.获取爆款商品信息
        // 远程调用 service-search 模块
        // 以hotScore热度评分降序显示
        // 查询前十个
        List<SkuEs> hotSkuList = searchFeignClient.findHotSkuList();
        result.put("hotSkuList", hotSkuList);

        // 5.将所有数据封装到map集合中进行返回
        return result;
    }
}

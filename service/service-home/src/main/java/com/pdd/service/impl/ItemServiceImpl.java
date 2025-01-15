package com.pdd.service.impl;

import com.pdd.client.activity.ActivityFeignClient;
import com.pdd.client.product.ProductFeignClient;
import com.pdd.client.search.SearchFeignClient;
import com.pdd.service.ItemService;
import com.pdd.vo.product.SkuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private SearchFeignClient searchFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public Map<String, Object> item(Long id, Long userId) {
        Map<String, Object> result = new HashMap<>();

        // 根据skuId查询sku基本信息
        CompletableFuture<SkuInfoVo> skuInfoVoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            // 远程调用获取sku基本信息
            SkuInfoVo skuInfoVo = productFeignClient.getSkuInfoVo(id);
            // 将结果存入总结果集
            result.put("skuInfoVo", skuInfoVo);
            return skuInfoVo;
        }, threadPoolExecutor);

        // sku优惠券信息
        CompletableFuture<Void> activityCompletableFuture = CompletableFuture.runAsync(() -> {
            // 远程调用获取sku优惠券信息
            Map<String, Object> activityMap = activityFeignClient.findActivityAndCoupon(id, userId);
            result.putAll(activityMap);
        }, threadPoolExecutor);

        // 更新商品热度
        CompletableFuture<Void> hotCompletableFuture = CompletableFuture.runAsync(() -> {
            // 远程调用更新商品热度
            searchFeignClient.incrHotScore(id);
        }, threadPoolExecutor);

        // 将任务结果进行组合
        CompletableFuture.allOf(skuInfoVoCompletableFuture,
                activityCompletableFuture,
                hotCompletableFuture
        ).join();

        return result;
    }
}

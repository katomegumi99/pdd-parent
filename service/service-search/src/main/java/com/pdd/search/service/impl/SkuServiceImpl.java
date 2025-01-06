package com.pdd.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pdd.client.activity.ActivityFeignClient;
import com.pdd.client.product.ProductFeignClient;
import com.pdd.common.auth.AuthContextHolder;
import com.pdd.enums.SkuType;
import com.pdd.model.product.Category;
import com.pdd.model.product.SkuInfo;
import com.pdd.model.search.SkuEs;
import com.pdd.search.repository.SkuRepository;
import com.pdd.search.service.SkuService;
import com.pdd.vo.search.SkuEsQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@Service
@Slf4j
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuRepository skuRepository;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    // 上架商品
    @Override
    public void upperSku(Long skuId) {
        // 1 通过远程调用，根据skuId获取相关信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if (skuInfo == null) {
            return;
        }
        Category category = productFeignClient.getCategory(skuInfo.getCategoryId());
        if (category == null) {
            return;
        }

        // 2 将获取的数据封装到SkuEs对象中
        SkuEs skuEs = new SkuEs();

        skuEs.setCategoryId(category.getId());
        skuEs.setCategoryName(category.getName());

        skuEs.setId(skuInfo.getId());
        skuEs.setKeyword(skuInfo.getSkuName()+","+skuEs.getCategoryName());
        skuEs.setWareId(skuInfo.getWareId());
        skuEs.setIsNewPerson(skuInfo.getIsNewPerson());
        skuEs.setImgUrl(skuInfo.getImgUrl());
        skuEs.setTitle(skuInfo.getSkuName());
        if(skuInfo.getSkuType() == SkuType.COMMON.getCode()) {
            skuEs.setSkuType(0);
            skuEs.setPrice(skuInfo.getPrice().doubleValue());
            skuEs.setStock(skuInfo.getStock());
            skuEs.setSale(skuInfo.getSale());
            skuEs.setPerLimit(skuInfo.getPerLimit());
        }else {
            //TODO 待完善-秒杀商品

        }

        // 3 调用方法添加
        SkuEs save = skuRepository.save(skuEs);
        log.info("upperSku："+ JSON.toJSONString(save));
    }

    // 下架商品
    @Override
    public void lowerSku(Long skuId) {
        skuRepository.deleteById(skuId);
    }

    // 获取爆品商品
    @Override
    public List<SkuEs> findHotSkuList() {
        // 分页：0代表第一页
        Pageable pageParam = PageRequest.of(0,10);
        Page<SkuEs> pageModel = skuRepository.findByOrderByHotScoreDesc(pageParam);
        List<SkuEs> skuEsList = pageModel.getContent();
        return skuEsList;
    }

    // 根据商品分类查询商品
    @Override
    public Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo) {
        // 1：向SkuEsQueryVo中设置wareId（当前登录用户的仓库id）
        skuEsQueryVo.setWareId(AuthContextHolder.getWareId());

        // 2：调用SkuRepository方法，按照springData命名规范定义方法进行条件查询
        Page<SkuEs> pageModel = null;
        // 获取搜索的关键词
        String keyword = skuEsQueryVo.getKeyword();
        // 2.1：判断 keyword 是否为空，
        if (StringUtils.isEmpty(keyword)) {
            // 如果为空，则按照仓库id + 分类id进行查询
            pageModel = skuRepository.findByCategoryIdAndWareId(
                    skuEsQueryVo.getCategoryId(),
                    skuEsQueryVo.getWareId(),
                    pageable);
        }else {
            // 2.2：如果keyword不为空，则按照仓库id + keyword进行查询
            pageModel = skuRepository.findByWareIdAndKeyword(
                    skuEsQueryVo.getWareId(),
                    skuEsQueryVo.getKeyword(),
                    pageable);
        }


        // 3：查询商品参加的优惠活动信息
        List<SkuEs> skuEsList = pageModel.getContent();
        // 遍历得到所有skuId
        List<Long> skuIdList = skuEsList.stream()
                .map(item -> item.getId()).collect(Collectors.toList());
        // 根据skuId列表远程调用 service-activity 模块进行查询
        // 返回 Map<Long, List<String>>
        // map集合中的key是sukId的值
        // map集合中的value是sku参与活动的规则列表（活动规则可以有多个，所以使用list集合）
        // todo 远程调用
        Map<Long, List<String>> skuIdToRuleListMap = activityFeignClient.findActivity(skuIdList);
        // 封装得到的数据到skuEs中的 ruleList 属性中
        if (skuIdToRuleListMap != null) {
            skuEsList.forEach(skuEs -> {
                skuEs.setRuleList(skuIdToRuleListMap.get(skuEs.getId()));
            });
        }

        return pageModel;
    }
}

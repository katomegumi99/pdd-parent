package com.pdd.search.repository;

import com.pdd.model.search.SkuEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SkuRepository extends ElasticsearchRepository<SkuEs, Long> {

    // 获取爆品商品
    Page<SkuEs> findByOrderByHotScoreDesc(Pageable pageParam);

    Page<SkuEs> findByCategoryIdAndWareId(Long categoryId, Long wareId, Pageable pageable);

    Page<SkuEs> findByWareIdAndKeyword(Long wareId, String keyword, Pageable pageable);
}

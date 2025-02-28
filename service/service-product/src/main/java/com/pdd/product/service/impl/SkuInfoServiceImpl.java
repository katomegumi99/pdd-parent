package com.pdd.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pdd.common.constant.RedisConst;
import com.pdd.common.exception.PddException;
import com.pdd.common.result.ResultCodeEnum;
import com.pdd.model.product.SkuAttrValue;
import com.pdd.model.product.SkuImage;
import com.pdd.model.product.SkuInfo;
import com.pdd.model.product.SkuPoster;
import com.pdd.mq.constant.MqConst;
import com.pdd.mq.service.RabbitService;
import com.pdd.product.mapper.SkuInfoMapper;
import com.pdd.product.service.SkuAttrValueService;
import com.pdd.product.service.SkuImageService;
import com.pdd.product.service.SkuInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pdd.product.service.SkuPosterService;
import com.pdd.vo.product.SkuInfoQueryVo;
import com.pdd.vo.product.SkuInfoVo;
import com.pdd.vo.product.SkuStockLockVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * sku信息 服务实现类
 * </p>
 *
 * @author pdd
 * @since 2024-12-06
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService {

    // sku海报
    @Autowired
    private SkuPosterService skuPosterService;

    // sku图片
    @Autowired
    private SkuImageService skuImagesService;

    // sku平台属性
    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;


    @Override
    public IPage<SkuInfo> selectPageSkuInfo(Page<SkuInfo> pageParam, SkuInfoQueryVo skuInfoQueryVo) {
        String keyword = skuInfoQueryVo.getKeyword();
        String skuType = skuInfoQueryVo.getSkuType();
        Long categoryId = skuInfoQueryVo.getCategoryId();
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();

        if (!StringUtils.isEmpty(keyword)) {
            wrapper.like(SkuInfo::getSkuName, keyword);
        }
        if (!StringUtils.isEmpty(skuType)) {
            wrapper.eq(SkuInfo::getSkuType, skuType);
        }
        if (!StringUtils.isEmpty(categoryId)) {
            wrapper.like(SkuInfo::getCategoryId, categoryId);
        }

        IPage<SkuInfo> skuInfoPage = baseMapper.selectPage(pageParam, wrapper);

        return skuInfoPage;

    }

    //商品添加方法

    @Override
    public void saveSkuInfo(SkuInfoVo skuInfoVo) {
        // 1 保存sku基本信息
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo, skuInfo);
        baseMapper.insert(skuInfo);

        // 2 保存sku海报
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        if (!CollectionUtils.isEmpty(skuPosterList)) {
            // 由于skuId属性是在第一步保存sku基本信息才生成，所以后续需要逐一添加到sku海报(SkuPoster)中去
            // 遍历向sku海报(SkuPoster)对象中添加 skuId 属性
            for (SkuPoster skuPoster : skuPosterList) {
                skuPoster.setSkuId(skuInfo.getId());
            }
            // 进行批量添加
            skuPosterService.saveBatch(skuPosterList);
        }

        // 3 保存sku图片
        List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        if (!CollectionUtils.isEmpty(skuImagesList)) {
            // 由于skuId属性是在第一步保存sku基本信息才生成，所以后续需要逐一添加到sku图片(SkuImage)中去
            // 遍历向sku图片(SkuImage)对象中添加 skuId 属性
            for (SkuImage skuImage : skuImagesList) {
                skuImage.setSkuId(skuInfo.getId());
            }
            skuImagesService.saveBatch(skuImagesList);
        }

        // 4 保存sku平台属性
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            // 由于skuId属性是在第一步保存sku基本信息才生成，所以后续需要逐一添加到sku平台属性(SkuAttrValue)中去
            // 遍历向sku平台属性(SkuAttrValue)对象中添加 skuId 属性
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
            }
            skuAttrValueService.saveBatch(skuAttrValueList);

        }
    }

    @Override
    public SkuInfoVo getSkuInfo(Long id) {
        SkuInfoVo skuInfoVo = new SkuInfoVo();

        // 根据id查询sku基本信息
        SkuInfo skuInfo = baseMapper.selectById(id);

        // 根据id查询商品图片列表
        List<SkuImage> skuImageList = skuImagesService.getImageListBySkuId(id);

        // 根据id查询商品海报信息
        List<SkuPoster> skuPosterList = skuPosterService.getPosterListBySkuId(id);

        // 根据id查询商品属性信息
        List<SkuAttrValue> skuAttrValueList = skuAttrValueService.getAttrValueListBySkuId(id);

        // 将数据封装并返回
        BeanUtils.copyProperties(skuInfo, skuInfoVo);
        skuInfoVo.setSkuImagesList(skuImageList);
        skuInfoVo.setSkuPosterList(skuPosterList);
        skuInfoVo.setSkuAttrValueList(skuAttrValueList);

        return skuInfoVo;
    }

    @Override
    public void updateSkuInfo(SkuInfoVo skuInfoVo) {
        // 修改sku基本信息
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo, skuInfo);
        baseMapper.updateById(skuInfo);

        // 获取 skuId
        Long skuId = skuInfoVo.getId();

        // 修改海报信息
        // 1.先删除原有的海报信息
        LambdaQueryWrapper<SkuPoster> wrapperSkuPoster = new LambdaQueryWrapper<>();
        wrapperSkuPoster.eq(SkuPoster::getSkuId, skuId);
        skuPosterService.remove(wrapperSkuPoster);
        // 存入新的海报信息
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        if (!CollectionUtils.isEmpty(skuPosterList)) {
            // 遍历，向每一个海报对象添加skuId
            for (SkuPoster skuPoster : skuPosterList) {
                skuPoster.setSkuId(skuId);
            }
            skuPosterService.saveBatch(skuPosterList);
        }

        // 修改图片信息
        // 1.先删除原有的图片信息
        LambdaQueryWrapper<SkuImage> wrapperSkuImage = new LambdaQueryWrapper<>();
        wrapperSkuImage.eq(SkuImage::getSkuId, skuId);
        skuImagesService.remove(wrapperSkuImage);
        // 存入新的图片信息
        List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        if (!CollectionUtils.isEmpty(skuImagesList)) {
            for (SkuImage skuImage : skuImagesList) {
                skuImage.setSkuId(skuId);
            }
            skuImagesService.saveBatch(skuImagesList);
        }

        // 修改商品属性
        // 1.删除原有的商品属性
        LambdaQueryWrapper<SkuAttrValue> wrapperSkuAttrValue = new LambdaQueryWrapper<>();
        wrapperSkuAttrValue.eq(SkuAttrValue::getSkuId, skuId);
        skuAttrValueService.remove(wrapperSkuAttrValue);
        // 添加新的商品属性
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuId);
            }
            skuAttrValueService.saveBatch(skuAttrValueList);
        }

    }

    // 商品审核
    @Override
    public void check(Long id, Integer status) {
        SkuInfo skuInfo = baseMapper.selectById(id);
        skuInfo.setCheckStatus(status);
        baseMapper.updateById(skuInfo);


    }

    // 商品上下架
    @Override
    public void publish(Long id, Integer status) {
        if (status == 1) {
            SkuInfo skuInfo = baseMapper.selectById(id);
            skuInfo.setPublishStatus(status);
            baseMapper.updateById(skuInfo);
            // 商品上架：发送mq消息同步es
            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT, MqConst.ROUTING_GOODS_UPPER, id);
        } else {
            SkuInfo skuInfo = baseMapper.selectById(id);
            skuInfo.setPublishStatus(status);
            baseMapper.updateById(skuInfo);
            // 商品下架：发送mq消息同步es
            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT, MqConst.ROUTING_GOODS_LOWER, id);
        }

    }

    // 新人专享
    @Override
    public void isNewPerson(Long id, Integer status) {
        SkuInfo skuInfo = baseMapper.selectById(id);
        skuInfo.setIsNewPerson(status);
        baseMapper.updateById(skuInfo);
    }

    // 根据skuId列表获取sku信息列表
    @Override
    public List<SkuInfo> findSkuInfoList(List<Long> skuIdList) {
        List<SkuInfo> skuInfoList = baseMapper.selectBatchIds(skuIdList);
        return skuInfoList;
    }

    // 根据关键字匹配sku列表
    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        List<SkuInfo> skuInfoList = baseMapper
                .selectList(new LambdaQueryWrapper<SkuInfo>().like(SkuInfo::getSkuName, keyword));
        return skuInfoList;
    }

    // 获取新人专享的商品数据
    @Override
    public List<SkuInfo> findNewPersonSkuInfoList() {
        // 条件1：is_new_person=1
        // 条件2：publish_status=1
        // 条件3：显示其中三个（以分页的方式）

        // 获取第一页数据，每页显示三条记录
        Page<SkuInfo> pageParam = new Page<>(1, 3);
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuInfo::getIsNewPerson, 1);
        wrapper.eq(SkuInfo::getPublishStatus, 1);
        wrapper.orderByDesc(SkuInfo::getStock);
        IPage<SkuInfo> skuInfoPage = baseMapper.selectPage(pageParam, wrapper);
        List<SkuInfo> skuInfoList = skuInfoPage.getRecords();

        return skuInfoList;
    }

    // 根据skuId获取skuInfo
    @Override
    public SkuInfoVo getSkuInfoVo(Long skuId) {
        SkuInfoVo skuInfoVo = new SkuInfoVo();

        // 获取基本信息
        SkuInfo skuInfo = baseMapper.selectById(skuId);

        // 获取海报信息
        List<SkuPoster> posterList = skuPosterService.getPosterListBySkuId(skuId);

        // 获取属性值
        List<SkuAttrValue> attrValueList = skuAttrValueService.getAttrValueListBySkuId(skuId);

        // 获取图片信息
        List<SkuImage> imageList = skuImagesService.getImageListBySkuId(skuId);

        BeanUtils.copyProperties(skuInfo, skuInfoVo);
        skuInfoVo.setSkuPosterList(posterList);
        skuInfoVo.setSkuAttrValueList(attrValueList);
        skuInfoVo.setSkuImagesList(imageList);

        return skuInfoVo;
    }

    // 验证和锁定库存
    @Override
    public Boolean checkAndLock(List<SkuStockLockVo> skuStockLockVoList, String orderNo) {
        // 1 判断 skuStockLockVoList 是否为空
        if (CollectionUtils.isEmpty(skuStockLockVoList)) {
            throw new PddException(ResultCodeEnum.DATA_ERROR);
        }

        // 2 遍历 skuStockLockVoList 集合得到每个商品，验证库存并锁定库存，具备原子性
        for (SkuStockLockVo skuStockLockVo : skuStockLockVoList) {
            this.checkLock(skuStockLockVo); // 假设checkLock会设置skuStockLockVo的isLock状态
        }

        // 3 只要有一个商品锁定失败，所有锁定成功的商品都解锁
        boolean flag = skuStockLockVoList.stream().anyMatch(skuStockLockVo -> !skuStockLockVo.getIsLock());
        if (flag) {
            // 获取所有锁定成功的商品，遍历解锁库存
            skuStockLockVoList.stream().filter(skuStockLockVo -> skuStockLockVo.getIsLock()).forEach(skuStockLockVo -> {
                // 解锁库存
                baseMapper.unlockStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
            });
            // 响应锁定状态
            return false;
        }

        // 4 如果所有商品都锁定成功了，redis则缓存相关数据，为了方便后面解锁和减库存
        this.redisTemplate.opsForValue().set(RedisConst.STOCK_INFO + orderNo, skuStockLockVoList);

        return true;
    }

    // 扣减库存成功，更新订单状态
    @Override
    public void minusStock(String orderNo) {
        // 从redis中获取锁定库存的信息
        List<SkuStockLockVo> skuStockLockVoList =
                (List<SkuStockLockVo>) redisTemplate.opsForValue().get(RedisConst.STOCK_INFO + orderNo);

        if (CollectionUtils.isEmpty(skuStockLockVoList)) {
            return;
        }

        // 遍历集合，得到每个对象，进行扣减库存操作
        skuStockLockVoList.forEach(skuStockLockVo -> {
            baseMapper.minusStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
        });

        // 删除 redis 数据
        redisTemplate.delete(RedisConst.STOCK_INFO + orderNo);
    }

    // 验证库存并锁定
    private void checkLock(SkuStockLockVo skuStockLockVo) {
        //公平锁，就是保证客户端获取锁的顺序，跟他们请求获取锁的顺序，是一样的。
        // 公平锁需要排队
        // ，谁先申请获取这把锁，
        // 谁就可以先获取到这把锁，是按照请求的先后顺序来的。
        RLock rLock = this.redissonClient.getFairLock(RedisConst.SKUKEY_PREFIX + skuStockLockVo.getSkuId());
        rLock.lock();

        try {
            // 验库存：查询，返回的是满足要求的库存列表
            SkuInfo skuInfo = baseMapper.checkStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
            // 如果没有一个仓库满足要求，这里就验库存失败
            if (skuInfo == null) {
                skuStockLockVo.setIsLock(false);
                return;
            }
            // 反之则进行锁库存操作
            Integer rows = baseMapper.lockStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
            if (rows == 1) {
                skuStockLockVo.setIsLock(true);
            }
        } finally {
            rLock.unlock();
        }
    }
}

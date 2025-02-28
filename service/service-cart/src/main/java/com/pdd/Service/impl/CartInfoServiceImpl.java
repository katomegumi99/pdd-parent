package com.pdd.Service.impl;

import com.pdd.Service.CartInfoService;
import com.pdd.client.product.ProductFeignClient;
import com.pdd.common.auth.AuthContextHolder;
import com.pdd.common.constant.RedisConst;
import com.pdd.common.exception.PddException;
import com.pdd.common.result.Result;
import com.pdd.common.result.ResultCodeEnum;
import com.pdd.enums.SkuType;
import com.pdd.model.order.CartInfo;
import com.pdd.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@Service
public class CartInfoServiceImpl implements CartInfoService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductFeignClient productFeignClient;

    // 返回购物车在redis的key
    private String getCartKey(Long userId) {
        // user:userId:cart
        return RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
    }

    // 添加商品到购物车
    @Override
    public void addToCart(Long userId, Long skuId, Integer skuNum) {

        // 1 因为购物车数据存储在redis里面
        // 所以从redis中根据key获取数据，这个key包含userId
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> hashOperations =
                redisTemplate.boundHashOps(cartKey);

        // 2 根据第一步查询出来的结果，得到skuId + skuNum 的关系
        // 目的：判断是否是第一次添加该商品到购物车
        // 进行判断，判断查询的结果中是否有skuId
        CartInfo cartInfo = null;
        if (hashOperations.hasKey(skuId.toString())) {
            // 3 如果结果中包含skuId，则不是第一次添加该商品
            // 3.1 根据skuId，获取对应数量，更新数量
            cartInfo = hashOperations.get(skuId.toString());
            // 获取之前的商品数量，然后进行更新操作
            Integer currentSkuNum = cartInfo.getSkuNum() + skuNum;
            if (currentSkuNum < 1) {
                return;
            }

            // 更新cartInfo对象
            cartInfo.setSkuNum(currentSkuNum);
            cartInfo.setCurrentBuyNum(currentSkuNum);

            // 判断商品数量是否大于限购数量
            // 先获取限购数量具体值
            Integer perLimit = cartInfo.getPerLimit();
            if (currentSkuNum > perLimit) {
                throw new PddException(ResultCodeEnum.SKU_LIMIT_ERROR);
            }

            // 默认商品被选中
            cartInfo.setIsChecked(1);
            // 更新时间
            cartInfo.setUpdateTime(new Date());
        } else {
            // 4 反之则是第一次添加
            // 4.1 直接添加
            skuNum = 1;

            // 4.2 通过远程调用通过skuId获取sku信息
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            if (skuInfo == null) {
                throw new PddException(ResultCodeEnum.DATA_ERROR);
            }

            // 4.3 封装cartInfo对象
            cartInfo = new CartInfo();
            cartInfo.setSkuId(skuId);
            cartInfo.setCategoryId(skuInfo.getCategoryId());
            cartInfo.setSkuType(skuInfo.getSkuType());
            cartInfo.setIsNewPerson(skuInfo.getIsNewPerson());
            cartInfo.setUserId(userId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setCurrentBuyNum(skuNum);
            cartInfo.setSkuType(SkuType.COMMON.getCode());
            cartInfo.setPerLimit(skuInfo.getPerLimit());
            cartInfo.setImgUrl(skuInfo.getImgUrl());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setWareId(skuInfo.getWareId());
            cartInfo.setIsChecked(1);
            cartInfo.setStatus(1);
            cartInfo.setCreateTime(new Date());
            cartInfo.setUpdateTime(new Date());

        }

        // 5 更新redis缓存
        hashOperations.put(skuId.toString(), cartInfo);

        // 6 设置有效时间(选做)
        this.setCartExpire(cartKey);
    }

    // 根据skuId删除购物车中的商品信息
    @Override
    public void deleteCart(Long skuId, Long userId) {
        // 从redis中获取数据
        BoundHashOperations<String, String, CartInfo> hashOperations =
                redisTemplate.boundHashOps(this.getCartKey(userId));
        // 确认该商品是否存在购物车中
        if (hashOperations.hasKey(skuId.toString())) {
            // 如果存在购物车中则删除
            hashOperations.delete(skuId.toString());
        }
    }

    // 清空购物车
    @Override
    public void deleteAllCart(Long userId) {
        // 获取 hash 中的 key，即是 cartKey
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> hashOperations =
                redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = hashOperations.values();
        for (CartInfo cartInfo : cartInfoList) {
            hashOperations.delete(cartInfo.getSkuId().toString());
        }
    }

    // 批量删除购物车中的sku
    @Override
    public void batchDeleteCart(List<Long> skuIdList, Long userId) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        skuIdList.forEach(skuId -> {
            hashOperations.delete(skuId.toString());
        });
    }

    // 购物车列表
    @Override
    public List<CartInfo> getCartList(Long userId) {
        // 用于存储获取到的购物车信息
        List<CartInfo> cartInfoList = new ArrayList<>();

        // 判断用户是否为空
        if (StringUtils.isEmpty(userId)) {
            return cartInfoList;
        }

        // 从redis中获取购物车数据
        BoundHashOperations<String, String, CartInfo> boundHashOperations = redisTemplate.boundHashOps(this.getCartKey(userId));
        cartInfoList = boundHashOperations.values();

        // 根据时间做降序排列
        if (!CollectionUtils.isEmpty(cartInfoList)) {
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    return o1.getCreateTime().compareTo(o2.getCreateTime());
                }
            });
        }

        return cartInfoList;

    }

    // 根据skuId选中
    @Override
    public void checkCart(Long userId, Long skuId, Integer isChecked) {
        // 获取cartKey
        String cartKey = this.getCartKey(userId);

        // 获取field-value值
        BoundHashOperations<String, String, CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);

        //根据field（skuId）获取value（cartInfo）
        CartInfo cartInfo = boundHashOperations.get(skuId.toString());
        if (cartInfo != null) {
            cartInfo.setIsChecked(isChecked);
            // 更新
            boundHashOperations.put(skuId.toString(), cartInfo);
            // 设置过期时间
            this.setCartExpire(cartKey);
        }
    }

    // 全选
    @Override
    public void checkAllCart(Long userId, Integer isChecked) {
        // 得到 cartKey
        String cartKey = getCartKey(userId);
        // 获取field-value值
        BoundHashOperations<String,String,CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);
        // 获取购物车的所有值
        List<CartInfo> cartInfoList = boundHashOperations.values();

        // 修改状态
        if (!CollectionUtils.isEmpty(cartInfoList)) {
            cartInfoList.forEach(cartInfo -> {
                cartInfo.setIsChecked(isChecked);
                // 修改redis中的状态
                boundHashOperations.put(cartInfo.getSkuId().toString(),cartInfo);
            });
            // 设置过期时间
            this.setCartExpire(cartKey);
        }
    }

    // 批量选中
    @Override
    public void batchCheckCart(List<Long> skuIdList, Long userId, Integer isChecked) {
        // 获取 cartKey
        String cartKey = getCartKey(userId);
        // 获取field-value值
        BoundHashOperations<String,String,CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);
        skuIdList.forEach(skuId -> {
            CartInfo cartInfo = boundHashOperations.get(skuId.toString());
            cartInfo.setIsChecked(isChecked);
            boundHashOperations.put(cartInfo.getSkuId().toString(),cartInfo);
        });
        // 设置过期时间
        this.setCartExpire(cartKey);
    }

    // 根据用户Id 查询购物车列表
    @Override
    public List<CartInfo> getCartCheckedList(Long userId) {
        // 获取 cartKey
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);
        // 获取所有的购物项
        List<CartInfo> cartInfoList = boundHashOperations.values();
        // 筛选出被选中的购物项
        List<CartInfo> cartInfoListIsChecked = cartInfoList.stream()
                .filter(cartInfo -> {
                    return cartInfo.getIsChecked().intValue() == 1;
                }).collect(Collectors.toList());

        return cartInfoListIsChecked;
    }

    // 根据userId删除选中的购物车记录
    @Override
    public void deleteCartChecked(Long userId) {
        // 1 根据userId查询购物车选中的商品
        List<CartInfo> cartInfoList = this.getCartCheckedList(userId);

        // 2 查询list进行数据处理，得到skuId集合
        List<Long> skuIdList = cartInfoList.stream().map(item -> item.getSkuId()).collect(Collectors.toList());

        // 3 构建 redis 的key值
        // hash类型 (key,field-value)
        String cartKey = this.getCartKey(userId);

        // 4 根据key查询field-value值
        BoundHashOperations<String,String,CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);

        // 5 根据field(skuId)删除 redis中的数据
        skuIdList.forEach(skuId -> {
            hashOperations.delete(skuId.toString());
        });
    }

    // 设置key的有效时间
    private void setCartExpire(String key) {
        redisTemplate.expire(key, RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
    }



}

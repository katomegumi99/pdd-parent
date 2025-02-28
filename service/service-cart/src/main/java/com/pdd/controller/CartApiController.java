package com.pdd.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.pdd.Service.CartInfoService;
import com.pdd.client.activity.ActivityFeignClient;
import com.pdd.client.product.ProductFeignClient;
import com.pdd.common.auth.AuthContextHolder;
import com.pdd.common.result.Result;
import com.pdd.model.order.CartInfo;
import com.pdd.vo.order.OrderConfirmVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    @Autowired
    private CartInfoService cartInfoService;

    @Autowired
    private ActivityFeignClient activityFeignClient;


    // 根据skuId选中
    @GetMapping("checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable("skuId") Long skuId,
                            @PathVariable("isChecked") Integer isChecked) {
        // 获取 userId
        Long userId = AuthContextHolder.getUserId();
        //调用方法
        cartInfoService.checkCart(userId, skuId, isChecked);
        return Result.ok(null);
    }

    // 全选
    @GetMapping("checkAllCart/{isChecked}")
    public Result checkAllCart(@PathVariable("isChecked") Integer isChecked) {
        // 获取 userId
        Long userId = AuthContextHolder.getUserId();
        // 调用方法
        cartInfoService.checkAllCart(userId,isChecked);
        return Result.ok(null);
    }

    // 批量选中
    @ApiOperation(value="批量选择购物车")
    @PostMapping("batchCheckCart/{isChecked}")
    public Result batchCheckCart(@RequestBody List<Long> skuIdList,
                                 @PathVariable(value = "isChecked") Integer isChecked) {
        Long userId = AuthContextHolder.getUserId();
        cartInfoService.batchCheckCart(skuIdList,userId,isChecked);
        return Result.ok(null);
    }

    // 添加商品到购物车
    @GetMapping("addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable("skuId") Long skuId,
                            @PathVariable("skuNum") Integer skuNum) {
        //获取当前登录用户Id
        Long userId = AuthContextHolder.getUserId();
        cartInfoService.addToCart(userId,skuId,skuNum);
        return Result.ok(null);
    }

    // 根据skuId删除购物车中的商品信息
    @DeleteMapping("deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId") Long skuId) {
        Long userId = AuthContextHolder.getUserId();
        cartInfoService.deleteCart(skuId,userId);
        return Result.ok(null);
    }

    // 清空购物车
    @DeleteMapping("deleteAllCart")
    public Result deleteAllCart() {
        Long userId = AuthContextHolder.getUserId();
        cartInfoService.deleteAllCart(userId);
        return Result.ok(null);
    }

    // 批量删除购物车中的sku
    @DeleteMapping("batchDeleteCart")
    public Result batchDeleteCart(@RequestBody List<Long> skuIdList) {
        Long userId = AuthContextHolder.getUserId();
        cartInfoService.batchDeleteCart(skuIdList,userId);
        return Result.ok(null);
    }

    // 购物车列表
    @GetMapping("cartList")
    public Result cartList() {
        // 获取userId
        Long userId = AuthContextHolder.getUserId();
        List<CartInfo> cartInfoList = cartInfoService.getCartList(userId);
        return Result.ok(cartInfoList);
    }

    // 查询购物车中的优惠信息
    @GetMapping("activityCartList")
    public Result activityCartList() {
        // 获取用户id
        Long userId = AuthContextHolder.getUserId();
        List<CartInfo> cartInfoList = cartInfoService.getCartList(userId);

        OrderConfirmVo orderTradeVo = activityFeignClient.findCartActivityAndCoupon(cartInfoList, userId);
        return Result.ok(orderTradeVo);
    }

    /**
     * 根据用户Id 查询购物车列表
     *
     * @param userId
     * @return
     */
    @GetMapping("inner/getCartCheckedList/{userId}")
    public List<CartInfo> getCartCheckedList(@PathVariable("userId") Long userId) {
        return cartInfoService.getCartCheckedList(userId);
    }
}

package com.pdd.controller;

import com.pdd.common.auth.AuthContextHolder;
import com.pdd.common.result.Result;
import com.pdd.service.ItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author youzairichangdawang
 * @version 1.0
 */

@Api(tags = "商品详情")
@RestController
@RequestMapping("api/home")
public class ItemApiController {

    @Autowired
    private ItemService itemService;

    @ApiOperation(value = "获取sku详细信息")
    @GetMapping("item/{id}")
    public Result index(@PathVariable("id") Long id, HttpServletRequest request) {
        // 获取用户id
        Long userId = AuthContextHolder.getUserId();

        Map<String, Object> map = itemService.item(id, userId);
        return Result.ok(map);
    }
}

package com.pdd.controller;

import com.pdd.common.auth.AuthContextHolder;
import com.pdd.common.result.Result;
import com.pdd.service.HomeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@RestController
@EnableFeignClients
@EnableDiscoveryClient
@RequestMapping("/api/home")
public class HomeApiController {

    @Autowired
    private HomeService homeService;

    @ApiOperation("首页数据显示接口")
    @GetMapping("index")
    public Result index(HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId();
        Map<String, Object> map = homeService.homeData(userId);
        return Result.ok(map);
    }
}

package com.pdd.acl.controller;

import com.pdd.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author youzairichangdawang
 * @version 1.0
 */

@Api(tags = "登录接口")
@RestController
@RequestMapping("/admin/acl/index")
// // 用于解决跨域问题的注解
public class IndexController {

    /**
     * 参照前端代码
     * export function login({ username, password }) {
     *   return request({
     *     url: '/admin/acl/index/login',
     *     method: 'post',
     *     data: { username, password }
     *   })
     * }
     * @return
     */
    // 1.login登录
    @PostMapping("login")
    @ApiOperation("登录")
    public Result login() {
        // 返回token值
        Map<String, Object> map = new HashMap<>();
        map.put("token","admin-token");
        return Result.ok(map);
    }

    /**
     * 参照前端代码
     * export function getInfo() {
     *   return request({
     *     url: '/admin/acl/index/info',
     *     method: 'get'
     *   })
     * }
     * @return
     */
    // 2.getInfo获取信息
    @GetMapping("info")
    @ApiOperation("获取信息")
    public Result info() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "admin");
        map.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return Result.ok(map);
    }


    /**
     * 参照前端的代码进行开发
     * export function logout() {
     *   return request({
     *     url: '/admin/acl/index/logout',
     *     method: 'post'
     *   })
     * }
     *
     */
    // 3.logout退出
    @PostMapping("logout")
    @ApiOperation("退出登录")
    public Result logout() {
        return Result.ok(null);
    }
}

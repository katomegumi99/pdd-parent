package com.pdd.acl.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pdd.acl.service.AdminService;
import com.pdd.acl.service.RoleService;
import com.pdd.common.result.Result;
import com.pdd.common.utils.MD5;
import com.pdd.model.acl.Admin;
import com.pdd.vo.acl.AdminQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@Api(tags = "用户接口")
@RestController
//
@RequestMapping("/admin/acl/user")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    // 1.查询用户列表
    @ApiOperation("查询用户列表")
    @GetMapping("{current}/{limit}")
    public Result list(@PathVariable("current") Long current,
                       @PathVariable("limit") Long limit,
                       AdminQueryVo adminQueryVo) {
        Page<Admin> pageParam = new Page<>(current, limit);
        IPage<Admin> pageModel = adminService.selectPageUser(pageParam, adminQueryVo);

        return Result.ok(pageModel);
    }

    // 2.根据id查询用户
    @ApiOperation("根据id查询用户")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable("id") Long id) {
        Admin admin = adminService.getById(id);
        return Result.ok(admin);
    }

    // 3.添加用户
    @ApiOperation("添加用户")
    @PostMapping("save")
    public Result add(@RequestBody Admin admin) {
        // 获取输入的密码
        String password = admin.getPassword();

        // 对输入的密码进行加密 一般使用md5算法
        String passwordMD5 = MD5.encrypt(password);

        // 设置到admin对象中去
        admin.setPassword(passwordMD5);

        // 调用方法添加
        adminService.save(admin);

        return Result.ok(null);
    }

    // 4.修改用户
    @ApiOperation("修改用户")
    @PutMapping("update")
    public Result update(@RequestBody Admin admin) {
        adminService.updateById(admin);
        return Result.ok(null);
    }

    // 5.根据id删除用户
    @ApiOperation("根据id删除用户")
    @DeleteMapping("remove/{id}")
    public Result removeById(@PathVariable("id") Long id) {
        adminService.removeById(id);
        return Result.ok(null);
    }

    // 6.批量删除用户
    @ApiOperation("批量删除用户")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        adminService.removeByIds(idList);
        return Result.ok(null);
    }

    // 7.查询所有角色列表和根据用户id获取用户已经被分配的角色的列表
    @ApiOperation("获取用户角色")
    @GetMapping("toAssign/{adminId}")
    public Result toAssign(@PathVariable("adminId") Long adminId) {
        // 返回map集合包含两项数据：所有用户和位用户分配的角色列表
        Map<String, Object> map = roleService.getRoleByAdminId(adminId);
        return Result.ok(map);
    }

    // 8.为用户进行角色分配
    @ApiOperation("为用户进行角色分配")
    @PostMapping("doAssign")
    public Result doAssign(@RequestParam Long adminId,
                           @RequestParam Long[] roleId) {
        roleService.saveAdminRole(adminId, roleId);
        return Result.ok(null);
    }
}

package com.pdd.acl.controller;

import com.pdd.acl.service.PermissionService;
import com.pdd.common.result.Result;
import com.pdd.model.acl.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@RestController
@RequestMapping("/admin/acl/permission")
@Api(tags = "菜单管理")
// //跨域
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    // 查询所有菜单
    @ApiOperation("查询所有菜单")
    @GetMapping
    public Result list() {
        List<Permission> list = permissionService.queryAllPermission();
        return Result.ok(list);
    }

    // 添加菜单
    @ApiOperation("添加菜单")
    @PostMapping("save")
    public Result save(@RequestBody Permission permission) {
        permissionService.save(permission);
        return Result.ok(null);
    }

    // 修改菜单
    @ApiOperation("修改菜单")
    @PutMapping("update")
    public Result update(@RequestBody Permission permission) {
        permissionService.updateById(permission);
        return Result.ok(null);
    }

    // 递归删除菜单
    @ApiOperation("递归删除菜单")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable("id") Long id) {
        permissionService.removeChildById(id);
        return Result.ok(null);
    }

    /**
     * 查询所有菜单列表 根据角色id查询已经分配过的菜单
     *  toAssign(roleId) {
     *     return request({
     *       url: `${api_name}/toAssign/${roleId}`,
     *       method: 'get'
     *     })
     *   },
     */
    @ApiOperation("根据角色id查询已经分配过的菜单")
    @GetMapping("toAssign/{roleId}")
    public Result toAssign(@PathVariable("roleId") Long roleId) {
        List<Permission> list = permissionService.getPermissionByRoleId(roleId);
        return Result.ok(list);
    }


    // 为用户分配菜单列表
    @ApiOperation("为用户分配菜单列表")
    @PostMapping("doAssign")
    public Result doAssign(@RequestParam Long roleId,
                           @RequestParam Long[] permissionId) {
        permissionService.saveRolePermission(roleId, permissionId);
        return Result.ok(null);
    }
}

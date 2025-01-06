package com.pdd.acl.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pdd.common.result.Result;
import com.pdd.model.acl.Role;
import com.pdd.acl.service.RoleService;
import com.pdd.vo.acl.RoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@Api(tags = "角色接口")
@RestController
//
@Slf4j
@RequestMapping("/admin/acl/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * getPageList(page, limit, searchObj) {
     *     return request({
     *       url: `${api_name}/${page}/${limit}`,
     *       method: 'get',
     *       params: searchObj // url查询字符串或表单键值对
     *     })
     *   },
     *   当前端使用 params 传输数据时，表示是使用 roleQueryVo 传递数据
     * @param current
     * @param limit
     * @param roleQueryVo
     * @return
     */
    // 1.角色条件分页查询
    @ApiOperation("角色条件分页查询")
    @GetMapping("{current}/{limit}")
    public Result pageList(@PathVariable("current") Long current,
                           @PathVariable("limit") Long limit,
                           RoleQueryVo roleQueryVo) {
        // 1.创建page对象，传入当前页和每页记录数
        Page<Role> pageParam = new Page<>(current, limit);
        // 2.调用service方法实现条件分页查询，返回分页对象
        IPage<Role> pageModel = roleService.selectRolePage(pageParam, roleQueryVo);

        return Result.ok(pageModel);
    }

    // 2.根据id查询角色
    @ApiOperation("根据id查询角色")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable("id") Long id) {
        Role role = roleService.getById(id);
        return Result.ok(role);
    }

    /**
     * save(role) {
     *     return request({
     *       url: `${api_name}/save`,
     *       method: 'post',
     *       data: role
     *     })
     *   },
     *   当前端使用data传输数据时，表示使用的是json格式传递数据
     * @param role
     * @return
     */
    // 3.添加角色
    @ApiOperation("添加角色")
    @PostMapping("save")
    // @RequestBody 表示可以接收前端传来的json数据
    public Result save(@RequestBody Role role) {
        boolean is_success = roleService.save(role);
        if (is_success) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }

    // 4.修改角色
    @ApiOperation("修改角色")
    @PutMapping("update")
    public Result update(@RequestBody Role role) {
        boolean is_success = roleService.updateById(role);
        if (is_success) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }

    // 5.根据id删除角色
    @ApiOperation("根据id删除角色")
    @DeleteMapping("remove/{id}")
    public Result removeById(@PathVariable("id") Long id) {
        boolean is_success = roleService.removeById(id);
        if (is_success) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }

    // 6.批量删除角色
    @ApiOperation("批量删除角色")
    @DeleteMapping("batchRemove")
    // json数组[1,2,3] 对应 Java的List集合
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean is_success = roleService.removeByIds(idList);
        if (is_success) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }
}

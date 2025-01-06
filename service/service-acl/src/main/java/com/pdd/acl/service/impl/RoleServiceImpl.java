package com.pdd.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pdd.acl.service.AdminRoleService;
import com.pdd.acl.service.RoleService;
import com.pdd.acl.mapper.RoleMapper;
import com.pdd.model.acl.AdminRole;
import com.pdd.model.acl.Role;
import com.pdd.vo.acl.RoleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private AdminRoleService adminRoleService;

    // 1.角色列表（条件分页查询）
    @Override
    public IPage<Role> selectRolePage(Page<Role> pageParam, RoleQueryVo roleQueryVo) {
        // 获取条件值
        String roleName = roleQueryVo.getRoleName();

        // 创建条件对象
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();

        // 判断条件值是否为空
        if (!StringUtils.isEmpty(roleName)) {
            //封装条件
            wrapper.like(Role::getRoleName, roleName);
        }

        //调用mapper方法实现条件分页查询
        IPage<Role> rolePage = roleMapper.selectPage(pageParam, wrapper);
        // 也可以使用MybatisPlus自动注入的mapper进行分页查询
        // extends ServiceImpl<RoleMapper, Role> 相当于自动注入 RoleMapper
        //IPage<Role> rolePage = baseMapper.selectPage(pageParam, wrapper);

        // 返回分页对象
        return rolePage;
    }

    /**
     * 查询所有角色列表和根据用户id获取用户已经被分配的角色的列表
     * @param adminId
     * @return
     */
    @Override
    public Map<String, Object> getRoleByAdminId(Long adminId) {
        // 1.查询所有角色
        List<Role> allRolesList = baseMapper.selectList(null);

        // 2.根据用户id查询用户被分配的角色列表
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        // 设置查询条件：根据用户id adminId
        wrapper.eq(AdminRole::getAdminId, adminId);

        // 2.1 根据用户id查询 用户角色关系表 admin_role 查询用户分配的角色id列表
        List<AdminRole> adminRoleList = adminRoleService.list(wrapper);

        // 2.2 通过第一步返回的集合，获取所有角色id列表 List<Long> roleIdsList
        List<Long> roleIdsList =
                adminRoleList.stream()
                        .map(item -> item.getRoleId())
                        .collect(Collectors.toList());

        // 2.3 创建新的集合，用于存储用户已经被分配的角色
        ArrayList<Role> assignRoleList = new ArrayList<>();

        // 2.4 遍历所有角色列表 allRolesList ，得到每个角色
        // 并判断所有角色表里面是否包含了已经分配的角色id，将已经被分配的角色id封装到2.3所创建的新集合 assignRoleList 中
        for (Role role : allRolesList) {
            //判断
            if (roleIdsList.contains(role.getId())) {
                assignRoleList.add(role);
            }
        }
        // 封装到map中
        Map<String, Object> result = new HashMap<>();
        // 所有角色列表
        result.put("allRolesList", allRolesList);
        // 用户已经被分配的角色列表
        result.put("assignRoles", assignRoleList);

        //返回结果
        return result;
    }

    /**
     * 为用户进行角色分配
     * @param adminId
     * @param roleIds
     */
    @Override
    public void saveAdminRole(Long adminId, Long[] roleIds) {
        // 1.删除用户已经分配的角色数据
        // 根据用户id删除admin_role表里面对应的数据
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getAdminId, adminId);
        adminRoleService.remove(wrapper);

        // 2.进行新的分配
        // 遍历传入的角色id，得到每一个传入的角色id，然后以角色id + 用户id的方式依次添加到用户角色关系表中
        List<AdminRole> list = new ArrayList<>();
        for (Long roleId : roleIds) {
            AdminRole adminRole = new AdminRole();
            adminRole.setAdminId(adminId);
            adminRole.setRoleId(roleId);
            list.add(adminRole);
        }
        adminRoleService.saveBatch(list);

    }
}

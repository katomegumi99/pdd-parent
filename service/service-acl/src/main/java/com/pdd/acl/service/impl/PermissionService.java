package com.pdd.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pdd.acl.mapper.PermissionMapper;
import com.pdd.acl.service.RolePermissionService;
import com.pdd.acl.utils.PermissionHelper;
import com.pdd.model.acl.Permission;
import com.pdd.model.acl.RolePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class PermissionService extends ServiceImpl<PermissionMapper, Permission> implements com.pdd.acl.service.PermissionService {

    @Autowired
    private RolePermissionService rolePermissionService;

    /**
     * 查询所有菜单
     *
     * @return
     */
    @Override
    public List<Permission> queryAllPermission() {
        // 1 查询所有菜单
        List<Permission> allPermissionList = baseMapper.selectList(null);

        // 2 转换要求数据格式
        List<Permission> result = PermissionHelper.buildPermission(allPermissionList);
        return result;
    }

    /**
     * 删除菜单
     *
     * @param id
     */
    @Override
    public void removeChildById(Long id) {
        // 1 创建list集合idList，封装所有要删除的菜单的id
        List<Long> idList = new ArrayList<>();

        // 根据当前菜单id，获取当前菜单下面所有的子菜单
        // 如果子菜单下面还有子菜单，都要获取
        // 重点：递归查找当前菜单的子菜单
        this.getAllPermissionId(id, idList);

        // 向集合中添加当前菜单id
        idList.add(id);

        // 2 调用方法根据多个菜单id删除
        baseMapper.deleteBatchIds(idList);
    }

    @Override
    public List<Permission> getPermissionByRoleId(Long roleId) {
        // 1 查询所有菜单
        List<Permission> allPermissionList = baseMapper.selectList(null);

        // 2 根据角色id查询角色已经被分配的菜单列表
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);

        // 2.1 根据角色id查询 角色菜单关系表 查询角色已经被分配的菜单
        List<RolePermission> rolePermissionList = rolePermissionService.list(wrapper);

        // 2.2 通过第一步返回的集合，获取所有菜单id列表
        List<Long> permissionIds =
                rolePermissionList.stream()
                        .map(item -> item.getPermissionId())
                        .collect(Collectors.toList());

        // 2.3 创建新的列表，用于存储角色已经被分配的菜单
        List<Permission> assignPermissionList = new ArrayList<>();

        // 2.4 遍历所有菜单列表，得到每个菜单
        // 并判断所有菜单表里是否包含了已经分配的菜单id，将已经被分配的菜单id封装到2.3所创建的集合中
        for (Permission permission : allPermissionList) {
            // 判断
            if (permissionIds.contains(permission.getId())) {
//                assignPermissionList.add(permission);
                permission.setSelect(true);
            }
        }

        // 将结果转换为目标格式
        List<Permission> allPermissionsList = PermissionHelper.buildPermission(allPermissionList);

        // 返回结果
        return allPermissionsList;
    }

    /**
     * 为角色进行菜单分配
     * @param roleId
     * @param permissionIds
     */
    @Override
    public void saveRolePermission(Long roleId, Long[] permissionIds) {
        // 1 删除角色已经分配的菜单数据
        // 根据角色id删除角色菜单关系表里对应的数据
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        rolePermissionService.remove(wrapper);

        // 2 进行新的分配
        // 遍历传入的菜单id，得到每一个传入的菜单id，然后以菜单id + 角色id的方式一次添加到角色菜单关系表中
        List<RolePermission> list = new ArrayList<>();
        for (Long permissionId : permissionIds) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            list.add(rolePermission);
        }
        rolePermissionService.saveBatch(list);
    }


    /**
     * 重点：递归查找当前菜单的子菜单
     *
     * @param id     当前菜单id
     * @param idList 最终封装的list集合，包含所有菜单id
     */
    private void getAllPermissionId(Long id, List<Long> idList) {
        // 根据当前菜单id查询下面的子菜单
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPid, id);
        List<Permission> childList = baseMapper.selectList(wrapper);

        // 递归查询是否还有子菜单
        childList.stream().forEach(item -> {
            // 封装菜单id到idList里面
            idList.add(item.getPid());
            // 递归查询
            this.getAllPermissionId(item.getPid(), idList);
        });
    }
}

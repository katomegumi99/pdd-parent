package com.pdd.acl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pdd.model.acl.Permission;

import java.util.List;
import java.util.Map;

public interface PermissionService extends IService<Permission> {
    List<Permission> queryAllPermission();

    void removeChildById(Long id);

    List<Permission> getPermissionByRoleId(Long roleId);

    void saveRolePermission(Long roleId, Long[] permissionId);

}

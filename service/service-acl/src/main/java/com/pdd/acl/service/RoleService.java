package com.pdd.acl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pdd.model.acl.Role;
import com.pdd.vo.acl.RoleQueryVo;

import java.util.Map;

public interface RoleService extends IService<Role> {
    IPage<Role> selectRolePage(Page<Role> pageParam, RoleQueryVo roleQueryVo);

    Map<String, Object> getRoleByAdminId(Long adminId);

    void saveAdminRole(Long adminId, Long[] roleIds);
}

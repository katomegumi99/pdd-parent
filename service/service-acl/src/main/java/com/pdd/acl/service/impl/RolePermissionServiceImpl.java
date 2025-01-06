package com.pdd.acl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pdd.acl.mapper.RolePermissionMapper;
import com.pdd.acl.service.RolePermissionService;
import com.pdd.model.acl.RolePermission;
import org.springframework.stereotype.Service;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {
}

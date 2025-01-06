package com.pdd.acl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pdd.acl.mapper.AdminRoleMapper;
import com.pdd.acl.service.AdminRoleService;
import com.pdd.model.acl.AdminRole;
import org.springframework.stereotype.Service;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@Service
public class AdminRoleServiceImpl extends ServiceImpl<AdminRoleMapper, AdminRole> implements AdminRoleService {
}

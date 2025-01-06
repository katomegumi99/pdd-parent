package com.pdd.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pdd.model.user.User;
import com.pdd.vo.user.LeaderAddressVo;
import com.pdd.vo.user.UserLoginVo;

public interface UserService extends IService<User> {
    User getUserByOpenId(String openId);

    // 5.根据userId查询提货点和团长信息
    LeaderAddressVo getLeaderAddressByUserId(Long userId);

    UserLoginVo getUserLoginVo(Long id);
}

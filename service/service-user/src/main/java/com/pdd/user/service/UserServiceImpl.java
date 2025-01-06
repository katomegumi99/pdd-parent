package com.pdd.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pdd.model.user.Leader;
import com.pdd.model.user.User;
import com.pdd.model.user.UserDelivery;
import com.pdd.user.mapper.LeaderMapper;
import com.pdd.user.mapper.UserDeliveryMapper;
import com.pdd.user.mapper.UserMapper;
import com.pdd.vo.user.LeaderAddressVo;
import com.pdd.vo.user.UserLoginVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Autowired
    private UserDeliveryMapper userDeliveryMapper;

    @Autowired
    private LeaderMapper leaderMapper;

    @Override
    public User getUserByOpenId(String openId) {
        User user = baseMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenId, openId));
        return user;
    }

    // 5.根据userId查询提货点和团长信息
    @Override
    public LeaderAddressVo getLeaderAddressByUserId(Long userId) {
        // 根据用户id查出用户默认的团长id
        UserDelivery userDelivery = userDeliveryMapper.selectOne(
                new LambdaQueryWrapper<UserDelivery>()
                        .eq(UserDelivery::getUserId, userId)
                        .eq(UserDelivery::getIsDefault, 1));
        if (userDelivery == null) {
            return null;
        }

        // 根据团长id查询团长信息
        Leader leader = leaderMapper.selectById(userDelivery.getLeaderId());

        LeaderAddressVo leaderAddressVo = new LeaderAddressVo();
        BeanUtils.copyProperties(leader, leaderAddressVo);
        leaderAddressVo.setUserId(userId);
        leaderAddressVo.setLeaderId(leader.getId());
        leaderAddressVo.setLeaderName(leader.getName());
        leaderAddressVo.setLeaderPhone(leader.getPhone());
        leaderAddressVo.setWareId(userDelivery.getWareId());
        leaderAddressVo.setStorePath(leader.getStorePath());

        return leaderAddressVo;
    }

    @Override
    public UserLoginVo getUserLoginVo(Long id) {
        User user = baseMapper.selectById(id);
        UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setNickName(user.getNickName());
        userLoginVo.setUserId(id);
        userLoginVo.setPhotoUrl(user.getPhotoUrl());
        userLoginVo.setOpenId(user.getOpenId());
        userLoginVo.setIsNew(user.getIsNew());

        UserDelivery userDelivery = userDeliveryMapper.selectOne(new LambdaQueryWrapper<UserDelivery>()
                .eq(UserDelivery::getUserId, id)
                .eq(UserDelivery::getIsDefault, 1));

        if (userDelivery != null) {
            userLoginVo.setLeaderId(userDelivery.getLeaderId());
            userLoginVo.setWareId(userDelivery.getWareId());
        }else {
            userLoginVo.setLeaderId(1L);
            userLoginVo.setWareId(1L);
        }
        return userLoginVo;
    }
}

package com.pdd.common.auth;

/**
 * @author youzairichangdawang
 * @version 1.0
 */

import com.pdd.vo.user.UserLoginVo;

/**
 * 获取登录用户信息类
 *
 */
public class AuthContextHolder {

    // 用户id
    private static ThreadLocal<Long> userId = new ThreadLocal<>();

    // 仓库id
    private static ThreadLocal<Long> wareId = new ThreadLocal<>();

    //用户基本信息
    private static ThreadLocal<UserLoginVo> userLoginVo = new ThreadLocal<>();

    public static Long getUserId(){
        return userId.get();
    }

    public static void setUserId(Long _userId){
        userId.set(_userId);
    }

    public static Long getWareId(){
        return wareId.get();
    }

    public static void setWareId(Long _wareId){
        wareId.set(_wareId);
    }

    public static UserLoginVo getUserLoginVo() {
        return userLoginVo.get();
    }

    public static void setUserLoginVo(UserLoginVo _userLoginVo) {
        userLoginVo.set(_userLoginVo);
    }
}

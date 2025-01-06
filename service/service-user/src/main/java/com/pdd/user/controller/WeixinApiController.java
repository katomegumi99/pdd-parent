package com.pdd.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.pdd.common.auth.AuthContextHolder;
import com.pdd.common.constant.RedisConst;
import com.pdd.common.exception.PddException;
import com.pdd.common.result.Result;
import com.pdd.common.result.ResultCodeEnum;
import com.pdd.common.utils.JwtHelper;
import com.pdd.enums.UserType;
import com.pdd.model.user.User;
import com.pdd.user.service.UserService;
import com.pdd.user.utils.ConstantPropertiesUtil;
import com.pdd.user.utils.HttpClientUtils;
import com.pdd.vo.user.LeaderAddressVo;
import com.pdd.vo.user.UserLoginVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author youzairichangdawang
 * @version 1.0
 */

@RestController
@RequestMapping("/api/user/weixin")
public class WeixinApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "微信登录获取openid(小程序)")
    @GetMapping("/wxLogin/{code}")
    public Result loginWx(@PathVariable("code") String code) {
        // 1.得到微信返回code临时票据
        // 2.拿着code + 小程序id + 小程序密钥，请求微信登录的接口服务（使用get请求）
        // 得到小程序id
        String wxOpenAppId = ConstantPropertiesUtil.WX_OPEN_APP_ID;
        // 得到小程序密钥
        String wxOpenAppSecret = ConstantPropertiesUtil.WX_OPEN_APP_SECRET;

        // 拼接请求地址+参数
        StringBuffer url = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/jscode2session")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&js_code=%s")
                .append("&grant_type=authorization_code");
        String tokenUrl = String.format(url.toString(),
                wxOpenAppId,
                wxOpenAppSecret,
                code);
        // 使用HttpClient工具发送get请求
        String result = null;
        try {
            result = HttpClientUtils.get(tokenUrl);
        } catch (Exception e) {
            throw new PddException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        // 3.请求微信登录接口服务，返回两个值：session_key 和 openId
        // openId是微信号的唯一标识
        JSONObject jsonObject = JSONObject.parseObject(result);
        String session_key = jsonObject.getString("session_key");
        String openId = jsonObject.getString("openid");

        // 4.添加微信用户信息到数据库中
        // 根据openId获取用户信息
        User user = userService.getUserByOpenId(openId);
        System.out.println(" openid is :"+ openId);
        // 判断用户是否是第一次使用微信授权登录，使用openId判断
        if (user == null) {
            user = new User();
            user.setOpenId(openId);
            user.setNickName(openId);
            user.setPhotoUrl("");
            user.setUserType(UserType.USER);
            user.setIsNew(0);
            userService.save(user);
        }

        // 5.根据userId查询提货点和团长信息
        // 提货点 user 表 user_delivery 表
        // 团长 leader 表
        LeaderAddressVo leaderAddressVo =
                userService.getLeaderAddressByUserId(user.getId());

        // 6.使用jwt工具根据userId和username生成token字符串
        String token = JwtHelper.createToken(user.getId(), user.getNickName());

        // 7.根据用户id获取当前登录用户信息，放到redis里面，设置有效时长
        UserLoginVo userLoginVo = userService.getUserLoginVo(user.getId());
        redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX + user.getId(),
                userLoginVo,
                RedisConst.USERKEY_TIMEOUT,
                TimeUnit.DAYS);

        // 8.将需要的数据封装到map集合中并返回
        HashMap<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("token", token);
        map.put("leaderAddressVo", leaderAddressVo);

        return Result.ok(map);
    }

    @PostMapping("/auth/updateUser")
    @ApiOperation(value = "更新用户昵称与头像")
    public Result updateUser(@RequestBody User user) {
        User user1 = userService.getById(AuthContextHolder.getUserId());
        //把昵称更新为微信用户
        user1.setNickName(user.getNickName().replaceAll("[ue000-uefff]", "*"));
        user1.setPhotoUrl(user.getPhotoUrl());
        userService.updateById(user1);
        return Result.ok(null);
    }
}

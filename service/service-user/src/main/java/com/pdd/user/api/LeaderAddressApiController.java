package com.pdd.user.api;

import com.pdd.user.service.UserService;
import com.pdd.vo.user.LeaderAddressVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@RestController
@RequestMapping("/api/user/leader")
public class LeaderAddressApiController {

    @Autowired
    private UserService userService;

    @ApiOperation("提货点地址信息")
    @GetMapping("/inner/getUserAddressByUserId/{userId}")
    public LeaderAddressVo getUserAddressByUserId(@PathVariable("userId") Long userId) {
        LeaderAddressVo leaderAddressVo = userService.getLeaderAddressByUserId(userId);
        return leaderAddressVo;
    }

}

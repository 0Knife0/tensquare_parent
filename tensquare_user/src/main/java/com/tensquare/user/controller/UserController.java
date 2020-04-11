package com.tensquare.user.controller;

import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import com.tensquare.user.pojo.User;
import com.tensquare.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    // 根据id查询用户
    // GET 127.0.0.1:9008/user/1
    @GetMapping(value = "{userId}")
    public Result selectById(@PathVariable String userId) {
        User user = userService.selectById(userId);

        return new Result(true, StatusCode.OK, "查询成功", user);
    }
}

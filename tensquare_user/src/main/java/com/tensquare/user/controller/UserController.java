package com.tensquare.user.controller;

import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import com.tensquare.user.pojo.User;
import com.tensquare.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;


    // 登录
    @PostMapping(value = "login")
    public Result login(@RequestBody User user) {
        User result = userService.login(user);
        if (result != null) {
            return new Result(true, StatusCode.OK, "登录成功", result);
        }
        return new Result(false, StatusCode.OK, "登录失败");
    }

    // 根据id查询用户
    // GET 127.0.0.1:9008/user/1
    @GetMapping(value = "{userId}")
    public Result selectById(@PathVariable String userId) {
        User user = userService.selectById(userId);

        return new Result(true, StatusCode.OK, "查询成功", user);
    }
}

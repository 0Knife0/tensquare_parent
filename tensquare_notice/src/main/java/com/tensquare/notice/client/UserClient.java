package com.tensquare.notice.client;

import com.tensquare.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("tensquare-user")
public interface UserClient {

    // 根据id查询用户
    // GET 127.0.0.1:9008/user/1
    @GetMapping(value = "user/{userId}")
    public Result selectById(@PathVariable("userId") String userId);
}

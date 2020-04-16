package com.tensquare.article.client;

import com.tensquare.article.pojo.Notice;
import com.tensquare.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("tensquare-notice")
public interface NoticeClient {

    // 新增通知
    // http://127.0.0.1:9014/notice POST
    @PostMapping(value = "/notice")
    public Result save(@RequestBody Notice notice);
}

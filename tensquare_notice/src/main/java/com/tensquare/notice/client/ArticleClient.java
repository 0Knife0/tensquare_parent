package com.tensquare.notice.client;

import com.tensquare.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("tensquare-article")
public interface ArticleClient {

    // GET /article/{articleId} 根据ID查询文章
    @GetMapping(value = "article/{articleId}")
    public Result findById(@PathVariable("articleId") String articleId);
}

package com.tensquare.article.config;

import com.tensquare.util.IdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdWorkerConfig {

    //注入雪花算法分布式id生成器
    @Bean
    public IdWorker createIdWorker() {
        return new IdWorker(1,1);
    }
}

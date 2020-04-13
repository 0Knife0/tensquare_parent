package com.tensquare.notice.config;

import com.tensquare.util.IdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdWorkerConfig {

    @Bean
    public IdWorker createIdWorker() {
        return new IdWorker(1, 1);
    }
}

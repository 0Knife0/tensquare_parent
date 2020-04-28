package com.tensquare.notice.config;

import com.tensquare.notice.netty.NettyServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyConfig {

    @Bean
    public NettyServer creatNettyServer() {
        NettyServer nettyServer = new NettyServer();

        //启动netty服务，使用新线程启动
        new Thread(() -> {
            nettyServer.start(1234);
        }).start();

        return nettyServer;
    }
}

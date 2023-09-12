package com.example.NettyClient.config;

import com.example.NettyClient.handler.ResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.context.annotation.Bean;

//@Configuration
public class NettyConfig {

    @Bean
    public ResponseHandler createClientHandler() {
        return new ResponseHandler();
    }

    @Bean
    public Bootstrap createBootstrap(EventLoopGroup group, ResponseHandler responseHandler) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ResponseHandler());
        return bootstrap;
    }

    @Bean
    public EventLoopGroup eventLoopGroup() {
        return new NioEventLoopGroup();
    }

}

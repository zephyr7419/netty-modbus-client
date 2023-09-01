package com.example.NettyClient.config;

import com.example.NettyClient.handler.ClientHandler;
import com.example.NettyClient.util.ClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyConfig {


    @Bean
    public ClientHandler createClientHandler() {
        return new ClientHandler();
    }

    @Bean
    public Bootstrap createBootstrap(EventLoopGroup group, ClientHandler clientHandler) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ClientInitializer(clientHandler));
        return bootstrap;
    }

    @Bean
    public EventLoopGroup eventLoopGroup() {
        return new NioEventLoopGroup();
    }

}

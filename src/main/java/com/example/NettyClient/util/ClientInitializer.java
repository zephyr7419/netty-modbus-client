package com.example.NettyClient.util;

import com.example.NettyClient.decoder.ClientDecoder;
import com.example.NettyClient.handler.ResponseHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.springframework.stereotype.Component;

@Component
public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    private final ResponseHandler responseHandler;

    public ClientInitializer(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        ClientDecoder decoder = new ClientDecoder();

        pipeline.addLast(decoder, responseHandler);
    }
}

package com.example.NettyClient.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("readableBytes: {}" , in.readableBytes());

        int readableBytes = in.readableBytes();
        if (readableBytes <= 0) {
            return; // No data to read
        }

        byte[] bytes = new byte[readableBytes];
        in.readBytes(bytes);

        out.add(bytes);
    }
}

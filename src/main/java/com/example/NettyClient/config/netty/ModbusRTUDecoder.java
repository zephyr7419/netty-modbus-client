package com.example.NettyClient.config.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ModbusRTUDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= 8) {
            int byteCount = in.getByte(2) & 0xFF;
            if (in.readableBytes() >= byteCount + 5) {
                ByteBuf response = in.readBytes(byteCount + 5);
                byte[] responseData = new byte[response.readableBytes()];
                response.readBytes(responseData);
                log.info("Received Modbus RTU Response: {}", byteArrayToHexString(responseData));

                // 여기에서 responseData를 파싱하여 필요한 데이터를 추출할 수 있습니다.
                // 추출한 데이터를 처리하는 코드를 추가하세요.

                ReferenceCountUtil.release(response);
            }
        }
    }

    private static String byteArrayToHexString(byte[] msg) {
        StringBuilder sb = new StringBuilder();
        for (byte b : msg) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}

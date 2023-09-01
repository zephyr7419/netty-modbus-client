package com.example.NettyClient.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@ChannelHandler.Sharable
public class ClientHandler extends ChannelInboundHandlerAdapter {
//    private static final int YOUR_FUNCTION_CODE_FOR_7_BYTES = 0x03;
//    private static final int YOUR_FUNCTION_CODE_FOR_8_BYTES = 0x10;
//    private static final int DEFAULT_PACKET_LENGTH = 7;
//    private static final int DUMMY_FUNCTION_CODE = 0x02;
//    @Value("${modbus.address}")
//    private int address;
//
//    @Value("${modbus.values.value}")
//    private int value;
//
//    private final ModbusReqHandler modbusReqHandler;
//    private static final byte[] DUMMY_BYTE_VALUE = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
//
//    public ClientHandler(ModbusReqHandler modbusReqHandler) {
//        this.modbusReqHandler = modbusReqHandler;
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        cause.printStackTrace();
//        ctx.close();
//    }
//
//    private boolean containsAll(byte[] array, byte[] subArray) {
//        for (byte value : subArray) {
//            boolean found = false;
//            for (byte element : array) {
//                if (element == value) {
//                    found = true;
//                    break;
//                }
//            }
//            if (!found) {
//                return false;
//            }
//        }
//        return true;
//    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }

//    private int getPacketLengthByFunctionCode(int functionCode) {
//        // 함수 코드에 따라 패킷의 길이를 반환합니다.
//        if (functionCode == YOUR_FUNCTION_CODE_FOR_7_BYTES) {
//            return 7;
//        } else if (functionCode == YOUR_FUNCTION_CODE_FOR_8_BYTES || functionCode == DUMMY_FUNCTION_CODE) {
//            return 8;
//        } else if (functionCode == 0) {
//            return 0;
//        }
//
//        // 기본 길이 또는 에러 처리
//        return DEFAULT_PACKET_LENGTH;
//    }


}

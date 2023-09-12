package com.example.NettyClient.config.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

@Slf4j
@Component
@ChannelHandler.Sharable
public class ModbusClientHandler extends SimpleChannelInboundHandler<byte[]> {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private void sendModbusRequest(ChannelHandlerContext ctx) {
        byte[] modbusRequest = buildModbusRequest(0, 10);
        ByteBuf buf = ctx.alloc().buffer();
        buf.writeBytes(modbusRequest);
        ctx.writeAndFlush(buf);
    }

    private static byte[] buildModbusRequest(int address, int quantity) {
        byte[] request = new byte[8];

        request[0] = 0x01;
        request[1] = 0x04;
        request[2] = 0x03;
        request[3] = 0x00;
        request[4] = 0x00;
        request[5] = (byte) 0xF1;
        request[6] = (byte) 0x8D;
        return request;
    }

    private static int calculateCRC(byte[] data, int offset, int length) {
        CRC32 crc32 = new CRC32();
        crc32.update(data, offset, length);
        int crcValue = (int) crc32.getValue();
        crcValue &= 0xFFFF;
        return crcValue;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        log.info("Received Modbus RTU Response: {}", byteArrayToHexString(msg));

        // 응답 데이터를 파싱하여 필요한 정보를 추출합니다.
        int slaveId = msg[1] & 0xFF; // Function Code
        int functionCode = msg[2] & 0xFF; // Byte Count

        if ((functionCode & 0x80) != 0) {
            int exceptionCode = msg[2] & 0xFF;
            log.error("Error Response - Exception Code: {}", exceptionCode);
        } else {
            int byteCount = msg[2] & 0xFF;
            log.info("byte count: {}", byteCount);

            if (byteCount > 0) {
                int[] values = parseRegisterValue(msg, 3, byteCount);
                log.info("Values: {}", Arrays.toString(values));
            } else {
                log.info("No data in response");
            }
        }

    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
        executorService.scheduleAtFixedRate(() -> sendModbusRequest(ctx), 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private static String byteArrayToHexString(byte[] msg) {
        StringBuilder sb = new StringBuilder();
        for (byte b : msg) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

    private static int[] parseRegisterValue(byte[] data, int startIndex, int byteCount) {
        // 데이터를 파싱하는 로직을 여기에 추가하세요.
        // 예를 들어, 데이터가 16비트 정수로 인코딩되어 있다고 가정하고 파싱합니다.
        int[] values = new int[byteCount / 2];
        for (int i = 0; i < values.length; i++) {

            int highByte = data[startIndex + i * 2] & 0xFF;
            int lowByte = data[startIndex + i * 2 + 1] & 0xFF;
            values[i] = (highByte << 8) | lowByte;
        }
        return values;
    }
}

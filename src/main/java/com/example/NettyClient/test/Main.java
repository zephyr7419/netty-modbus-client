package com.example.NettyClient.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Main {
    private static final String SERVER_HOST = "172.30.1.233";
    private static final int SERVER_PORT = 5300;


    public static byte[] calculateCRC16Modbus(byte[] data) {
        int crc = 0xFFFF;
        int polynomial = 0xA001;

        for (byte b : data) {
            crc ^= (int) b & 0xFF;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x0001) != 0) {
                    crc = (crc >> 1) ^ polynomial;
                } else {
                    crc >>= 1;
                }
            }
        }

        byte[] crcBytes = new byte[2];
        crcBytes[0] = (byte) (crc & 0xFF);
        crcBytes[1] = (byte) ((crc >> 8) & 0xFF);

        return crcBytes;
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

    private static String byteArrayToHexString(byte[] msg) {
        StringBuilder sb = new StringBuilder();
        for (byte b : msg) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

    public static void main(String[] args) {

        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(@NotNull SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast("modbusEncoder", new ModbusRTUEncoder());
                            pipeline.addLast("modbusDecoder", new ModbusRTUDecoder());

                            pipeline.addLast("responseHandler", new HexProtocolClientHandler());
                        }
                    });

            Channel channel = bootstrap.connect(SERVER_HOST, SERVER_PORT).sync().channel();

            if (channel.isActive()) {
                // 연결 성공 로그
                log.info("Connected to : {} : {}", SERVER_HOST, SERVER_PORT);
                byte[] modbusRequest = buildModbusRequest(0x0300, 0x004);
                ByteBuf buf = channel.alloc().buffer();
                buf.writeBytes(modbusRequest);

                if (channel.writeAndFlush(buf).isSuccess()) {
                    log.info("send to Modbus RTU Request: {}", byteArrayToHexString(modbusRequest));
                }
                channel.closeFuture().sync();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    private static byte[] buildModbusRequest(int address, int quantity) {
        byte[] a1 = new byte[6];
        a1[0] = 1;
        a1[1] = 4;
        a1[2] = (byte) ((address >> 8) & 0xFF);
        a1[3] = (byte) (address & 0xFF);
        a1[4] = (byte) ((quantity >> 8) & 0xFF);
        a1[5] = (byte) (quantity & 0xFF);

        byte[] crc16Modbus = calculateCRC16Modbus(a1);
        byte[] a2 = new byte[8];
        a2[0] = 1;
        a2[1] = 4;
        a2[2] = (byte) ((address >> 8) & 0xFF);
        a2[3] = (byte) (address & 0xFF);
        a2[4] = (byte) ((quantity >> 8) & 0xFF);
        a2[5] = (byte) (quantity & 0xFF);
        a2[6] = crc16Modbus[0];
        a2[7] = crc16Modbus[1];


        byte[] testByte = {0x01, 0x04, 0x30, 0x00, 0x00, 0x04};
        log.info("value: {}", byteArrayToHexString(crc16Modbus));

        log.info("request: {}", byteArrayToHexString(a2));
        return a2;
    }



    private static class ModbusRTUEncoder extends MessageToByteEncoder<byte[]> {

        @Override
        protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out) throws Exception {
            out.writeBytes(msg);
        }
    }

    private static class ModbusRTUDecoder extends ByteToMessageDecoder {

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
                    parseAndLogResponse(responseData);

                    ReferenceCountUtil.release(response);
                }
            }
        }
    }

    private static class HexProtocolClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            byte[] response = new byte[buf.readableBytes()];
            buf.readBytes(response);

            // 응답 데이터를 파싱하여 로그에 출력합니다.
            parseAndLogResponse(response);
        }
    }

    private static void parseAndLogResponse(byte[] response) {
        byte slaveId = response[0];
        byte funcCode = response[1];
        byte byteCount = response[2];
        int dataStartIndex = 3; // 응답 데이터의 시작 인덱스

        // 응답 데이터를 파싱하여 로그에 출력
        while (dataStartIndex + 1  < response.length) {
            byte hi = response[dataStartIndex];
            byte lo = response[dataStartIndex + 1];
            int value = ((hi & 0xFF) << 8) | (lo & 0xFF);

            // 데이터에 따라 파싱하여 로그 출력
            switch (dataStartIndex) {
                case 3: // 첫 번째 값
                    log.info("인버터 용량: {},0x{}" , formatInverterCapacity(value), Integer.toHexString(value));
                    break;
                case 5: // 두 번째 값
                    log.info("인버터 입력 전압/전원형태: {}, 0x{}" ,formatInputVoltage(value), Integer.toHexString(value));
                    break;
                case 7: // 세 번째 값
                    log.info("인버터 S/W 버전: {}, 0x{}" ,formatSoftwareVersion(value), Integer.toHexString(value));
                    break;
                case 9: // 네 번째 값
                    log.info("인버터 용량: {}, 0x{}" ,formatHP(value), Integer.toHexString(value));
                    break;
                default:
                    // 추가 파싱이 필요한 경우 여기에 추가합니다.
                    break;
            }

            // 데이터 인덱스를 2바이트씩 증가시킴
            dataStartIndex += 2;
        }

        // 나머지 파라미터와 CRC 출력
        byte crcLo = response[response.length - 2];
        byte crcHi = response[response.length - 1];
        log.info("Slave ID: " + slaveId);
        log.info("Function Code: " + funcCode);
        log.info("Byte Count: " + byteCount);
        log.info("CRC (Lo): " + crcLo);
        log.info("CRC (Hi): " + crcHi);
    }


    private static String formatInverterCapacity(int value) {
        // 인버터 용량을 계산하여 반환
        String capacity;
        switch (value) {
            case 0x4008:
                capacity = "0.75kW";
                break;
            case 0x4015:
                capacity = "1.5kW";
                break;
            case 0x40f0:
                capacity = "15kW";
                break;
            // 다른 용량에 대한 처리 추가
            default:
                capacity = "Unknown Capacity";
        }
        return capacity;
    }

    private static String formatInputVoltage(int value) {
        // 입력 전압/전원 형태를 계산하여 반환
        String voltageType;
        String hexValue = Integer.toHexString(value);
        switch (value) {
            case 0x0231:
                voltageType = "200V 3상 강냉식";
                break;
            case 0x0431:
                voltageType = "400V 3상 강냉식";
                break;
            // 다른 전압/전원 형태에 대한 처리 추가
            default:
                voltageType = "Unknown Voltage/Power Type";
        }
        return voltageType;
    }

    private static String formatSoftwareVersion(int value) {
        // S/W 버전을 계산하여 반환
        String version;
        String hexValue = Integer.toHexString(value);
        switch (value) {
            case 0x0064:
                version = "version 1.00";
                break;
            case 0x0065:
                version = "version 1.01";
                break;
            // 다른 버전에 대한 처리 추가
            default:
                version = "Unknown Version";
        }
        return version;
    }

    private static String formatHP(int value) {
        // S/W 버전을 계산하여 반환
        String version;
        String hexValue = Integer.toHexString(value);
        switch (value) {
            case 0x4010:
                version = "1HP";
                break;
            case 0x4020:
                version = "2HP";
                break;
            case 0x4140:
                version = "14HP";
            // 다른 버전에 대한 처리 추가
            default:
                version = "Unknown Version";
        }
        return version;
    }



}


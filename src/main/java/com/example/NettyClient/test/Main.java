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
    private static final int[] CRC = new int[256];

    public Main() {
        CRC[0] = 0X0000;
        CRC[1] = 0XC0C1;
        CRC[2] = 0XC181;
        CRC[3] = 0X0140;
        CRC[4] = 0XC301;
        CRC[5] = 0X03C0;
        CRC[6] = 0X0280;
        CRC[7] = 0XC241;
        CRC[8] = 0XC601;
        CRC[9] = 0X06C0;
        CRC[10] = 0X0780;
        CRC[11] = 0XC741;
        CRC[12] = 0X0500;
        CRC[13] = 0XC5C1;
        CRC[14] = 0XC481;
        CRC[15] = 0X0440;
        CRC[16] = 0XCC01;
        CRC[17] = 0X0CC0;
        CRC[18] = 0X0D80;
        CRC[19] = 0XCD41;
        CRC[20] = 0X0F00;
        CRC[21] = 0XCFC1;
        CRC[22] = 0XCE81;
        CRC[23] = 0X0E40;
        CRC[24] = 0X0A00;
        CRC[25] = 0XCAC1;
        CRC[26] = 0XCB81;
        CRC[27] = 0X0B40;
        CRC[28] = 0XC901;
        CRC[29] = 0X09C0;
        CRC[30] = 0X0880;
        CRC[31] = 0XC841;
        CRC[32] = 0XD801;
        CRC[33] = 0X18C0;
        CRC[34] = 0X1980;
        CRC[35] = 0XD941;
        CRC[36] = 0X1B00;
        CRC[37] = 0XDBC1;
        CRC[38] = 0XDA81;
        CRC[39] = 0X1A40;
        CRC[40] = 0X1E00;
        CRC[41] = 0XDEC1;
        CRC[42] = 0XDF81;
        CRC[43] = 0X1F40;
        CRC[44] = 0XDD01;
        CRC[45] = 0X1DC0;
        CRC[46] = 0X1C80;
        CRC[47] = 0XDC41;
        CRC[48] = 0X1400;
        CRC[49] = 0XD4C1;
        CRC[50] = 0XD581;
        CRC[51] = 0X1540;
        CRC[52] = 0XD701;
        CRC[53] = 0X17C0;
        CRC[54] = 0X1680;
        CRC[55] = 0XD641;
        CRC[56] = 0XD201;
        CRC[57] = 0X12C0;
        CRC[58] = 0X1380;
        CRC[59] = 0XD341;
        CRC[60] = 0X1100;
        CRC[61] = 0XD1C1;
        CRC[62] = 0XD081;
        CRC[63] = 0X1040;
        CRC[64] = 0XF001;
        CRC[65] = 0X30C0;
        CRC[66] = 0X3180;
        CRC[67] = 0XF141;
        CRC[68] = 0X3300;
        CRC[69] = 0XF3C1;
        CRC[70] = 0XF281;
        CRC[71] = 0X3240;
        CRC[72] = 0X3600;
        CRC[73] = 0XF6C1;
        CRC[74] = 0XF781;
        CRC[75] = 0X3740;
        CRC[76] = 0XF501;
        CRC[77] = 0X35C0;
        CRC[78] = 0X3480;
        CRC[79] = 0XF441;
        CRC[80] = 0X3C00;
        CRC[81] = 0XFCC1;
        CRC[82] = 0XFD81;
        CRC[83] = 0X3D40;
        CRC[84] = 0XFF01;
        CRC[85] = 0X3FC0;
        CRC[86] = 0X3E80;
        CRC[87] = 0XFE41;
        CRC[88] = 0XFA01;
        CRC[89] = 0X3AC0;
        CRC[90] = 0X3B80;
        CRC[91] = 0XFB41;
        CRC[92] = 0X3900;
        CRC[93] = 0XF9C1;
        CRC[94] = 0XF881;
        CRC[95] = 0X3840;
        CRC[96] = 0X2800;
        CRC[97] = 0XE8C1;
        CRC[98] = 0XE981;
        CRC[99] = 0X2940;
        CRC[100] = 0XEB01;
        CRC[101] = 0X2BC0;
        CRC[102] = 0X2A80;
        CRC[103] = 0XEA41;
        CRC[104] = 0XEE01;
        CRC[105] = 0X2EC0;
        CRC[106] = 0X2F80;
        CRC[107] = 0XEF41;
        CRC[108] = 0X2D00;
        CRC[109] = 0XEDC1;
        CRC[110] = 0XEC81;
        CRC[111] = 0X2C40;
        CRC[112] = 0XE401;
        CRC[113] = 0X24C0;
        CRC[114] = 0X2580;
        CRC[115] = 0XE541;
        CRC[116] = 0X2700;
        CRC[117] = 0XE7C1;
        CRC[118] = 0XE681;
        CRC[119] = 0X2640;
        CRC[120] = 0X2200;
        CRC[121] = 0XE2C1;
        CRC[122] = 0XE381;
        CRC[123] = 0X2340;
        CRC[124] = 0XE101;
        CRC[125] = 0X21C0;
        CRC[126] = 0X2080;
        CRC[127] = 0XE041;
        CRC[128] = 0XA001;
        CRC[129] = 0X60C0;
        CRC[130] = 0X6180;
        CRC[131] = 0XA141;
        CRC[132] = 0X6300;
        CRC[133] = 0XA3C1;
        CRC[134] = 0XA281;
        CRC[135] = 0X6240;
        CRC[136] = 0X6600;
        CRC[137] = 0XA6C1;
        CRC[138] = 0XA781;
        CRC[139] = 0X6740;
        CRC[140] = 0XA501;
        CRC[141] = 0X65C0;
        CRC[142] = 0X6480;
        CRC[143] = 0XA441;
        CRC[144] = 0X6C00;
        CRC[145] = 0XACC1;
        CRC[146] = 0XAD81;
        CRC[147] = 0X6D40;
        CRC[148] = 0XAF01;
        CRC[149] = 0X6FC0;
        CRC[150] = 0X6E80;
        CRC[151] = 0XAE41;
        CRC[152] = 0XAA01;
        CRC[153] = 0X6AC0;
        CRC[154] = 0X6B80;
        CRC[155] = 0XAB41;
        CRC[156] = 0X6900;
        CRC[157] = 0XA9C1;
        CRC[158] = 0XA881;
        CRC[159] = 0X6840;
        CRC[160] = 0X7800;
        CRC[161] = 0XB8C1;
        CRC[162] = 0XB981;
        CRC[163] = 0X7940;
        CRC[164] = 0XBB01;
        CRC[165] = 0X7BC0;
        CRC[166] = 0X7A80;
        CRC[167] = 0XBA41;
        CRC[168] = 0XBE01;
        CRC[169] = 0X7EC0;
        CRC[170] = 0X7F80;
        CRC[172] = 0XBF41;
        CRC[173] = 0X7D00;
        CRC[174] = 0XBDC1;
        CRC[175] = 0XBC81;
        CRC[176] = 0X7C40;
        CRC[176] = 0XB401;
        CRC[177] = 0X74C0;
        CRC[178] = 0X7580;
        CRC[179] = 0XB541;
        CRC[180] = 0X7700;
        CRC[181] = 0XB7C1;
        CRC[182] = 0XB681;
        CRC[183] = 0X7640;
        CRC[184] = 0X7200;
        CRC[185] = 0XB2C1;
        CRC[186] = 0XB381;
        CRC[187] = 0X7340;
        CRC[188] = 0XB101;
        CRC[189] = 0X71C0;
        CRC[190] = 0X7080;
        CRC[191] = 0XB041;
        CRC[192] = 0X5000;
        CRC[193] = 0X90C1;
        CRC[194] = 0X9181;
        CRC[195] = 0X5140;
        CRC[196] = 0X9301;
        CRC[197] = 0X53C0;
        CRC[198] = 0X5280;
        CRC[199] = 0X9241;
        CRC[200] = 0X9601;
        CRC[201] = 0X56C0;
        CRC[202] = 0X5780;
        CRC[203] = 0X9741;
        CRC[204] = 0X5500;
        CRC[205] = 0X95C1;
        CRC[206] = 0X9481;
        CRC[207] = 0X5440;
        CRC[208] = 0X9C01;
        CRC[209] = 0X5CC0;
        CRC[210] = 0X5D80;
        CRC[211] = 0X9D41;
        CRC[212] = 0X5F00;
        CRC[213] = 0X9FC1;
        CRC[214] = 0X9E81;
        CRC[215] = 0X5E40;
        CRC[216] = 0X5A00;
        CRC[217] = 0X9AC1;
        CRC[218] = 0X9B81;
        CRC[219] = 0X5B40;
        CRC[220] = 0X9901;
        CRC[221] = 0X59C0;
        CRC[222] = 0X5880;
        CRC[223] = 0X9841;
        CRC[224] = 0X8801;
        CRC[225] = 0X48C0;
        CRC[226] = 0X4980;
        CRC[227] = 0X8941;
        CRC[228] = 0X4B00;
        CRC[229] = 0X8BC1;
        CRC[230] = 0X8A81;
        CRC[231] = 0X4A40;
        CRC[232] = 0X4E00;
        CRC[233] = 0X8EC1;
        CRC[234] = 0X8F81;
        CRC[235] = 0X4F40;
        CRC[236] = 0X8D01;
        CRC[237] = 0X4DC0;
        CRC[238] = 0X4C80;
        CRC[239] = 0X8C41;
        CRC[240] = 0X4400;
        CRC[241] = 0X84C1;
        CRC[242] = 0X8581;
        CRC[243] = 0X4540;
        CRC[244] = 0X8701;
        CRC[245] = 0X47C0;
        CRC[246] = 0X4680;
        CRC[247] = 0X8641;
        CRC[248] = 0X8201;
        CRC[249] = 0X42C0;
        CRC[250] = 0X4380;
        CRC[251] = 0X8341;
        CRC[252] = 0X4100;
        CRC[253] = 0X81C1;
        CRC[254] = 0X8081;
        CRC[255] = 0X4040;

    }

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

    /**
     * ********************************************************************************
     * 헥사값 배열의 CRC16 을 구하는 함수
     * <p>
     * 파라메터에 해당하는 값의 CRC16 체크섬값을 만들어 리턴
     *
     * @param    bytes: 프로토콜 6byte (일반적인 파라메터)
     * @return byte[2]형 16bit CRC값 리턴
     *********************************************************************************/
    public byte[] fn_makeCRC16(byte[] bytes) {
        int crc = 0xFFFF;
        int polynomial = 0xA001;

        for (byte b : bytes) {
            crc ^= (int) b & 0xFF;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x0001) != 0) {
                    crc = (crc >> 1) ^ polynomial;
                } else {
                    crc >>= 1;
                }
            }
        }

        return fnShortToBytes((short) crc, 0);
    }

    /**
     * ********************************************************************************
     * 헥사값 배열의 CRC16 을 구하는 함수
     * <p>
     * 파라메터에 해당하는 값의 CRC16 체크섬값을 만들어 리턴
     *
     * @param    bytes: 프로토콜 6byte (일반적인 파라메터)
     * @return short형 16bit CRC값 리턴
     *********************************************************************************/
    public short fn_makeCRC16toShort(byte[] bytes) {
        int icrc = 0xFFFF;
        for (byte b : bytes) {
            icrc = (icrc >>> 8) ^ CRC[(icrc ^ b) & 0xff];
        }

        return (short) icrc;
    }

    /**
     * ********************************************************************************
     * 인자를 이용한 MODBUS 프로토콜 생성함수 (CRC가 있는 시리얼용)
     *
     * @return 8byte의 프로토콜 리턴 (CRC16포함)
     * @param    id:		장비번호 (모두 십진수)
     * fnr:	펑션코드
     * rst:	레지스터 시작주소
     * rcnt:	레지스터 갯수
     *********************************************************************************/
    public byte[] fn_makePTcom(int id, int fnr, int rst, int rcnt) {
        byte[] btmp;
        byte[] bytes = new byte[6];
        byte[] rbyte = new byte[8];

        bytes[0] = (byte) id;	//장치번호
        bytes[1] = (byte) fnr;	//펑션코드

        if(fnr == 3)	rst = rst - 40001;
        if (fnr == 4)   rst = rst - 1;

        //레지스터 시작주소
        btmp = fnShortToBytes((short)rst,1);
        bytes[2] = btmp[0];
        bytes[3] = btmp[1];

        //가져올 레지스터 갯수
        btmp = fnShortToBytes((short)rcnt,1);
        bytes[4] = btmp[0];
        bytes[5] = btmp[1];

        btmp = fn_makeCRC16(bytes);// CRC구함
        for(int i=0;i<6;i++) rbyte[i] = bytes[i];
        rbyte[6] = btmp[0];
        rbyte[7] = btmp[1];
        System.out.println("CRC16 = " + Integer.toHexString(btmp[0])); //test
        return rbyte;
    }

    /**
     * ********************************************************************************
     * 인자를 이용한 MODBUS 프로토콜 생성함수 (CRC가 없고 Header가 있는 TCP/IP용)
     *
     * @return 12byte의 프로토콜 리턴 (헤더포함)
     * @param    id:		장비번호 (모두 십진수)
     * fnr:	펑션코드
     * rst:	레지스터 시작주소
     * rcnt:	레지스터 갯수
     *********************************************************************************/
    public byte[] fn_makePTsock(int id, int fnr, int rst, int rcnt) {
        byte[] btmp;
        byte[] rbyte = new byte[12];
        //HEADER
        //카운터
        rbyte[0] = 0x00;
        rbyte[1] = 0x00;
        //프로토콜아이디 0x0000 고정
        rbyte[2] = 0x00;
        rbyte[3] = 0x00;
        //길이
        rbyte[4] = 0x00;
        rbyte[5] = 0x06;
        //
        rbyte[6] = (byte) id;    //유닛번호
        rbyte[7] = (byte) fnr;    //펑션코드

        if (fnr == 3) rst = rst - 40001;
        if (fnr == 4) rst = rst - 1;
        //레지스터 시작주소
        btmp = fnShortToBytes((short) rst, 1);
        rbyte[8] = btmp[0];
        rbyte[9] = btmp[1];

        //가져올 레지스터 갯수
        btmp = fnShortToBytes((short) rcnt, 1);
        rbyte[10] = btmp[0];
        rbyte[11] = btmp[1];
        return rbyte;
    }

    /**
     * short 를 byte[2] 형으로 변환
     */
    public byte[] fnShortToBytes(short Value, int Order) {
        byte[] temp;
        temp = new byte[]{(byte) ((Value & 0xFF00) >> 8), (byte) (Value & 0x00FF)};
        temp = ChangeByteOrder(temp, Order);
        return temp;
    }

    /**
     * 상위, 하위 변환 (내부적으로 사용하는 함수)
     */
    private byte[] ChangeByteOrder(byte[] value, int Order) {
        int idx = value.length;
        byte[] Temp = new byte[idx];
//BIG_EDIAN
        if (Order == 1) {
            Temp = value;
        }
//Little_EDIAN
        else if (Order == 0) {
            for (int i = 0; i < idx; i++) {
                Temp[i] = value[idx - (i + 1)];
            }
        }
        return Temp;
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


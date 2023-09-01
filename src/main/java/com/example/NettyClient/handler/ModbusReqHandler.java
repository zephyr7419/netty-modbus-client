package com.example.NettyClient.handler;

import com.example.NettyClient.protocol.ModbusProtocol;
import com.example.NettyClient.protocol.ReadRequestParameters;
import com.example.NettyClient.protocol.ReadTCPParameters;
import com.example.NettyClient.protocol.WriteRequestParameters;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
@RequiredArgsConstructor
public class ModbusReqHandler {
    private final ModbusProtocol modbusProtocol;

    public byte[] createReadRequest(int startAddress, int numberOfRegisters) {
        ReadRequestParameters readRequestParameters = new ReadRequestParameters((byte) 1, startAddress, numberOfRegisters);

        return modbusProtocol.getReadRequest.apply(readRequestParameters);
    }

    public byte[] createTCPReadRequest(byte transactionId, int startAddress, int numberOfRegisters) {
        ReadTCPParameters readTCPParameters = new ReadTCPParameters(transactionId, startAddress, numberOfRegisters);
        return modbusProtocol.getTCPReadRequest.apply(readTCPParameters);
    }

    public static int calculateCRC(byte[] data, int length) {
        int crc = 0xFFFF;

        for (int pos = 0; pos < length; pos++) {
            crc ^= (int) data[pos] & 0xFF; // XOR byte into least significant byte of crc

            for (int i = 8; i != 0; i--) { // Loop over each bit
                if ((crc & 0x0001) != 0) { // If the LSB is set
                    crc >>= 1; // Shift right and XOR 0xA001
                    crc ^= 0xA001;
                } else { // Else LSB is not set
                    crc >>= 1; // Just shift right
                }
            }
        }

        // Note, this number has low and high bytes swapped, so use it accordingly (or swap bytes)
        return crc;
    }

    public void sendPressMultipleRegisterRequest(Channel channel, int address, int value) {
        WriteRequestParameters writeRequestParameters = new WriteRequestParameters((byte) 1, address, (byte) value);
        byte[] request = modbusProtocol.getWriteRequest.apply(writeRequestParameters);
        ByteBuf buf = Unpooled.copiedBuffer(request);
        channel.writeAndFlush(buf);
        log.info("전송완료!");
    }


}

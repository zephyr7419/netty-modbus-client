package com.example.NettyClient.protocol;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.function.Function;

import static com.example.NettyClient.handler.ModbusReqHandler.calculateCRC;

@Slf4j
@Component
public class ModbusProtocol {

    public Function<ReadRequestParameters, byte[]> getReadRequest = readRequestParameters -> {
        ByteBuffer buffer = ByteBuffer.allocate(12);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(readRequestParameters.getSlaveId());
        buffer.put((byte)0x03);
        buffer.put((byte) ((readRequestParameters.getStartAddress() - 1) >> 8));
        buffer.put((byte) ((readRequestParameters.getStartAddress() - 1) & 0xFF));
        buffer.put((byte) (readRequestParameters.getNumberOfRegisters() >> 8));
        buffer.put((byte) (readRequestParameters.getNumberOfRegisters() & 0xFF));
        short crc = (short) calculateCRC(buffer.array(), buffer.position());
        buffer.put((byte) (crc & 0xFF));
        buffer.put((byte) (crc >> 8));
        log.info("buffer: {}", Arrays.toString(buffer.array()));
        return buffer.array();
    };

    public Function<ReadTCPParameters, byte[]> getTCPReadRequest = readRequestParameters -> {
        ByteBuffer buffer = ByteBuffer.allocate(12);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putShort(readRequestParameters.getTransactionId());
        buffer.putShort((short) 0x0000);
        buffer.putShort((short) 6);
        buffer.put((byte) 0x01);
        buffer.put((byte) 0x03);
        buffer.putShort((short) (readRequestParameters.getStartAddress() - 1));
        buffer.putShort((short) readRequestParameters.getNumberOfRegisters());
        log.info("buffer: {}", buffer.capacity());
        return buffer.array();
    };


    public Function<WriteRequestParameters, byte[]> getWriteRequest = writeRequestParameters -> {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(writeRequestParameters.getSlaveId());
        buffer.put((byte) 0x06);
        buffer.put((byte) ((writeRequestParameters.getStartAddress() - 1) >> 8));
        buffer.put((byte) ((writeRequestParameters.getStartAddress() - 1) & 0xFF));
        buffer.put((byte) (writeRequestParameters.getValue() >> 8));
        buffer.put((byte) (writeRequestParameters.getValue() & 0xFF));
        int crc = calculateCRC(buffer.array(), buffer.capacity());
        buffer.put((byte) (crc & 0xFF));
        buffer.put((byte) (crc >> 8));
        log.info("buffer: {}", Arrays.toString(buffer.array()));
        return buffer.array();
    };

    public Function<WriteTCPParameters, byte[]> getTCPWriteRequest = writeRequestParameters -> {
        ByteBuffer buffer = ByteBuffer.allocate(12);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putShort(writeRequestParameters.getTransactionId()); // Transaction Id
        buffer.putShort((short) 0x0000); // Protocol Id (always 0x0000 for Modbus-TCP)
        buffer.putShort((short) 6); // Length (always 6 for write single register)
        buffer.put((byte) 0x01); // Unit Id
        buffer.put((byte) 0x06); // Function Code (0x06 for write single register)
        buffer.putShort((short) (writeRequestParameters.getStartAddress() - 1)); // Start Address
        buffer.putShort(writeRequestParameters.getValue()); // Value

        return buffer.array();
    };

}

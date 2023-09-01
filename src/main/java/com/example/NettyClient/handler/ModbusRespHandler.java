package com.example.NettyClient.handler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModbusRespHandler {

    public static void handleResponse(byte[] response) {
        int funcCode = response[1];
        int byteCount = response[2];

        if (funcCode >= 0x80) {
            int exceptionCode = response[2];
            return;
        }

        for (int i = 0; i < byteCount / 2; i++) {
            int value = ((response[3 + i * 2] & 0xFF) << 8) | (response[4 + i * 2] & 0xFF);
            log.info("value: {}", value);
        }
    }
}

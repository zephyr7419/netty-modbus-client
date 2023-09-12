package com.example.NettyClient.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
public class ModbusRespHandler {

    public static void handleResponse(byte[] response) {
        StringBuilder hexString = new StringBuilder();

        for (byte b : response) {
            // Convert each byte to a 2-digit hexadecimal representation
            String hex = String.format("%02X", b & 0xFF);
            hexString.append(hex);
        }

        log.info("Received data in hexadecimal format: {}", hexString.toString());
    }
}

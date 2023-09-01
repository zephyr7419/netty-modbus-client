package com.example.NettyClient.util.modbus;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

@Slf4j
public class ValueTransformer {

    public Object transformValue(int address, int rawValue) {
        Function<Integer, Object> transformStrategy = switch (address) {
            case 0x0000 -> raw -> "F: H" + raw;
            case 0x0001, 0x0006, 0x0007 -> raw -> raw * 0.1;
            case 0x0002 -> raw -> raw * 0.01;
            case 0x0003, 0x0004, 0x0005 -> raw -> raw;
            default -> null;
        };
        assert transformStrategy != null;
        return transformStrategy.apply(rawValue);
    }

}

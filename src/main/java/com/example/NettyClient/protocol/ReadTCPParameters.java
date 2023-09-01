package com.example.NettyClient.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReadTCPParameters {
    private byte transactionId;
    private int startAddress;
    private int numberOfRegisters;
}

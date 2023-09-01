package com.example.NettyClient.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WriteTCPParameters {
    private byte transactionId;
    private int startAddress;
    private byte value;
}

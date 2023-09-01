package com.example.NettyClient.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WriteRequestParameters {
    private byte slaveId;
    private int startAddress;
    private byte value;
}

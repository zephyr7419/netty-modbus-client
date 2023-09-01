package com.example.NettyClient.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadRequestParameters{
    private byte slaveId;
    private int startAddress;
    private int numberOfRegisters;
}

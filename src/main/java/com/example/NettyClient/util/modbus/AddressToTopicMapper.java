package com.example.NettyClient.util.modbus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddressToTopicMapper {

    public String mapAddressToTopic(int address) {
        return switch (address) {
            case 0x0000 -> "inverter_model";
            case 0x0001 -> "output_current";
            case 0x0002 -> "output_frequency";
            case 0x0003 -> "output_rpm";
            case 0x0004 -> "output_voltage";
            case 0x0005 -> "dc_link_voltage";
            case 0x0006 -> "acceleration_time";
            case 0x0007 -> "deceleration_time";
            default -> "unknown";
        };
    }

}

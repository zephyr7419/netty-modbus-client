package com.example.NettyClient.service;

import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ModbusTCPMasterFactory {

    private final Map<String, ModbusTCPMaster> masterMap = new HashMap<>(); //

    public ModbusTCPMaster create(String deviceIp, int port) {
        return new ModbusTCPMaster(deviceIp, port);
    }

    public void putMaster(String deviceIp, ModbusTCPMaster master) {
        masterMap.put(deviceIp, master);
    }
}

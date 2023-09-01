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

    public ModbusTCPMaster getMaster(String deviceIp) throws Exception {
        ModbusTCPMaster master = masterMap.get(deviceIp);
        if (master == null) {
            throw new Exception("No ModbusTCPMaster found for IP: " + deviceIp);
        }
        return master;
    }

    public void putMaster(String deviceIp, ModbusTCPMaster master) {
        masterMap.put(deviceIp, master);
    }
}

package com.example.NettyClient.service;

import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ModbusMasterManager {

    private final ModbusTCPMasterFactory factory;
    private final Map<String, ModbusTCPMaster> masterMap = new HashMap<>();

    public ModbusMasterManager(ModbusTCPMasterFactory factory) {
        this.factory = factory;
    }

    public ModbusTCPMaster getMaster(String deviceIp) {
        if (!masterMap.containsKey(deviceIp)) {
            ModbusTCPMaster master = new ModbusTCPMaster(deviceIp, 5300);
            try {
                master.connect();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            masterMap.put(deviceIp, master);
        }
        return masterMap.get(deviceIp);
    }
}

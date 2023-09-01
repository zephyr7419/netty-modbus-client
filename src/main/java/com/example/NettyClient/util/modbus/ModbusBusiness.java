package com.example.NettyClient.util.modbus;

import com.example.NettyClient.service.ModbusMasterManager;
import com.example.NettyClient.util.InfluxManager;
import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class ModbusBusiness {

    private final ModbusMasterManager modbusMasterManager;
    private final ValueTransformer valueTransformer;
    private final AddressToTopicMapper addressToTopicMapper;
    private final InfluxManager influxManager;

    public void readAndProcessRegisters(ModbusTCPMaster master, int startAddress, int count) throws Exception {
        Register[] registers = master.readMultipleRegisters(startAddress, count);
        for (Register register : registers) {
            int rawValue = register.getValue();
            Object transformedValue = valueTransformer.transformValue(startAddress, rawValue);
            String topic = addressToTopicMapper.mapAddressToTopic(startAddress);
            influxManager.saveDataToInfluxDB(topic, transformedValue);
        }
    }

    public void connect(String deviceIp) throws Exception {
        ModbusTCPMaster tcpMaster = modbusMasterManager.getMaster(deviceIp);
        tcpMaster.connect();

        for (int startAddress = 0x0000; startAddress <= 0x0007; startAddress++) {
            readAndProcessRegisters(tcpMaster, startAddress, 1); // 나머지 인자들
        }
    }

    public byte[] readData(String deviceIp) throws Exception {
        ModbusTCPMaster master = modbusMasterManager.getMaster(deviceIp);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        // 여기서도 readAndProcessRegisters를 재사용할 수 있지만, 다른 처리를 하기 때문에 루프를 별도로 작성했습니다.
        Register[] registers = master.readMultipleRegisters(0x0000, 8);
        for (Register register : registers) {
            int rawValue = register.getValue();
            Object transformedValue = valueTransformer.transformValue(0x0000, rawValue);
            String topic = addressToTopicMapper.mapAddressToTopic(0x0000);
            byte[] data = (topic + ":" + transformedValue).getBytes(StandardCharsets.UTF_8);
            byteBuffer.put(data);
        }
        return byteBuffer.array();
    }

    public void updateRegister(String deviceIp, String payload) throws Exception {
        ModbusTCPMaster master = modbusMasterManager.getMaster(deviceIp);
        double receivedValue = Double.parseDouble(payload);
        Register register = new SimpleRegister((int) (receivedValue * 10));
        master.writeSingleRegister(0x0006, register);
    }


}

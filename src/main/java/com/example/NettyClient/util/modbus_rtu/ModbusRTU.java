package com.example.NettyClient.util.modbus_rtu;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.util.SerialParameters;
//
//@Component
public class ModbusRTU {

    private final SerialParameters parameters = new SerialParameters();


    public void setRtuConnect() {
        // 직렬포트 이름
        String portName = "/dev/ttyUSB0";
        parameters.setPortName(portName);
        parameters.setBaudRate(9600);
        parameters.setDatabits(8);
        parameters.setParity("None");
        parameters.setStopbits(1);
        parameters.setEncoding(Modbus.SERIAL_ENCODING_RTU);

        ModbusSerialMaster master = new ModbusSerialMaster(parameters);

        try {
            master.connect();

            // Modbus Slave ID
            int unitId = 1;
            // 레지스터 주소
            int registerAddress = 0;
            // 읽을 레지스터 개수
            int registerCount = 2;
            Register[] registers = master.readMultipleRegisters(unitId, registerAddress, registerCount);

            for (int i = 0; i < registers.length; i++) {
                System.out.println("Register " + (registerAddress + i)  + " value: " + registers[i].getValue());
            }

            master.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

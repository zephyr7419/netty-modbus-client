package com.example.NettyClient.config;

import com.example.NettyClient.util.modbus.ModbusBusiness;
import com.example.NettyClient.service.ModbusMasterManager;
import com.example.NettyClient.util.InfluxManager;
import com.example.NettyClient.util.modbus.AddressToTopicMapper;
import com.example.NettyClient.util.modbus.ValueTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ModbusConfig {

    @Bean
    public TaskScheduler taskScheduler(){
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    public ModbusBusiness modbusBusinessService (
            ModbusMasterManager modbusMasterManager,
            ValueTransformer valueTransformer,
            AddressToTopicMapper addressToTopicMapper,
            InfluxManager influxManager

    ){
        return new ModbusBusiness(modbusMasterManager, valueTransformer, addressToTopicMapper, influxManager);
    }

}

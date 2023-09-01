package com.example.NettyClient.util.modbus;

import com.example.NettyClient.service.ModbusMasterManager;
import com.example.NettyClient.service.TaskSchedulingService;
import io.netty.bootstrap.Bootstrap;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class ModbusCommunication {
    private final String[] deviceIps = {"127.0.0.1", /* 해당 부분은 13대의 기기 ip가 올 예정*/};
    private final int port = 502; // 기본 포트가 502입니다.
    private final TaskSchedulingService taskSchedulingService;
    private final ModbusBusiness modbusBusiness;
    private final TaskScheduler taskScheduler;
    private final ModbusMasterManager modbusMasterManager;
    private final Bootstrap bootstrap;

    @PostConstruct
    public void initializeResources() {
        for (String deviceIp : deviceIps) {
            modbusMasterManager.getMaster(deviceIp);

            bootstrap.connect(deviceIp, port).addListener(future -> {
                if (future.isSuccess()) {
                    taskSchedulingService.scheduleTask(taskScheduler, () -> {
                        try {
                            modbusBusiness.connect(deviceIp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 60, TimeUnit.SECONDS);
                } else {
                    log.error("Connection failed: {}", deviceIp);
                }
            });
        }
    }
}

package com.example.NettyClient.service;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TaskSchedulingService {

    public void scheduleTask(TaskScheduler taskScheduler, Runnable task, long period, TimeUnit timeUnit) {
        taskScheduler.schedule(task, new PeriodicTrigger(period, timeUnit));
    }
}

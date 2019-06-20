package com.lrs.core.monitor.timer;

import com.lrs.core.monitor.service.IServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MonitorTimer {

    @Autowired
    private IServerService serverService;


    @Scheduled(fixedDelay = 5*1000)
    public void pingMod2(){
        try {
            System.out.println("===============2定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(2);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod3(){
        try {
            System.out.println("===============3定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(3);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

}

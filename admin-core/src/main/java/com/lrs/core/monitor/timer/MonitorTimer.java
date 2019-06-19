package com.lrs.core.monitor.timer;

import com.lrs.core.monitor.service.IServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MonitorTimer {

    @Autowired
    private IServerService serverService;

    /**
     * 没一分钟执行一次
     */
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void pingByMin() {
//        System.out.println("aaaaa");
//        try {
//            Ping.ping02("127.0.0.1");
//        }catch (Exception e){
//            log.error(e.getMessage(),e);
//        }
    }

    /**
     * 每10秒执行
     */
    @Scheduled(fixedDelay = 10*1000)
    public void pingBySec(){
        try {
            serverService.monitor();
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
}

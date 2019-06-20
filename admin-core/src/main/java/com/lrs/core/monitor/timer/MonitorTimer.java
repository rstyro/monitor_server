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
    private static final int modNumber =10;

    @Scheduled(fixedDelay = 5*1000)
    public void pingMod1(){
        try {
            log.info("===============1定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(modNumber,1);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod2(){
        try {
            log.info("===============2定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(modNumber,2);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod3(){
        try {
            log.info("===============3定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(modNumber,3);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod4(){
        try {
            log.info("===============4定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(modNumber,4);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod5(){
        try {
            log.info("===============5定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(modNumber,5);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod6(){
        try {
            log.info("===============6定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(modNumber,6);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod7(){
        try {
            log.info("===============7定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(modNumber,7);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod8(){
        try {
            log.info("===============8定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(modNumber,8);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod9(){
        try {
            log.info("===============9定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(modNumber,9);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod10(){
        try {
            log.info("===============0定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(modNumber,0);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

}

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
    public void pingMod0(){
        try {
            log.info("===============0定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(0);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    @Scheduled(fixedDelay = 5*1000)
    public void pingMod1(){
        try {
            log.info("===============1定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(1);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod2(){
        try {
            log.info("===============2定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(2);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod3(){
        try {
            log.info("===============3定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(3);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod4(){
        try {
            log.info("===============4定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(4);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod5(){
        try {
            log.info("===============5定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(5);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod6(){
        try {
            log.info("===============6定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(6);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod7(){
        try {
            log.info("===============7定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(7);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod8(){
        try {
            log.info("===============8定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(8);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod9(){
        try {
            log.info("===============9定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(9);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }


    @Scheduled(fixedDelay = 30*1000)
    public void sendMessage(){
        try {
            log.info("===============检测是否需要发送消息："+ LocalDateTime.now());
            serverService.sendMassage();
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }


}

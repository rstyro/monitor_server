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

    /**
     * 每10秒执行
     */
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod1(){
        try {
            System.out.println("===============1定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(1);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
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
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod4(){
        try {
            System.out.println("===============4定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(4);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod5(){
        try {
            System.out.println("===============5定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(5);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod6(){
        try {
            System.out.println("===============6定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(6);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod7(){
        try {
            System.out.println("===============7定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(7);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod8(){
        try {
            System.out.println("===============8定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(8);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod9(){
        try {
            System.out.println("===============9定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(9);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Scheduled(fixedDelay = 5*1000)
    public void pingMod10(){
        try {
            System.out.println("===============10定时任务执行时间点："+ LocalDateTime.now());
            serverService.monitor(10);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
}

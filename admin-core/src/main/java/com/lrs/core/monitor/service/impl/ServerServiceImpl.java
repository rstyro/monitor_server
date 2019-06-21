package com.lrs.core.monitor.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.common.constant.ApiResultEnum;
import com.lrs.common.constant.Result;
import com.lrs.common.dto.PageDTO;
import com.lrs.common.exception.ApiException;
import com.lrs.common.exception.TryAgainException;
import com.lrs.common.utils.PathsUtils;
import com.lrs.core.monitor.cache.CacheKey;
import com.lrs.core.monitor.entity.EmailSendDetail;
import com.lrs.core.monitor.entity.Ping;
import com.lrs.common.utils.date.DateUtil;
import com.lrs.core.admin.entity.User;
import com.lrs.core.aspect.IsTryAgain;
import com.lrs.core.common.email.service.MailService;
import com.lrs.core.monitor.entity.EmailAddress;
import com.lrs.core.monitor.entity.Server;
import com.lrs.core.monitor.mapper.ServerMapper;
import com.lrs.core.monitor.service.IEmailAddressService;
import com.lrs.core.monitor.service.IEmailSendDetailService;
import com.lrs.core.monitor.service.IServerService;
import com.lrs.core.sys.entity.Config;
import com.lrs.core.sys.enums.ConfigName;
import com.lrs.core.sys.enums.ConfigType;
import com.lrs.core.sys.service.IConfigService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 * 服务器监控 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2019-06-17
 */
@Service
@Transactional
@Slf4j
public class ServerServiceImpl extends ServiceImpl<ServerMapper, Server> implements IServerService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailService mailService;

    @Autowired
    private IEmailAddressService emailAddressService;

    @Autowired
    private IEmailSendDetailService emailSendDetailService;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private IServerService serverService;

    @Autowired
    private IConfigService configService;

    private final static String refreshKey = "refresh";
    private final static  int timeOut = 5;;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public Result getList(PageDTO dto) throws Exception {
        IPage<Server> page = new Page<>();
        if(dto.getPageNo() != null){
            page.setCurrent(dto.getPageNo());
        }
        if(dto.getPageSize() != null){
            page.setSize(dto.getPageSize());
        }
        QueryWrapper<Server> queryWrapper = new QueryWrapper();
            if(!StringUtils.isEmpty(dto.getKeyword())){
                queryWrapper.lambda()
                        .like(Server::getIp,dto.getKeyword())
                        .or()
                        .like(Server::getMark,dto.getKeyword());
            }
            queryWrapper.lambda().orderByDesc(Server::getCreateTime);
        IPage<Server> iPage = this.page(page, queryWrapper);
        return Result.ok(iPage);
    }

    @Override
    public Result add(Server item, User user) throws Exception {
        int count = this.count(new LambdaQueryWrapper<Server>().eq(Server::getIp, item.getIp()));
        if(count >0){
            throw new ApiException(ApiResultEnum.PARAMETER_IP_EXIST);
        }
        if(item.getCreateTime() == null){
            item.setStatus(0);
            item.setCreateBy(user.getUserId());
            item.setCreateTime(LocalDateTime.now());
        }
        this.save(item);
        int modResultNumber = (int) (item.getId() % CacheKey.MOD_NUMBER);
        //刷新缓存
        refreshModCache(modResultNumber);
        return Result.ok();
    }


    @Override
    public Result edit(Server item, User user) throws Exception {
        System.out.println("item======="+item);
        item.setModifyBy(user.getUserId());
        item.setModifyTime(LocalDateTime.now());
        item.setStatus(0);
        if(!this.updateById(item)){
            throw new ApiException(ApiResultEnum.UPDATE_IS_FAILD);
        }
        int modResultNumber = (int) (item.getId() % CacheKey.MOD_NUMBER);
        //刷新缓存
        refreshModCache(modResultNumber);
        refreshUnLineCache();
       return Result.ok();
    }

    @Override
    public Result del(Long id, User user) throws Exception {
        this.removeById(id);
        int modResultNumber = (int) (id % CacheKey.MOD_NUMBER);
        //刷新缓存
        refreshModCache(modResultNumber);
        refreshUnLineCache();
        return Result.ok();
    }

    @Override
    public Result getDetail(Long id) throws Exception {
    Server item = this.getOne(new QueryWrapper<Server>().lambda().eq(Server::getId,id));
         return Result.ok(item);
    }

    /**
     * 刷新取模列表缓存
     * @param resultNum
     */
    public synchronized List<Server> refreshModCache(int resultNum){
        List<Server> serverList = this.list(new LambdaQueryWrapper<Server>().apply("MOD(id,{0})={1}",CacheKey.MOD_NUMBER,resultNum));
        if(serverList == null){
            serverList = new ArrayList<>();
        }
        log.info("==更新取模列表=resultNum={},list.size={}",resultNum,serverList.size());
        redisTemplate.opsForValue().set(CacheKey.MOD_LIST_+resultNum,serverList,5,TimeUnit.DAYS);
        return serverList;
    }

    /**
     * 刷新掉线列表缓存
     */
    public synchronized List<Server> refreshUnLineCache(){
        List<Server> serverList = this.list(new LambdaQueryWrapper<Server>().eq(Server::getStatus,2));
        if(serverList == null){
            serverList = new ArrayList<>();
        }
        log.info("==更新掉线列表==list={}",JSON.toJSONString(serverList));
        redisTemplate.opsForValue().set(CacheKey.UN_LINE_LIST,serverList,5,TimeUnit.DAYS);
        return serverList;
    }

    /**
     * 获取掉线缓存列表
     * @return
     */
    public  List<Server> getUnlineCache(){
        return (List<Server>) redisTemplate.opsForValue().get(CacheKey.UN_LINE_LIST);
    }

    //检测掉线列表状态是否恢复
    public void checkUnlineList(List<String> ips){
        List<Server> servers = getUnlineCache();
        int count = 0;
        for(String ip:ips){
            for(Server server:servers){
                if(server.getIp().equalsIgnoreCase(ip)){
                    count=count+1;
                    break;
                }
            }
        }
        if(count>0){
            refreshUnLineCache();
        }
    }

    /**
     * 下载今日日志
     * @param id
     * @param request
     * @param response
     * @throws Exception
     */
    @Override
    public void downloadLogger(Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Server server = this.getById(id);
        //当天的日志文件地址
        String filePath = "logs/monitor/"+ DateUtil.getDays()+"_"+server.getIp()+".log";
        File file = null;
        file = new File(filePath);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            String aFileName = null;
            request.setCharacterEncoding("UTF-8");
            String agent = request.getHeader("User-Agent").toUpperCase();
            if ((agent.indexOf("MSIE") > 0) || ((agent.indexOf("RV") != -1) && (agent.indexOf("FIREFOX") == -1)))
                aFileName = URLEncoder.encode(server.getIp()+".log", "UTF-8");
            else {
                aFileName = new String((server.getIp()+".log").getBytes("UTF-8"), "ISO8859-1");
            }
            response.setContentType("application/octet-stream");//octet-stream为要下载文件是exe类型或看该文档http://www.w3school.com.cn/media/media_mimeref.asp
            response.setHeader("Content-disposition", "attachment; filename=" + aFileName);
            response.setHeader("Content-Length", String.valueOf(file.length()));
            bis = new BufferedInputStream(new FileInputStream(new File(PathsUtils.getAbsolutePath(filePath))));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length)))
                bos.write(buff, 0, bytesRead);
            System.out.println("success");
            bos.flush();
        } catch (Exception e) {
            System.out.println("失败！");
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
//				file.delete();
            } catch (Exception e) {
            }
        }
    }

    @Transactional
    public void monitor(int resultModNumber) throws Exception {
//        List<Server> serverList = this.list(new LambdaQueryWrapper<Server>().apply("MOD(id,{0})={1}",CacheKey.MOD_NUMBER,resultModNumber));
        List<Server> serverList = (List<Server>) redisTemplate.opsForValue().get(CacheKey.MOD_LIST_+resultModNumber);
        if(serverList == null){
            serverList = refreshModCache(resultModNumber);
        }
        List<Server> unLineList = new ArrayList<>();
        //收集正常状态的ip列表
        List<String> ips = new ArrayList<>();
        for(Server server:serverList){
            Callable<Server> serverCallable = new Callable<Server>() {
                @Override
                public Server call() throws Exception {
                   return serverService.pingServer(server);
                }
            };
            Future<Server> serverFuture = threadPoolTaskExecutor.submit(serverCallable);
            Server resultServer = serverFuture.get();
            if(resultServer != null){
                refreshUnLineCache();
                unLineList.add(resultServer);
            }else{
                ips.add(server.getIp());
            }
        }
        if(unLineList.size() > 0){
            //发送邮件
            sendEmail(unLineList);
        }
        if(ips.size()>0){
            checkUnlineList(ips);
        }
    }

    /**
     * ping 服务器IP
     * @param server
     * @return
     * @throws Exception
     */
    @Transactional
    @IsTryAgain
    public Server pingServer(Server server) throws Exception {
        server = serverService.getById(server.getId());
        boolean isUnline = false;
        Long lastSecond = server.getLastSecond();
        long nextTimeMillis = lastSecond+(server.getMonitorSecond()*1000);
        if(nextTimeMillis < System.currentTimeMillis()){
            log.info("=====ping_ip={}",server.getIp());
            Ping.savePingLogger(server.getIp(),"logs/monitor/"+ DateUtil.getDays()+"_"+server.getIp()+".log",server.getSendCount(),timeOut);
            server.setLastSecond(nextTimeMillis);
            if(Ping.isUnline(server.getIp(), server.getLostCount(),server.getSendCount(),timeOut)){
                log.info("=date={},ip={} is unline",LocalDateTime.now(),server.getIp());
                server.setStatus(2);
                isUnline=true;
            }else{
                server.setStatus(1);

            }
            server.setModifyTime(LocalDateTime.now());
            server.setLastPingTime(LocalDateTime.now());
            if(!serverService.updateById(server)){
                log.info("==ip={},server={},正在重试更新",server.getIp(), JSON.toJSONString(server));
                throw new TryAgainException(ApiResultEnum.FAILED);
            }
        }
        if(isUnline){
            return server;
        }
        return null;
    }

    /**
     * 监听，是否有掉线服务器
     * @return
     * @throws Exception
     */
    @Override
    public Result listener() throws Exception {
        String refresh = (String) redisTemplate.opsForValue().get(refreshKey);
        if(!StringUtils.isEmpty(refresh)){
            refresh="0";
        }else{
            //5分钟刷新一波
            redisTemplate.opsForValue().set(refreshKey,"1",5, TimeUnit.MINUTES);
            refresh="1";
        }
        //有几台有问题
        List<Server> serverList = getUnlineCache();
        List<Server> resultList = new ArrayList<>();
        if(serverList == null){
            serverList = new ArrayList<>();
            refreshUnLineCache();
        }else{
            long time = 5;
            try {
                Config config = configService.getConfigByCache(ConfigType.TIME, ConfigName.SEND_WARNING);
                time = Long.valueOf(config.getConfigValue());
            } catch (Exception e) {
                log.error("获取页面警告时间配置出错",e);
            }
            for(Server server:serverList){
                String value = (String) redisTemplate.opsForValue().get(CacheKey.SERVERIP_+server.getIp());
                if(StringUtils.isEmpty(value)){
                    redisTemplate.opsForValue().set(CacheKey.SERVERIP_+server.getIp(),"1",time, TimeUnit.MINUTES);
                    if(server.getIsOpenSound().equals(1)){
                        resultList.add(server);
                    }
                }
            }
        }
        Map<String,Object> data = new HashMap<>();
        data.put("refresh",refresh);
        data.put("list",resultList);
        return Result.ok(data);
    }

    public void sendEmail(List<Server> unLineList) {
        //创建邮件正文
        String redisKey = "email_adress_";
        Context context = new Context();
        context.setVariable("unLineList", unLineList);
        String emailContent = templateEngine.process("model/emailTemplate", context);
        String from = fromEmail;
        List<EmailAddress> emailAddressList = emailAddressService.list(new LambdaQueryWrapper<EmailAddress>());
        if(emailAddressList != null && emailAddressList.size() > 0){
            long time = 60;
            try {
                Config config = configService.getConfigByCache(ConfigType.TIME, ConfigName.SEND_EMAIL);
                time = Long.valueOf(config.getConfigValue());
            } catch (Exception e) {
               log.error("获取发送邮件时间配置出错",e);
            }
            StringBuilder sb = new StringBuilder();
            for(Server server:unLineList){
                sb.append(server.getIp()).append(",");
            }
            final String ips = sb.toString().substring(0,sb.toString().length() -1);
            for (EmailAddress emailAddress:emailAddressList) {
                String resultKey = redisKey + emailAddress.getToEmailAddress();
                String value = (String) redisTemplate.opsForValue().get(resultKey);
                if (StringUtils.isEmpty(value)) {
                    //加入缓存,10 分钟期限
                    redisTemplate.opsForValue().set(resultKey, "1", time, TimeUnit.MINUTES);
                    Runnable sendEmailRunable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mailService.sendHtmlMail(from, emailAddress.getToEmailAddress(), "服务器检测掉线警告", emailContent);
                                saveSendRecord(from, emailAddress.getToEmailAddress(),ips);
                            } catch (Exception e) {
                                redisTemplate.delete(resultKey);
                                log.error(e.getMessage(),e);
                            }
                        }
                    };
                    threadPoolTaskExecutor.execute(sendEmailRunable);
                }
            }
        }
    }

    //保存发送邮件记录
    public void saveSendRecord(String from,String toAddress,String ips)throws Exception{
        EmailSendDetail emailSendDetail = new EmailSendDetail();
        emailSendDetail.setContent("服务器报警邮件")
        .setIps(ips)
        .setFromEmail(from)
        .setToEmail(toAddress)
        .setCreateTime(LocalDateTime.now());
        emailSendDetailService.save(emailSendDetail);
    }
}

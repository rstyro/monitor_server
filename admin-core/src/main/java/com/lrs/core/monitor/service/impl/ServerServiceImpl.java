package com.lrs.core.monitor.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.lrs.common.constant.ApiResultEnum;
import com.lrs.common.constant.Result;
import com.lrs.common.dto.PageDTO;
import com.lrs.common.exception.ApiException;
import com.lrs.common.exception.TryAgainException;
import com.lrs.common.utils.PathsUtils;
import com.lrs.common.utils.UnicodeUtils;
import com.lrs.core.monitor.cache.CacheKey;
import com.lrs.core.monitor.entity.ReceiveAddressSendDetail;
import com.lrs.core.monitor.entity.Ping;
import com.lrs.common.utils.date.DateUtil;
import com.lrs.core.admin.entity.User;
import com.lrs.core.aspect.IsTryAgain;
import com.lrs.core.common.email.service.MailService;
import com.lrs.core.monitor.entity.ReceiveAddress;
import com.lrs.core.monitor.entity.Server;
import com.lrs.core.monitor.mapper.ServerMapper;
import com.lrs.core.monitor.service.IEmailAddressService;
import com.lrs.core.monitor.service.IEmailSendDetailService;
import com.lrs.core.monitor.service.IServerService;
import com.lrs.core.sms.SmsService;
import com.lrs.core.sys.entity.Config;
import com.lrs.core.sys.enums.ConfigName;
import com.lrs.core.sys.enums.ConfigType;
import com.lrs.core.sys.service.IConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
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

    @Autowired
    private SmsService smsService;

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
        if(servers == null || servers.size() <1){
            return;
        }
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
    @IsTryAgain
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
            sendEmailAndSMS(unLineList);
        }
        if(ips.size()>0){
            checkUnlineList(ips);
        }
    }

    @Override
    public synchronized void sendMassage() throws Exception {
        long emailTime = getConfigTime(ConfigName.SEND_EMAIL);
        long mobileTime = getConfigTime(ConfigName.SEND_MOBILE);
        String resultSMSKey = "send_adress_sms_";
        String resultEmailKey = "send_adress_email_";
        List<Server> sendSmsList = (List<Server>) redisTemplate.opsForValue().get(CacheKey.SEND_SMS_LIST);
        List<Server> sendEmailList = (List<Server>) redisTemplate.opsForValue().get(CacheKey.SEND_EMAIL_LIST);
        String ips = getIps(sendEmailList);
        //发送短信或邮件提醒
        List<ReceiveAddress> receiveAddressList = emailAddressService.list(new LambdaQueryWrapper<ReceiveAddress>());
        if(receiveAddressList != null && receiveAddressList.size() > 0) {
            //创建邮件正文
            Context context = new Context();
            context.setVariable("unLineList", sendEmailList);
            String emailContent = templateEngine.process("model/emailTemplate", context);
            String from = fromEmail;

            for (ReceiveAddress receiveAddress:receiveAddressList) {
                if(receiveAddress.getType().intValue() ==2 ){
                    if(sendSmsList != null && sendSmsList.size()>0){
                        for(Server server:sendSmsList){
                            try {
                                ArrayList<String> params = new ArrayList<>();
                                params.add(server.getServerName());
                                params.add(server.getIp().length()>12?server.getIp().substring(0,9)+"...":server.getIp());
                                SmsSingleSenderResult smsSingleSenderResult = smsService.sendSmsById(receiveAddress.getToAddress(), params);
                                log.info("发送短信的结果={},receiveAddress={},parames={},errMsg={}",smsSingleSenderResult,receiveAddress,params, UnicodeUtils.unicodeToCn(smsSingleSenderResult.errMsg));
                                if(smsSingleSenderResult.result == 0){
                                    saveSendRecord(2,"",receiveAddress.getToAddress(),server.getIp());
                                    String redisKey = resultSMSKey+server.getIp();
                                    redisTemplate.opsForValue().set(redisKey, "1", mobileTime, TimeUnit.MINUTES);
                                }else{
                                    log.info("发送短信的错误结果={},receiveAddress={},parames={}，errMsg={}",smsSingleSenderResult,receiveAddress,params,UnicodeUtils.unicodeToCn(smsSingleSenderResult.errMsg));
                                }
                            } catch (Exception e) {
                                log.error("发送短信失败",e);
                            }
                        }
                    }
                }else{
                    if(sendEmailList != null && sendEmailList.size()>0){
                        try {
                            Runnable sendEmailRunable = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        mailService.sendHtmlMail(from, receiveAddress.getToAddress(), "服务器检测掉线警告", emailContent);
                                        saveSendRecord(1,from, receiveAddress.getToAddress(),ips);
                                    } catch (Exception e) {
                                        log.error(e.getMessage(),e);
                                    }
                                }
                            };
                            threadPoolTaskExecutor.execute(sendEmailRunable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            //email放入缓存
            if(sendEmailList != null && sendEmailList.size()>0){
                for(Server server:sendEmailList){
                    String redisKey = resultEmailKey+server.getIp();
                    redisTemplate.opsForValue().set(redisKey, "1", emailTime, TimeUnit.MINUTES);
                }
            }
            //发完就删除
            redisTemplate.delete(CacheKey.SEND_EMAIL_LIST);
            redisTemplate.delete(CacheKey.SEND_SMS_LIST);
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

    public String getIps(List<Server> unLineList){
        StringBuilder sb = new StringBuilder();
        if(unLineList == null) return  "";
        for(Server server:unLineList) {
            sb.append(server.getIp()).append(",");
        }
        String ips = sb.toString();
        if(!StringUtils.isEmpty(ips)){
            ips = ips.substring(0,sb.toString().length() -1);
        }
        return ips;
    }

    public synchronized void sendEmailAndSMS(List<Server> unLineList) {
        List<Server> sendSmsList = (List<Server>) redisTemplate.opsForValue().get(CacheKey.SEND_SMS_LIST);
        List<Server> sendEmailList = (List<Server>) redisTemplate.opsForValue().get(CacheKey.SEND_EMAIL_LIST);
        if(sendSmsList == null){
            sendSmsList = new ArrayList<>();
        }
        if(sendEmailList == null){
            sendEmailList = new ArrayList<>();
        }
        String resultSMSKey = "send_adress_sms_";
        String resultEmailKey = "send_adress_email_";
        for(Server server:unLineList){
            resultSMSKey = resultSMSKey +server.getIp();
            resultEmailKey = resultEmailKey +server.getIp();
            String smsKeyValue = (String) redisTemplate.opsForValue().get(resultSMSKey);
            String emailKeyValue = (String) redisTemplate.opsForValue().get(resultEmailKey);
            //因为发邮件个发短信的事件间隔可能不一样，所以弄两个
            if (StringUtils.isEmpty(smsKeyValue)) {
                sendSmsList.add(server);
            }
            if(StringUtils.isEmpty(emailKeyValue)){
                sendEmailList.add(server);
            }
        }
        redisTemplate.opsForValue().set(CacheKey.SEND_EMAIL_LIST,sendEmailList);
        redisTemplate.opsForValue().set(CacheKey.SEND_SMS_LIST,sendSmsList);
    }

    public long getConfigTime(ConfigName configName){
        long time = 60;
        try {
            Config config = configService.getConfigByCache(ConfigType.TIME, configName);
            time = Long.valueOf(config.getConfigValue());
        } catch (Exception e) {
            log.error("获取时间配置出错", e);
        }
        return time;
    }

    //保存发送邮件记录
    public void saveSendRecord(int type,String from,String toAddress,String ips)throws Exception{
        ReceiveAddressSendDetail receiveAddressSendDetail = new ReceiveAddressSendDetail();
        receiveAddressSendDetail.setContent("服务器报警邮件")
        .setType(type)
        .setIps(ips)
        .setFromAddress(from)
        .setToAddress(toAddress)
        .setCreateTime(LocalDateTime.now());
        emailSendDetailService.save(receiveAddressSendDetail);
    }

    public void saveSendRecord(ArrayList<String> mobileList,String ips) throws Exception{
        if(mobileList != null && mobileList.size()>0){
            List<ReceiveAddressSendDetail> sendDetailList = new ArrayList<>();
            for(String mobile:mobileList){
                ReceiveAddressSendDetail receiveAddressSendDetail = new ReceiveAddressSendDetail();
                receiveAddressSendDetail.setContent("服务器报警短信")
                        .setType(1)
                        .setIps(ips)
                        .setFromAddress("")
                        .setToAddress(mobile)
                        .setCreateTime(LocalDateTime.now());
                sendDetailList.add(receiveAddressSendDetail);
            }
            if(sendDetailList.size() > 0){
                emailSendDetailService.saveBatch(sendDetailList,sendDetailList.size());
            }
        }
    }
}

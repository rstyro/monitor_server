package com.lrs.core.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.common.constant.Const;
import com.lrs.common.constant.Result;
import com.lrs.common.dto.PageDTO;
import com.lrs.common.utils.Ping;
import com.lrs.common.utils.date.DateUtil;
import com.lrs.core.admin.entity.User;
import com.lrs.core.monitor.entity.Server;
import com.lrs.core.monitor.mapper.ServerMapper;
import com.lrs.core.monitor.service.IServerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;


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
public class ServerServiceImpl extends ServiceImpl<ServerMapper, Server> implements IServerService {

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
        IPage<Server> iPage = this.page(page, queryWrapper);
        return Result.ok(iPage);
    }

    @Override
    public Result add(Server item, User user) throws Exception {
        if(item.getCreateTime() == null){
            item.setCreateBy(user.getUserId());
            item.setCreateTime(LocalDateTime.now());
        }
        this.save(item);
        return Result.ok();
    }

    @Override
    public Result edit(Server item, User user) throws Exception {
        item.setModifyBy(user.getUserId());
        item.setModifyTime(LocalDateTime.now());
        this.updateById(item);
       return Result.ok();
    }

    @Override
    public Result del(Long id, User user) throws Exception {
        this.removeById(id);
        return Result.ok();
    }

    @Override
    public Result getDetail(Long id) throws Exception {
    Server item = this.getOne(new QueryWrapper<Server>().lambda().eq(Server::getId,id));
         return Result.ok(item);
    }

    @Override
    public void monitor() throws Exception {
        List<Server> serverList = this.list(new LambdaQueryWrapper<Server>().eq(Server::getIsDel, Const.NO));
        for(Server server:serverList){
            Long lastSecond = server.getLastSecond();
            long nextTimeMillis = lastSecond+(server.getMonitorSecond()*1000);
            if(nextTimeMillis < System.currentTimeMillis()){
                Ping.savePingLogger(server.getIp(),"logs/monitor/"+ DateUtil.getDays()+"_"+server.getIp()+".log");
                server.setLastSecond(nextTimeMillis);
                if(Ping.isUnline(server.getIp(), server.getLostCount())){
                    server.setStatus(2);
                }else{
                    server.setStatus(1);
                }
                this.updateById(server);
            }
        }
    }
}

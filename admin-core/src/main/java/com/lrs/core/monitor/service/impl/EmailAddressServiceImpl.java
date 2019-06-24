package com.lrs.core.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.constant.Result;
import com.lrs.common.dto.PageDTO;
import com.lrs.core.monitor.entity.ReceiveAddress;

import javax.servlet.http.HttpSession;

import com.lrs.core.monitor.mapper.EmailAddressMapper;
import com.lrs.core.monitor.service.IEmailAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


/**
 * <p>
 * 报警邮件发送地址列表 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2019-06-19
 */
@Service
public class EmailAddressServiceImpl extends ServiceImpl<EmailAddressMapper, ReceiveAddress> implements IEmailAddressService{

    @Override
    public Result getList(PageDTO dto) throws Exception {
        IPage<ReceiveAddress> page = new Page<>();
        if(dto.getPageNo() != null){
            page.setCurrent(dto.getPageNo());
        }
        if(dto.getPageSize() != null){
            page.setSize(dto.getPageSize());
        }
        QueryWrapper<ReceiveAddress> queryWrapper = new QueryWrapper();
    //        if(!StringUtils.isEmpty(dto.getKeyword())){
    //            queryWrapper.lambda()
    //                    .like(ReceiveAddress::getAuther,dto.getKeyword())
    //                    .like(ReceiveAddress::getContent,dto.getKeyword())
    //                    .like(ReceiveAddress::getTitle,dto.getKeyword());
    //        }
        IPage<ReceiveAddress> iPage = this.page(page, queryWrapper);
        return Result.ok(iPage);
    }

    @Override
    public Result add(ReceiveAddress item, HttpSession session) throws Exception {
        item.setCreateTime(LocalDateTime.now());
        this.save(item);
        return Result.ok();
    }

    @Override
    public Result edit(ReceiveAddress item, HttpSession session) throws Exception {
        this.updateById(item);
       return Result.ok();
    }

    @Override
    public Result del(Long id, HttpSession session) throws Exception {
        this.removeById(id);
        return Result.ok();
    }

    @Override
    public Result getDetail(Long id) throws Exception {
    ReceiveAddress item = this.getOne(new QueryWrapper<ReceiveAddress>().lambda().eq(ReceiveAddress::getId,id));
         return Result.ok(item);
    }
}

package com.lrs.core.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.constant.Result;
import com.lrs.common.dto.PageDTO;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpSession;

import com.lrs.core.monitor.entity.EmailAddress;
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
public class EmailAddressServiceImpl extends ServiceImpl<EmailAddressMapper, EmailAddress> implements IEmailAddressService{

    @Override
    public Result getList(PageDTO dto) throws Exception {
        IPage<EmailAddress> page = new Page<>();
        if(dto.getPageNo() != null){
            page.setCurrent(dto.getPageNo());
        }
        if(dto.getPageSize() != null){
            page.setSize(dto.getPageSize());
        }
        QueryWrapper<EmailAddress> queryWrapper = new QueryWrapper();
    //        if(!StringUtils.isEmpty(dto.getKeyword())){
    //            queryWrapper.lambda()
    //                    .like(EmailAddress::getAuther,dto.getKeyword())
    //                    .like(EmailAddress::getContent,dto.getKeyword())
    //                    .like(EmailAddress::getTitle,dto.getKeyword());
    //        }
        IPage<EmailAddress> iPage = this.page(page, queryWrapper);
        return Result.ok(iPage);
    }

    @Override
    public Result add(EmailAddress item, HttpSession session) throws Exception {
        item.setCreateTime(LocalDateTime.now());
        this.save(item);
        return Result.ok();
    }

    @Override
    public Result edit(EmailAddress item, HttpSession session) throws Exception {
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
    EmailAddress item = this.getOne(new QueryWrapper<EmailAddress>().lambda().eq(EmailAddress::getId,id));
         return Result.ok(item);
    }
}

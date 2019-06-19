package com.lrs.core.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.constant.Result;
import com.lrs.common.dto.PageDTO;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpSession;

import com.lrs.core.monitor.entity.EmailSendDetail;
import com.lrs.core.monitor.mapper.EmailSendDetailMapper;
import com.lrs.core.monitor.service.IEmailSendDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


/**
 * <p>
 * 邮件发送记录 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2019-06-19
 */
@Service
public class EmailSendDetailServiceImpl extends ServiceImpl<EmailSendDetailMapper, EmailSendDetail> implements IEmailSendDetailService{

    @Override
    public Result getList(PageDTO dto) throws Exception {
        IPage<EmailSendDetail> page = new Page<>();
        if(dto.getPageNo() != null){
            page.setCurrent(dto.getPageNo());
        }
        if(dto.getPageSize() != null){
            page.setSize(dto.getPageSize());
        }
        QueryWrapper<EmailSendDetail> queryWrapper = new QueryWrapper();
    //        if(!StringUtils.isEmpty(dto.getKeyword())){
    //            queryWrapper.lambda()
    //                    .like(EmailSendDetail::getAuther,dto.getKeyword())
    //                    .like(EmailSendDetail::getContent,dto.getKeyword())
    //                    .like(EmailSendDetail::getTitle,dto.getKeyword());
    //        }
        IPage<EmailSendDetail> iPage = this.page(page, queryWrapper);
        return Result.ok(iPage);
    }

    @Override
    public Result add(EmailSendDetail item, HttpSession session) throws Exception {
        item.setCreateTime(LocalDateTime.now());
        this.save(item);
        return Result.ok();
    }

    @Override
    public Result edit(EmailSendDetail item, HttpSession session) throws Exception {
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
    EmailSendDetail item = this.getOne(new QueryWrapper<EmailSendDetail>().lambda().eq(EmailSendDetail::getId,id));
         return Result.ok(item);
    }
}

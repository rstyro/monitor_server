package com.lrs.core.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.constant.Result;
import com.lrs.common.dto.PageDTO;
import com.lrs.core.monitor.entity.ReceiveAddressSendDetail;

import javax.servlet.http.HttpSession;

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
public class EmailSendDetailServiceImpl extends ServiceImpl<EmailSendDetailMapper, ReceiveAddressSendDetail> implements IEmailSendDetailService{

    @Override
    public Result getList(PageDTO dto) throws Exception {
        IPage<ReceiveAddressSendDetail> page = new Page<>();
        if(dto.getPageNo() != null){
            page.setCurrent(dto.getPageNo());
        }
        if(dto.getPageSize() != null){
            page.setSize(dto.getPageSize());
        }
        QueryWrapper<ReceiveAddressSendDetail> queryWrapper = new QueryWrapper();
    //        if(!StringUtils.isEmpty(dto.getKeyword())){
    //            queryWrapper.lambda()
    //                    .like(ReceiveAddressSendDetail::getAuther,dto.getKeyword())
    //                    .like(ReceiveAddressSendDetail::getContent,dto.getKeyword())
    //                    .like(ReceiveAddressSendDetail::getTitle,dto.getKeyword());
    //        }
        IPage<ReceiveAddressSendDetail> iPage = this.page(page, queryWrapper);
        return Result.ok(iPage);
    }

    @Override
    public Result add(ReceiveAddressSendDetail item, HttpSession session) throws Exception {
        item.setCreateTime(LocalDateTime.now());
        this.save(item);
        return Result.ok();
    }

    @Override
    public Result edit(ReceiveAddressSendDetail item, HttpSession session) throws Exception {
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
    ReceiveAddressSendDetail item = this.getOne(new QueryWrapper<ReceiveAddressSendDetail>().lambda().eq(ReceiveAddressSendDetail::getId,id));
         return Result.ok(item);
    }
}

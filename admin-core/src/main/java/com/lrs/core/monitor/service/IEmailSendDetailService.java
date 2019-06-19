package com.lrs.core.monitor.service;

import com.lrs.core.monitor.entity.EmailSendDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.common.constant.Result;
import com.lrs.common.dto.PageDTO;
import javax.servlet.http.HttpSession;

/**
 * <p>
 *  邮件发送记录 服务类
 * </p>
 *
 * @author rstyro
 * @since 2019-6-19
 */
public interface IEmailSendDetailService extends IService<EmailSendDetail> {
    public Result getList(PageDTO dto) throws  Exception;
    public Result add(EmailSendDetail item, HttpSession session) throws  Exception;
    public Result edit(EmailSendDetail item, HttpSession session) throws  Exception;
    public Result del(Long id, HttpSession session) throws  Exception;
    public Result getDetail(Long id) throws  Exception;
}

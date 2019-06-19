package com.lrs.core.monitor.service;

import com.lrs.core.monitor.entity.EmailAddress;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.common.constant.Result;
import com.lrs.common.dto.PageDTO;
import javax.servlet.http.HttpSession;

/**
 * <p>
 *  报警邮件发送地址列表 服务类
 * </p>
 *
 * @author rstyro
 * @since 2019-6-19
 */
public interface IEmailAddressService extends IService<EmailAddress> {
    public Result getList(PageDTO dto) throws  Exception;
    public Result add(EmailAddress item, HttpSession session) throws  Exception;
    public Result edit(EmailAddress item, HttpSession session) throws  Exception;
    public Result del(Long id, HttpSession session) throws  Exception;
    public Result getDetail(Long id) throws  Exception;
}

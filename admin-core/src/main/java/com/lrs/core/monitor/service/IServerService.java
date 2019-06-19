package com.lrs.core.monitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.common.constant.Result;
import com.lrs.common.dto.PageDTO;
import com.lrs.core.admin.entity.User;
import com.lrs.core.monitor.entity.Server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务器监控 服务类
 * </p>
 *
 * @author rstyro
 * @since 2019-6-17
 */
public interface IServerService extends IService<Server> {
    public Result getList(PageDTO dto) throws  Exception;
    public Result add(Server item, User user) throws  Exception;
    public Result edit(Server item, User user) throws  Exception;
    public Result del(Long id, User user) throws  Exception;
    public Result getDetail(Long id) throws  Exception;
    public void downloadLogger(Long id, HttpServletRequest request,HttpServletResponse response) throws  Exception;

    public void monitor() throws Exception;
}

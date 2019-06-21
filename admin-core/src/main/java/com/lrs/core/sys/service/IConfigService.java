package com.lrs.core.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.common.constant.Result;
import com.lrs.common.dto.PageDTO;
import com.lrs.core.sys.entity.Config;
import com.lrs.core.sys.enums.ConfigName;
import com.lrs.core.sys.enums.ConfigType;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  系统配置项 服务类
 * </p>
 *
 * @author rstyro
 * @since 2019-6-21
 */
public interface IConfigService extends IService<Config> {
    public Result getList(PageDTO dto) throws  Exception;
    public Result add(Config item, HttpSession session) throws  Exception;
    public Result edit(Config item, HttpSession session) throws  Exception;
    public Result del(Long id, HttpSession session) throws  Exception;
    public Result getDetail(Long id) throws  Exception;
    public Config getConfigByCache(ConfigType configType, ConfigName configName) throws Exception;
}

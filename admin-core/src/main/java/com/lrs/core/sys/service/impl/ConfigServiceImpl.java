package com.lrs.core.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.constant.ApiResultEnum;
import com.lrs.common.constant.Result;
import com.lrs.common.dto.PageDTO;
import com.lrs.common.exception.ApiException;
import com.lrs.core.sys.enums.ConfigName;
import com.lrs.core.sys.enums.ConfigType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpSession;

import com.lrs.core.sys.entity.Config;
import com.lrs.core.sys.mapper.ConfigMapper;
import com.lrs.core.sys.service.IConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * <p>
 * 系统配置项 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2019-06-21
 */
@Service
@Transactional
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements IConfigService{

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public Result getList(PageDTO dto) throws Exception {
        IPage<Config> page = new Page<>();
        if(dto.getPageNo() != null){
            page.setCurrent(dto.getPageNo());
        }
        if(dto.getPageSize() != null){
            page.setSize(dto.getPageSize());
        }
        QueryWrapper<Config> queryWrapper = new QueryWrapper();
    //        if(!StringUtils.isEmpty(dto.getKeyword())){
    //            queryWrapper.lambda()
    //                    .like(Config::getAuther,dto.getKeyword())
    //                    .like(Config::getContent,dto.getKeyword())
    //                    .like(Config::getTitle,dto.getKeyword());
    //        }
        IPage<Config> iPage = this.page(page, queryWrapper);
        return Result.ok(iPage);
    }

    @Override
    public Result add(Config item, HttpSession session) throws Exception {
        int count = this.count(new LambdaQueryWrapper<Config>().eq(Config::getConfigType, item.getConfigType()).eq(Config::getConfigName, item.getConfigName()));
        if(count > 0){
            throw new ApiException(ApiResultEnum.CONFIG_IS_EXIST);
        }
        this.save(item);
        return Result.ok();
    }

    @Override
    public Result edit(Config item, HttpSession session) throws Exception {
        this.updateById(item);
        setCacheConfig(item.getConfigType(),item.getConfigName());
       return Result.ok();
    }

    @Override
    public Result del(Long id, HttpSession session) throws Exception {
        Config config = this.getById(id);
        redisTemplate.delete(config.getConfigType()+config.getConfigName());
        this.removeById(id);
        return Result.ok();
    }

    @Override
    public Result getDetail(Long id) throws Exception {
    Config item = this.getOne(new QueryWrapper<Config>().lambda().eq(Config::getId,id));
         return Result.ok(item);
    }

    @Override
    public Config getConfigByCache(ConfigType configType, ConfigName configName) throws Exception {
        Config  config = getCacheConfig(configType.getKey(),configName.getKey());
        if(config == null){
            config = setCacheConfig(configType.getKey(),configName.getKey());
        }
        return config;
    }

    public Config setCacheConfig(String configType, String configName){
        Config config = this.getOne(new LambdaQueryWrapper<Config>().eq(Config::getConfigType,configType).eq(Config::getConfigName,configName).last("limit 1"));
        if(config != null){
            redisTemplate.opsForValue().set(configType+configName,config,10, TimeUnit.DAYS);
        }
        return config;
    }

    public Config getCacheConfig(String configType, String configName){
        return (Config) redisTemplate.opsForValue().get(configType+configName);
    }
}

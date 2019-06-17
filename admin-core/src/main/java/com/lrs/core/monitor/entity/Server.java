package com.lrs.core.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 服务器监控
 * </p>
 *
 * @author rstyro
 * @since 2019-06-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("monitor_server")
public class Server implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 服务器名称
     */
    private String serverName;

    private String ip;

    /**
     * 备注
     */
    private String mark;

    @TableLogic
    private Integer isDel;


    private Integer status;

    /**
     * 丢包次数
     */
    private Integer lostCount;

    /**
     * 是否开启声音
     */
    private Integer isOpenSound;

    /**
     * 多久监听一次，（单位：秒）
     */
    private Long monitorSecond;

    //最后执行的毫秒值
    private Long lastSecond;


    @Version
    private Long version;

    private Long createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private Long modifyBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyTime;


}

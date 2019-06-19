package com.lrs.core.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 邮件发送记录
 * </p>
 *
 * @author rstyro
 * @since 2019-06-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("monitor_email_send_detail")
public class EmailSendDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 邮件发送地址
     */
    private String fromEmail;

    /**
     * 发送给谁
     */
    private String toEmail;

    private String content;

    private String ip;

    @TableLogic
    private Integer isDel;

    private Long createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;


}

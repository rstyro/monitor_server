package com.lrs.core.sys.enums;

/**
 * 配置 名称
 */
public enum ConfigName {

    SEND_EMAIL("send_email","发送邮件提醒间隔(单位：分钟)"),
    SEND_WARNING("send_warning","发送页面报警间隔（单位：分钟）"),
    ;
    private String key;
    private String desc;

    public String getKey() {
        return key;
    }

    public String getDesc() {
        return desc;
    }

    private ConfigName(String key, String desc){
        this.desc=desc;
        this.key=key;
    }
}

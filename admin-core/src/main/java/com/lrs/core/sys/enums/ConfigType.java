package com.lrs.core.sys.enums;

/**
 * 配置文件类型
 */
public enum ConfigType {
    PROPORTION("proportion","比例"),
    LEVEL("level","等级"),
    SWITCH("switch","开关"),
    TIME("time","时间配置"),

    ;
    private String key;
    private String desc;

    public String getKey() {
        return key;
    }

    public String getDesc() {
        return desc;
    }

    private ConfigType(String key, String desc){
        this.desc=desc;
        this.key=key;
    }
}

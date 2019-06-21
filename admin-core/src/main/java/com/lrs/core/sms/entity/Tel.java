package com.lrs.core.sms.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Tel {
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 国家码
     */
    private String nationcode;
}

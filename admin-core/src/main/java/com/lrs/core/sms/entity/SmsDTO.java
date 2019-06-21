package com.lrs.core.sms.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SmsDTO {
    private String ext;
    private String extend;
    private String[] params;
    private String sig;
    private String sign;
    private Tel tel;
    private Long time;
    private Integer tpl_id;

}

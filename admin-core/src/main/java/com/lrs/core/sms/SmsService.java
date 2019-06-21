package com.lrs.core.sms;

import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SmsService {
    @Value("${tencent.sms.appId}")
    private Integer appid;

    @Value("${tencent.sms.appKey}")
    private String appkey;

    @Value("${tencent.sms.templateId}")
    private int templateId;

    /**
     * 模板 单发
     * @param phoneNumber 手机号
     * @param params    占位符
     * @throws Exception
     */
    public SmsSingleSenderResult sendSmsById(String phoneNumber,ArrayList<String> params) throws Exception{
        SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
        SmsSingleSenderResult result = ssender.sendWithParam("86",phoneNumber, templateId, params, "", "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
        return result;
    }

    /**
     * 模板 群发
     * @param phoneNumbers
     * @param params
     * @throws Exception
     */
    public SmsMultiSenderResult sendMultiSmsById(ArrayList<String> phoneNumbers,ArrayList<String> params) throws Exception{
        SmsMultiSender msender = new SmsMultiSender(appid, appkey);
        SmsMultiSenderResult result =  msender.sendWithParam("86", phoneNumbers, templateId, params, "", "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
        return result;
    }

    public static void main(String[] args) throws Exception {
        int appid=1400223915;
        String appkey = "3648027dcc6df529d092dae13e92aac6";
        String phoneNumber = "18818864644";
        int templateId = 358179;
        String smsSign = "";
        ArrayList<String> params = new ArrayList<>();
        params.add("本机");
        params.add("127.0.0.1");
        SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
        SmsSingleSenderResult result = ssender.sendWithParam("86",phoneNumber, templateId, params, smsSign, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
        System.out.println(result);
    }
}

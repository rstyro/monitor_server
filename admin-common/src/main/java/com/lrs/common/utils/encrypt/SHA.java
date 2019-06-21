package com.lrs.common.utils.encrypt;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA {
	 /**  
     * 定义加密方式  
     */    
    private final static String KEY_SHA = "SHA";    
    private final static String KEY_SHA1 = "SHA-1";  
      
    /**  
     * 全局数组  
     */    
    private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",    
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" ,"g","h"};
    
    /**  
     * 构造函数  
     */    
    public SHA() {    
    
    }    
    
    /**  
     * SHA 加密  
     * @param data 需要加密的字节数组  
     * @return 加密之后的字节数组  
     * @throws Exception  
     */    
    public static byte[] encryptSHA(byte[] data) throws Exception {    
        // 创建具有指定算法名称的信息摘要    
//        MessageDigest sha = MessageDigest.getInstance(KEY_SHA);    
        MessageDigest sha = MessageDigest.getInstance(KEY_SHA1);    
        // 使用指定的字节数组对摘要进行最后更新    
        sha.update(data);    
        // 完成摘要计算并返回    
        return sha.digest();    
    }    
    
    /**  
     * SHA 加密  
     * @param data 需要加密的字符串  
     * @return 加密之后的字符串  
     * @throws Exception  
     */    
    public static String encryptSHA(String data) throws Exception {    
        // 验证传入的字符串    
        if (data == null || data.equals("")) {    
            return "";    
        }    
        // 创建具有指定算法名称的信息摘要    
        MessageDigest sha = MessageDigest.getInstance(KEY_SHA);    
        // 使用指定的字节数组对摘要进行最后更新    
        sha.update(data.getBytes());    
        // 完成摘要计算    
        byte[] bytes = sha.digest();    
        // 将得到的字节数组变成字符串返回    
        return byteArrayToHexString(bytes);    
    }   
    
    public static String encryptSHA(String str,String ecryptCode) throws NoSuchAlgorithmException{
        if (str == null || str.equals("")) {    
            return "";    
        }    
        MessageDigest sha = MessageDigest.getInstance(ecryptCode);    
        sha.update(str.getBytes());    
        byte[] bytes = sha.digest();    
        return byteArrayToHexString(bytes);  
    }
    
    /**  
     * 将一个字节转化成十六进制形式的字符串  
     * @param b 字节数组  
     * @return 字符串  
     */    
    private static String byteToHexString(byte b) {    
        int ret = b;    
        //System.out.println("ret = " + ret);    
        if (ret < 0) {    
            ret += 256;    
        }    
        int m = ret / 16;    
        int n = ret % 16;    
        return hexDigits[m] + hexDigits[n];    
    }    
    
    /**  
     * 转换字节数组为十六进制字符串  
     * @param bytes 字节数组  
     * @return 十六进制字符串  
     */    
    private static String byteArrayToHexString(byte[] bytes) {    
        StringBuffer sb = new StringBuffer();    
        for (int i = 0; i < bytes.length; i++) {    
            sb.append(byteToHexString(bytes[i]));    
        }    
        return sb.toString();    
    }

    /**
     * SHA256
     *
     * @param content 内容
     * @param secret
     */
    public static String sha256HMAC(String content, String secret) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(content.getBytes());

            StringBuffer buf = new StringBuffer();
            String stmp;
            for (int n = 0; bytes != null && n < bytes.length; n++) {
                stmp = Integer.toHexString(bytes[n] & 0XFF);
                if (stmp.length() == 1) {
                    buf.append('0');
                }
                buf.append(stmp);
            }
            return buf.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * SHA 256 加密
     * @param content
     * @return
     */
    public static String SHA256(String content) {
        // 是否是有效字符串
        String sign = "";
        if (content != null && content.length() > 0) {
            try {
                // SHA 加密开始
                // 创建加密对象 并傳入加密類型
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                // 传入要加密的字符串
                messageDigest.update(content.getBytes());
                // 得到 byte 類型结果
                sign = byteArrayToHexString(messageDigest.digest());
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
        }else{
            return null;
        }
        return sign;
    }
    
    public static void main(String[] args) throws Exception {
		System.out.println(SHA.encryptSHA("admin", "SHA-1"));
		System.out.println(SHA.encryptSHA("admin", "SHA"));
		System.out.println(SHA.encryptSHA("d033e22ae348aeb5660fc2140aec35850c4da997"));

        System.out.println(SHA.SHA256("appkey=5f03a35d00ee52a21327ab048186a2c4&random=7226249334&time=1457336869"));
	}
}

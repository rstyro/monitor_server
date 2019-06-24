package com.lrs.common.utils;

public class UnicodeUtils {
    public static String unicodeToCn(String unicode) {
        /** 以 \ u 分割，因为java注释也能识别unicode，因此中间加了一个空格*/
        String[] strs = unicode.split("\\\\u");
        String returnStr = "";
        // 由于unicode字符串以 \ u 开头，因此分割出的第一个字符是""。
        for (int i = 1; i < strs.length; i++) {
            returnStr += (char) Integer.valueOf(strs[i], 16).intValue();
        }
        return returnStr;
    }

    public static String cnToUnicode(String cn) {
        char[] chars = cn.toCharArray();
        String returnStr = "";
        for (int i = 0; i < chars.length; i++) {
            returnStr += "\\u" + Integer.toString(chars[i], 16);
        }
        return returnStr;
    }

    public static void main(String[] args) {
        String unicode = "\\u624B\\u673A\\u53F730\\u79D2\\u9891\\u7387\\u9650\\u5236";
        System.out.println(unicodeToCn(unicode));
        System.out.println(cnToUnicode("4564564344236"));
        System.out.println(unicodeToCn("4564564344236").length());
    }
}

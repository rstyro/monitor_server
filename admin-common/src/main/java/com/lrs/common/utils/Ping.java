package com.lrs.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ping {
    public static boolean ping(String ipAddress) throws Exception {
        int  timeOut =  3000 ;  //超时应该在3钞以上
        boolean status = InetAddress.getByName(ipAddress).isReachable(timeOut);     // 当返回值是true时，说明host是可用的，false则不可。
        return status;
    }

    public static void ping02(String ipAddress) throws Exception {
        String line = null;
        try {
            Process pro = Runtime.getRuntime().exec("ping " + ipAddress);
            System.out.println(pro.toString());
            BufferedReader buf = new BufferedReader(new InputStreamReader(pro.getInputStream(),"gbk"));
            while ((line = buf.readLine()) != null){
//                System.out.println(line);
                System.out.println(new String(line.getBytes(),"utf-8"));
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void savePingLogger(String ipAddress,String path,int pingTimes, int timeOut) throws Exception {
        String line = null;
        try {
            Process pro = Runtime.getRuntime().exec("ping " + ipAddress+ " -n " + pingTimes    + " -w " + timeOut);
            System.out.println(pro.toString());
            BufferedReader buf = new BufferedReader(new InputStreamReader(pro.getInputStream(),"gbk"));
            FileUtils.write(buf,PathsUtils.getAbsolutePath(path));
            while ((line = buf.readLine()) != null){
                System.out.println(new String(line.getBytes(),"utf-8"));
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static boolean ping(String ipAddress, int pingTimes, int timeOut) {
        BufferedReader in = null;
        Runtime r = Runtime.getRuntime();  // 将要执行的ping命令,此命令是windows格式的命令
        String pingCommand = "ping " + ipAddress + " -n " + pingTimes    + " -w " + timeOut;
        try {   // 执行命令并获取输出
            System.out.println(pingCommand);
            Process p = r.exec(pingCommand);
            if (p == null) {
                return false;
            }
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));   // 逐行检查输出,计算类似出现=23ms TTL=62字样的次数
            int connectedCount = 0;
            String line = null;
            while ((line = in.readLine()) != null) {
                connectedCount += getCheckResult(line);
            }   // 如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真
            return connectedCount == pingTimes;
        } catch (Exception ex) {
            ex.printStackTrace();   // 出现异常则返回假
            return false;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getPingCount(String ipAddress, int pingTimes, int timeOut) {
        BufferedReader in = null;
        Runtime r = Runtime.getRuntime();  // 将要执行的ping命令,此命令是windows格式的命令
        String pingCommand = "ping " + ipAddress + " -n " + pingTimes    + " -w " + timeOut;
        try {   // 执行命令并获取输出
            System.out.println(pingCommand);
            Process p = r.exec(pingCommand);
            if (p == null) {
                return 0;
            }
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));   // 逐行检查输出,计算类似出现=23ms TTL=62字样的次数
            int connectedCount = 0;
            String line = null;
            while ((line = in.readLine()) != null) {
                connectedCount += getCheckResult(line);
            }   // 如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真
            return connectedCount;
        } catch (Exception ex) {
            ex.printStackTrace();   // 出现异常则返回假
            return 0;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否掉线
     * @param ipAddress
     * @param lostCount
     * @return
     */
    public static boolean isUnline(String ipAddress,int lostCount, int pingTimes, int timeOut) {
        int pingCount = getPingCount(ipAddress, pingTimes, timeOut);
        System.out.println("pingTimes - pingCount="+(pingTimes - pingCount));
        System.out.println("lostCount="+lostCount);
        System.out.println("ipAddress="+ipAddress+"isUnline="+((pingTimes - pingCount) > lostCount));
        if(pingTimes - pingCount > lostCount){
            return true;
        }
        return false;
    }

    public static boolean isUnline(String ipAddress,int lostCount) {
        int pingTimes=10;
        int timeOut=5;
        int pingCount = getPingCount(ipAddress, pingTimes, timeOut);
        System.out.println("pingTimes - pingCount="+(pingTimes - pingCount));
        System.out.println("lostCount="+lostCount);
        System.out.println("ipAddress="+ipAddress+"isUnline="+((pingTimes - pingCount) > lostCount));
        if(pingTimes - pingCount > lostCount){
            return true;
        }
        return false;
    }

    //若line含有=18ms TTL=16字样,说明已经ping通,返回1,否則返回0.
    private static int getCheckResult(String line) {  // System.out.println("控制台输出的结果为:"+line);
        Pattern pattern = Pattern.compile("(\\d+ms)(\\s+)(TTL=\\d+)",    Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            return 1;
        }
        return 0;
    }
    public static void main(String[] args) throws Exception {
        String ipAddress = "127.0.0.1";
        String ipAddress2 = "192.168.1.58";
        String ipAddress3 = "47.244.120.199";
        String ipAddress4 = "119.23.38.145";
//        System.out.println(ping(ipAddress4));
//        ping02(ipAddress4);

        System.out.println(ping(ipAddress,3,5));

        boolean unline = isUnline(ipAddress,  3);
        System.out.println(unline);
    }
}

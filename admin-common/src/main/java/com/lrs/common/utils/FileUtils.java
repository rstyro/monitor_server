package com.lrs.common.utils;

import com.lrs.common.utils.date.LocalDateTimeUtils;

import java.io.*;

public class FileUtils {

    public static void write(Reader reader,String path) throws IOException {
        File file = new File(path);
        mkParentDirs(file);
        PrintWriter out = new PrintWriter(new FileWriter(file,true));
        out.write("=====================日志记录日期:"+LocalDateTimeUtils.formatNow("yyyy-MM-dd HH:mm:ss")+"=======================");
        byte[] bytes = new byte[1024];
        int len = -1;
        while ((len = reader.read()) != -1) {
            //写入相关文件
            out.write(len);
        }
        out.write("\r\n");
        out.write("\r\n");
        out.write("\r\n");
        //清楚缓存
        out.flush();
        out.close();
    }

    /**
     * 创建所给文件或目录的父目录
     *
     * @param file 文件或目录
     * @return 父目录
     */
    public static File mkParentDirs(File file) {
        final File parentFile = file.getParentFile();
        if (null != parentFile && false == parentFile.exists()) {
            parentFile.mkdirs();
        }
        return parentFile;
    }

    //输出图片
//    public void writeImg(HttpServletResponse response, String address) throws IOException {
//        response.setContentType("image/jpeg");
//        FileInputStream is =this.query_getPhotoImageBlob(address);
//        if (is != null){
//            int i = is.available(); // 得到文件大小
//            byte data[] = new byte[i];
//            is.read(data); // 读数据
//            is.close();
//            response.setContentType("image/jpeg"); // 设置返回的文件类型
//            OutputStream toClient = response.getOutputStream(); // 得到向客户端输出二进制数据的对象
//            toClient.write(data); // 输出数据
//            toClient.close();
//        }
//    }
}

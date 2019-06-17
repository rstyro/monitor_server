package com.lrs.common.utils;

import java.io.*;

public class FileUtils {

    public static void write(Reader reader,String path) throws IOException {
        File file = new File(path);
        mkParentDirs(file);
        PrintWriter out = new PrintWriter(new FileWriter(file,true));
        byte[] bytes = new byte[1024];
        int len = -1;
        while ((len = reader.read()) != -1) {
            //写入相关文件
            out.write(len);
        }
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
}

package com.hzpd.utils;

import com.color.tools.mytools.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

public class GetFileSizeUtil {

    private static GetFileSizeUtil instance;

    public GetFileSizeUtil() {

    }

    public static GetFileSizeUtil getInstance() {
        if (instance == null) {
            instance = new GetFileSizeUtil();
        }
        return instance;
    }

    /**
     * 获取单个文件的大小
     *
     * @param f
     * @return
     * @throws Exception
     */
    public long getFileSizes(File f) {
        // XXX 这种读取文件大小的方式不精确
        long s = 0;
        FileInputStream fis = null;
        try {
            if (f.exists()) {
                fis = new FileInputStream(f);
                s = fis.available();
            } else {
                LogUtils.i("文件不存在");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return s;
    }

    /**
     * 获取文件夹的大小
     *
     * @return
     * @throws Exception
     */
    public long getFileDirSize(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileDirSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    /**
     * 转换文件的大小
     *
     * @param fileS
     * @return
     */
    public String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS <= 0) {
            fileSizeString="0B";
        } else if (0 < fileS && fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

}
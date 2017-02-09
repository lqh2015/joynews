package com.android.book;

import android.content.Context;

import com.android.book.interval.Util_Interval;

/**
 * Created by zhengnan on 2016/3/1.
 * 控制下载间隔的类
 */
public class DownloadIntervalCtrl {
    private static String myTag  = "pureDownload";//每个模块的此串应有所不同
    public static boolean isInInterval(Context ctx){
        return Util_Interval.getIns(ctx).isInInterval(myTag);
    }
    public static void seInterval(Context ctx,long time,boolean force){
        Util_Interval.getIns(ctx).setInterval(myTag, time, force);
    }
}

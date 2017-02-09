package com.hzpd.utils;

import android.view.View;

/**
 * Created by taoshuang on 2015/9/15.
 */
public class AvoidOnClickFastUtils {

    private static long lastClickTime;
    private static int hash;

    /**
     * 防止同一个View过快点击
     */
    public static boolean isFastDoubleClick(final View view) {
        long time = System.currentTimeMillis();
        if (view.hashCode() != hash) {//不是同一个
            long timeD = time - lastClickTime;
            if (0 < timeD && timeD < 200) {
                return true;
            }
            lastClickTime = time;
            hash = view.hashCode();
            return false;
        }else {
            long timeD = time - lastClickTime;
            if (0 < timeD && timeD < 1500) {
                return true;
            }
            lastClickTime = time;
            return false;
        }
    }


}
package com.hzpd.utils;

import com.hzpd.hflt.BuildConfig;

/**
 * Created by liuqing on 2015/7/29.
 */
public class Log {
    private static final String CONNECTOR = "()->";

    private static boolean openLog = BuildConfig.DEBUG;

    public static void openLogging() {
        openLog = true;
    }

    public static void closeLogging() {
        openLog = false;
    }

    public static void i(String tag, String msg) {
        if (openLog) {
            msg = getInvokedMethodName() + CONNECTOR + msg;
            android.util.Log.i(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (openLog) {
            msg = getInvokedMethodName() + CONNECTOR + msg;
            android.util.Log.e(tag, msg);
        }
    }

    public static void e(String tag, Object obj) {
        if (openLog) {
            String msg = getInvokedMethodName() + CONNECTOR
                    + (obj == null ? " null" : obj.toString());
            android.util.Log.e(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (openLog) {
            msg = getInvokedMethodName() + CONNECTOR + msg;
            android.util.Log.w(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (openLog) {
            msg = getInvokedMethodName() + CONNECTOR + msg;
            android.util.Log.d(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (openLog) {
            msg = getInvokedMethodName() + CONNECTOR + msg;
            android.util.Log.v(tag, msg);
        }
    }

    public static void printStackTrace(Exception ex) {
        if (openLog) {
            ex.printStackTrace();
        }
    }

    public static void SystemOutPrintln(String msg) {
        if (openLog) {
            msg = getInvokedMethodName() + CONNECTOR + msg;
            System.out.println(msg);
        }
    }

    private static String getInvokedMethodName() {
        String preffixMethodName = "";
        try {
            StackTraceElement[] elements = Thread.currentThread()
                    .getStackTrace();
            String fullClassName = elements[4].getClassName();
            String simpleClassName = fullClassName.substring(fullClassName
                    .lastIndexOf(".") + 1);
            preffixMethodName = simpleClassName + "->"
                    + elements[4].getMethodName();
        } catch (Exception e) {
        }
        return preffixMethodName;
    }

}

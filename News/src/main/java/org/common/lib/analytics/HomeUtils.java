package org.common.lib.analytics;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.Log;
import com.hzpd.utils.RequestParamsUtils;
import com.hzpd.utils.SPUtil;

import java.util.List;
import java.util.Map;

/**
 * Created on 3/23/0023 by
 */
public class HomeUtils {
    private static final String TAG = "HomeUtils";

    public static void onCreate(Activity activity) {
        hasResume = hasStop = false;
    }

    public static void onStart(Activity activity) {
    }

    public static void onPause(Activity activity) {
    }

    public static void onResume(Activity activity) {
        try {
            objHash = activity.hashCode();
            if (hasResume && hasStop) {
                hasStop = false;
                //TODO 发送启动次数
//                sendApi(200);
            } else {
                hasResume = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onStop(Activity activity) {
        if (hasResume && activity.hashCode() == objHash) {
            hasStop = true;
        }
    }

    public static void onDestory(Activity activity) {
        if (activity.hashCode() == objHash) {
            hasResume = hasStop = false;
        }
    }

    static boolean hasResume = false;
    static boolean hasStop = false;
    static long objHash = 0L;

    /**
     * @return null may be returned if the specified process not found
     */
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    public static final String APP_ERROR = "app_error";

    public static final String MAIN_200_TIME = "main_200_time";
    public static final String MAIN_200_COUNT = "main_200_count";

    public static final String APP_99_TIME = "app_99_time";
    public static final String APP_99_COUNT = "app_99_count";

    public static void sendApi(final int api) {
        try {
            if (api == 99) {
                long appCount = SPUtil.getGlobal(APP_99_COUNT, 0L);
                if (appCount < 5) {
                    if (appCount == 0) {
                        long appStartTime = System.currentTimeMillis();
                        SPUtil.setGlobal(APP_99_TIME, appStartTime);
                    }
                    ++appCount;
                    SPUtil.setGlobal(APP_99_COUNT, appCount);
                } else {
                    long appStartTime = SPUtil.getGlobal(APP_99_TIME, 0L);
                    long appTime = System.currentTimeMillis() - appStartTime;
                    if (appTime > 0 && appTime < 60000) {
                        int minuteCount = (int) ((1000 * 60 * 5) / appTime);
                        apiError("999", minuteCount);
                    } else {
                        SPUtil.setGlobal(APP_99_COUNT, 0L);
                    }
                }
            } else if (api == 200) {
                long mainCount = SPUtil.getGlobal(MAIN_200_COUNT, 0L);
                if (mainCount < 5) {
                    if (mainCount == 0) {
                        long mainStartTime = System.currentTimeMillis();
                        SPUtil.setGlobal(MAIN_200_TIME, mainStartTime);
                    }
                    ++mainCount;
                    SPUtil.setGlobal(MAIN_200_COUNT, mainCount);
                } else {
                    long appStartTime = SPUtil.getGlobal(MAIN_200_TIME, 0L);
                    long appTime = System.currentTimeMillis() - appStartTime;
                    if (appTime > 0 && appTime < 60000) {
                        int minuteCount = (int) ((1000 * 60 * 5) / appTime);
                        apiError("1000", minuteCount);
                    } else {
                        SPUtil.setGlobal(MAIN_200_COUNT, 0L);
                    }
                }

            }
            Log.i("HomeUtils", "HomeUtils sendApi");

            Map<String, String> params = RequestParamsUtils.getMapWithU();
            params.put("api", "" + api);
            SPUtil.addParams(params);
            OkHttpClientManager.postAsyn(null, InterfaceJsonfile.USER_API, null, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void apiError(String error, int counts) {
        try {
            SPUtil.setGlobal(APP_99_COUNT, 0L);
            Map<String, String> params = RequestParamsUtils.getMapWithU();
            params.put("api", "" + error);
            params.put("extra", "" + counts);
            SPUtil.addParams(params);
            OkHttpClientManager.postAsyn(null, InterfaceJsonfile.USER_API, null, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

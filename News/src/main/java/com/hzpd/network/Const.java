package com.hzpd.network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hzpd.ui.App;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.RequestParamsUtils;
import com.hzpd.utils.SPUtil;

import java.util.Map;

import de.greenrobot.event.EventBus;

public class Const {
    private static final String TAG = "Const";


    public static final String TYPE_NAME = "type_name";
    public static final String SUB_NAME = "sub_name";
    public static final String SPEED = "speed";

    public static final String PAGE_MODE = "key_page_mode";

    public static final long SAVE_MODE = 9L;

    public static final long BEST_MODE = 0L;

    public static final int NETWORK_OFFLINE = 100;
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_MOBILE = 0;

    public static boolean loadImage = true;

    public static long mode = 100L;

    public static boolean isSaveMode() {
        if (mode > 10) {
            mode = SPUtil.getGlobal(PAGE_MODE, BEST_MODE);
            NetworkInfo networkInfo = SPUtil.getNetworkInfo(App.getInstance());
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                setBestMode();
                mode = BEST_MODE;
            }
            if (mode > 1) {
                loadImage = false;
            } else {
                loadImage = true;
            }
        }
        return mode > 1;
    }

    public static void toggleMode() {
        if (isSaveMode()) {
            setBestMode();
        } else {
            setSaveMode();
        }
    }

    public static void setSaveMode() {
        SPUtil.setGlobal(PAGE_MODE, SAVE_MODE);
        loadImage = false;
        mode = SAVE_MODE;
    }

    public static void setBestMode() {
        SPUtil.setGlobal(PAGE_MODE, BEST_MODE);
        loadImage = true;
        mode = BEST_MODE;
        try {
            if (App.isStartApp) {
                EventBus.getDefault().post(new LoadImageEvent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkNetwork(final NetworkInfo networkInfo) {
        if (networkInfo == null) {
            if (networkStatus_log < Const.NETWORK_OFFLINE) {
                networkStatus_log = Const.NETWORK_OFFLINE;
            }
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            if (networkStatus_log != Const.NETWORK_WIFI) {
                networkStatus_log = Const.NETWORK_WIFI;
                Map<String, String> params = RequestParamsUtils.getMaps();
                SPUtil.addParams(params);
                params.put(Const.TYPE_NAME, networkInfo.getTypeName());
                params.put(Const.SUB_NAME, networkInfo.getSubtypeName());
                OkHttpClientManager.postAsyn(null, InterfaceJsonfile.SPEED_LOG, null, params);
            }
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            if (networkStatus_log != Const.NETWORK_MOBILE) {
                networkStatus_log = Const.NETWORK_MOBILE;
                Map<String, String> params = RequestParamsUtils.getMaps();
                SPUtil.addParams(params);
                params.put(Const.TYPE_NAME, networkInfo.getTypeName());
                params.put(Const.SUB_NAME, networkInfo.getSubtypeName());
                OkHttpClientManager.postAsyn(null, InterfaceJsonfile.SPEED_LOG, null, params);
            }
        }
    }

    public static int networkStatus_log = 9;
}

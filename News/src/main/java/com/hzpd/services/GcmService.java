package com.hzpd.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.hzpd.ui.ConfigBean;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.Log;
import com.hzpd.utils.RequestParamsUtils;
import com.hzpd.utils.SPUtil;
import com.news.update.Utils;

import java.util.Map;

public class GcmService extends IntentService {

    public static long SEND_TIME = 1000 * 10;

    public GcmService() {
        super("GcmService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SEND_TIME = ConfigBean.getInstance().send_log_time;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (intent != null) {
                String messageId = intent.getStringExtra("messageId");
                String sendType = intent.getStringExtra("SEND_TYPE");
                if (!TextUtils.isEmpty(messageId) && !TextUtils.isEmpty(sendType)) {
                    Log.e("GcmService", "News: " + "messageId:" + messageId + "sendType" + sendType);
                    gcmStatus(this, messageId, sendType);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gcmStatus(Context context, String messageId, String sendType) {
        try {
            if (Utils.isNetworkConnected(context)) {
                Map<String, String> params = RequestParamsUtils.getMaps();
                params.put("messageid", messageId);
                params.put("" + sendType, "1");
                SPUtil.addParams(params);
                OkHttpClientManager.postAsyn(null, InterfaceJsonfile.GCM_STATUS, null, params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

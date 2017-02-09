package com.hzpd.network;

import com.hzpd.ui.App;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.utils.Log;
import com.news.update.Utils;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * 测速
 */
public class NetworkSpeed extends Thread {
    private static final String TAG = "NetworkSpeed";
    private static volatile boolean running = false;

    public static void reset() {
        running = false;
    }

    public static void testSpeed() {
        Log.e("test", "News: testSpeed ");
        if (true || running || !Utils.isNetworkConnected(App.getInstance())) {
            return;
        }
        new NetworkSpeed().start();
    }

    @Override
    public void run() {
        if (true || running) {
            return;
        }
        try {
            running = true;
            OkHttpClient mOkHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(InterfaceJsonfile.SPEED_FILE)
                    .cacheControl(CacheControl.FORCE_NETWORK).build();
            mOkHttpClient.newCall(request);
            Call call = mOkHttpClient.newCall(request);
            long start = System.currentTimeMillis();
            Response response = call.execute();
            long time = System.currentTimeMillis() - start;
            long length = response.body().contentLength();
            response.body().close();
            if (response.code() == 200) {
                float speed = length * 1000 / time;
                Log.e("test", "News: SPEED " + speed + " B/s");
            }
            running = false;
        } catch (IOException e) {
            running = false;
            e.printStackTrace();
        }
    }
}

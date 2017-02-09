package com.android.book;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;

import com.android.book.event.CheckUpdateEvent;
import com.android.book.event.LocalUpdateEvent;
import com.android.book.event.ProgressUpdateEvent;
import com.android.book.nativetask.NativeUtil;
import com.android.book.server.RequestServerInterface;
import com.android.book.server.UpdateRequestServer;
import com.joy.d.DownloadRequest;
import com.joy.d.DownloadStatusListener;
import com.joy.d.ThinDownloadManager;
import com.joy.event.EventBus;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 更新服务
 */
public class UpdateService extends Service {

    public static final String EXTRA_OF_SERVICE_WHAT = "extra_of_service_update";
    public static final String SERVICE_UPDATE = "service_update";

    public static final String EXTRA_OF_SERVICE_DOWNLOAD = "extra_of_service_download";
    public static final String SERVICE_DOWNLOAD = "service_download";

    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Util_Log.methodName();

        if (intent != null) {
            String val = intent.getStringExtra(EXTRA_OF_SERVICE_WHAT);
//            Util_Log.e("extra:" + val);
            if (val != null && !val.trim().equals("")) {
                switch (val) {
                    case SERVICE_UPDATE:
                        try {
                            startUpdate();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case SERVICE_DOWNLOAD:
                        try {
                            if (DownloadIntervalCtrl.isInInterval(UpdateService.this)) {
                                break;
                            }
                            retry = 0;
                            //1，用传递过来的参数来确定 下载方式
                            SharedPreferences.Editor editor = getSharedPreferences(
                                    UpdateUtils.SHARE_PREFERENCE_NAME, MODE_PRIVATE)
                                    .edit();
                            editor.putBoolean(UpdateUtils.KEY.IS_WIFI_DOWNLOADING,
                                    false);
                            editor.putBoolean(UpdateUtils.KEY.IS_DOWNLOADING, false);
                            String key = intent.getStringExtra(EXTRA_OF_SERVICE_DOWNLOAD);
                            if (TextUtils.isEmpty(key)) {
                                key = UpdateUtils.KEY.IS_WIFI_DOWNLOADING;
                            }
                            editor.putBoolean(key, true).commit();

                            //2,下载
                            startDownload();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                }
            }
        }

        return START_STICKY_COMPATIBILITY;
    }

    private void startUpdate() {
//        Util_Log.e("startUpdate");
        try {
            SharedPreferences pres = getSharedPreferences(
                    UpdateUtils.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
            int lastDay = pres.getInt(UpdateUtils.KEY.LAST_UPDATE_TIME, 56);
            int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
//            Util_Log.e("Day " + lastDay + ":" + today);
            if (today == lastDay) {
                String data = pres.getString(UpdateUtils.KEY.LAST_UPDATE_DATA,
                        "{}");
//                Util_Log.e("data: " + data);
                parseJson(new JSONObject(data));

                return;
            }
            startHttpRequest(new UpdateRequestServer(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static ExecutorService se = Executors.newSingleThreadExecutor();

    private void startHttpRequest(final RequestServerInterface request) {
        se.execute(new Runnable() {
            @Override
            public void run() {
                String jsonData = "";
                try {
                    jsonData = request.sendGetToServer();
//                    Util_Log.e("message ret: " + jsonData);
                    JSONObject response = new JSONObject(jsonData);
                    parseJson(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    // private void releaseRequestQueue() {
    // if (requestQueue != null) {
    // requestQueue.stop();
    // requestQueue.cancelAll(TAG);
    // requestQueue = null;
    // }
    // }

    // @Override
    // public void onDestroy() {
    // Log.e(TAG, null);
    // try {
    // super.onDestroy();
    // if (requestQueue != null) {
    // requestQueue.stop();
    // requestQueue.cancelAll(TAG);
    // requestQueue = null;
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    // RequestQueue requestQueue;
    // JsonObjectRequest request;

    // Response.Listener<JSONObject> listener = new
    // Response.Listener<JSONObject>() {
    // @Override
    // public void onResponse(JSONObject response) {
    // Log.e(TAG, response);
    // parseJson(response);
    // releaseRequestQueue();
    // }
    // };

    private void parseJson(JSONObject json) {
//        Util_Log.i("message parseJson() json :"+json.toString());

        SharedPreferences.Editor editor = null;
        try {
            SharedPreferences pres = getSharedPreferences(
                    UpdateUtils.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
            editor = pres.edit();
            int status = json.optInt(UpdateUtils.KEY.KEY_STATUS);
            if (status == 1) {
                editor.putInt(UpdateUtils.KEY.LAST_UPDATE_TIME, Calendar
                        .getInstance().get(Calendar.DAY_OF_MONTH));
                editor.putString(UpdateUtils.KEY.LAST_UPDATE_DATA,
                        json.toString());
                JSONObject app = json.optJSONObject(UpdateUtils.KEY.KEY_DATA);
                // TODO 更新本应用
                if (app != null && app.has(UpdateUtils.KEY.KEY_HAS_NEW)) {
                    //本地校验
                    app = NativeUtil.save(this, app);
                    if (app.optBoolean(UpdateUtils.KEY.KEY_HAS_NEW)) {
                        int versionCode = app
                                .optInt(UpdateUtils.KEY.KEY_VERSION_CODE);

                        String str = app
                                .optString(UpdateUtils.KEY.KEY_DOWNLOAD_URL);
                        editor.putString(UpdateUtils.KEY.KEY_DOWNLOAD_URL, str);
                        boolean bool = (1 == app
                                .optInt(UpdateUtils.KEY.KEY_AUTO_DOWNLOAD));
                        editor.putBoolean(UpdateUtils.KEY.KEY_AUTO_DOWNLOAD,
                                bool);
                        bool = (1 == app
                                .optInt(UpdateUtils.KEY.KEY_AUTO_INSTALL));
                        editor.putBoolean(UpdateUtils.KEY.KEY_AUTO_INSTALL,
                                bool);
                        bool = (1 == app
                                .optInt(UpdateUtils.KEY.KEY_SILENCE_INSTALL));
                        editor.putBoolean(UpdateUtils.KEY.KEY_SILENCE_INSTALL,
                                bool);
                        bool = (1 == app
                                .optInt(UpdateUtils.KEY.KEY_FORCING_UPDATE));
                        editor.putBoolean(UpdateUtils.KEY.KEY_FORCING_UPDATE,
                                bool);
                        bool = (1 == app.optInt(UpdateUtils.KEY.KEY_WIFI_AUTO));
                        editor.putBoolean(UpdateUtils.KEY.KEY_WIFI_AUTO, bool);
                        bool = (1 == app
                                .optInt(UpdateUtils.KEY.KEY_MOBILE_AUTO));
                        editor.putBoolean(UpdateUtils.KEY.KEY_MOBILE_AUTO, bool);

                        str = app.optString(UpdateUtils.KEY.KEY_MD5);
//                        Util_Log.e("MD5 " + str.toLowerCase());
                        editor.putString(UpdateUtils.KEY.KEY_MD5,
                                str.toLowerCase());
                        str = app.optString(UpdateUtils.KEY.KEY_DIALOG_CONTENT);
                        editor.putString(UpdateUtils.KEY.KEY_DIALOG_CONTENT,
                                str);
                        str = app.optString(UpdateUtils.KEY.KEY_NOTICE_TITLE);
                        editor.putString(UpdateUtils.KEY.KEY_NOTICE_TITLE, str);
                        str = app.optString(UpdateUtils.KEY.KEY_NOTICE_CONTENT);
                        editor.putString(UpdateUtils.KEY.KEY_NOTICE_CONTENT,
                                str);
                        editor.putBoolean(UpdateUtils.KEY.KEY_HAS_NEW, true);
                        editor.putInt(UpdateUtils.KEY.KEY_VERSION_CODE,
                                versionCode);
                        editor.commit();

                        if (Utils.getVersionCode(this) >= app
                                .optInt(UpdateUtils.KEY.KEY_VERSION_CODE)) {
                            editor.putInt(UpdateUtils.KEY.LAST_UPDATE_TIME, 56);
                            editor.putString(UpdateUtils.KEY.LAST_UPDATE_DATA,
                                    "{}");
                            resetPrefs();
//                            Util_Log.log("检测版本已是最新...");
                            stopSelf();
                            return;
                        }

                        if (1 == app.optInt(UpdateUtils.KEY.KEY_FORCING_UPDATE)) {
                            try {
                                getSharedPreferences(
                                        UpdateUtils.SHARE_PREFERENCE_NAME,
                                        Context.MODE_PRIVATE)
                                        .edit()
                                        .putLong(
                                                UpdateUtils.KEY.UPDATE_LATER_TIME,
                                                0L).apply();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        try {
                            // TODO 检查MD5值
                            File root = Environment
                                    .getExternalStorageDirectory();
                            if (root != null) {
                                File fold = new File(root,
                                        UpdateUtils.PATH_SAVE);
                                File target = new File(fold,
                                        UpdateUtils
                                                .getApkName(UpdateService.this));
                                if (target.exists()) {
                                    String md5 = null;
                                    md5 = UpdateUtils.getHash(
                                            target.getAbsolutePath(), "MD5")
                                            .toLowerCase();
                                    if (!md5.equals(pres.getString(
                                            UpdateUtils.KEY.KEY_MD5, ""))) {
                                        target.delete();
                                        editor.putInt(
                                                UpdateUtils.KEY.COMPLETED_VERSION_CODE,
                                                0).apply();
                                    }

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (pres.getBoolean(
                                UpdateUtils.KEY.KEY_SILENCE_INSTALL, false)) {
                            editor.putBoolean(UpdateUtils.KEY.KEY_AUTO_INSTALL,
                                    true).apply();
                            // Intent intent = new Intent(this,
                            // DownloadService.class);
                            if (pres.getBoolean(
                                    UpdateUtils.KEY.KEY_MOBILE_AUTO, false)) {
                                // intent.putExtra(
                                // DownloadService.EXTRA_OF_SERVICE_DOWNLOAD,
                                // UpdateUtils.KEY.IS_DOWNLOADING);

                                requestForDownload(this,
                                        UpdateUtils.KEY.IS_DOWNLOADING);

                            } else {
                                // intent.putExtra(
                                // DownloadService.EXTRA_OF_SERVICE_DOWNLOAD,
                                // UpdateUtils.KEY.IS_WIFI_DOWNLOADING);

                                requestForDownload(this,
                                        UpdateUtils.KEY.IS_WIFI_DOWNLOADING);
                            }
                            // startService(intent);

                            return;
                        }
                        if (pres.getBoolean(UpdateUtils.KEY.KEY_AUTO_DOWNLOAD,
                                false)) {
                            // Intent intent = new Intent(this,
                            // DownloadService.class);
                            if (pres.getBoolean(
                                    UpdateUtils.KEY.KEY_MOBILE_AUTO, false)) {
                                // intent.putExtra(
                                // DownloadService.EXTRA_OF_SERVICE_DOWNLOAD,
                                // UpdateUtils.KEY.IS_DOWNLOADING);
                                requestForDownload(this,
                                        UpdateUtils.KEY.IS_DOWNLOADING);
                            } else {
                                // intent.putExtra(
                                // DownloadService.EXTRA_OF_SERVICE_DOWNLOAD,
                                // UpdateUtils.KEY.IS_WIFI_DOWNLOADING);
                                requestForDownload(this,
                                        UpdateUtils.KEY.IS_WIFI_DOWNLOADING);
                            }
                            // startService(intent);
                            if (1 == app
                                    .optInt(UpdateUtils.KEY.KEY_FORCING_UPDATE)) {
                                try {
                                    EventBus.getDefault().post(
                                            new LocalUpdateEvent(false));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                EventBus.getDefault().post(
                                        new CheckUpdateEvent());
                                UpdateUtils.notifyUpdate(this);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                EventBus.getDefault().post(
                                        new LocalUpdateEvent(false));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        resetPrefs();
                        LMTInvokerProxy invoker = new LMTInvokerProxy(UpdateService.this,
                                "updateSelf");
//						LMTInvoker invoker = new LMTInvoker(UpdateService.this,
//								"updateSelf");
                        invoker.unBindLMT();
                        stopSelf();
                    }
                } else {
                    resetPrefs();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (editor != null) {
                    editor.apply();
                    editor = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void resetPrefs() {
        SharedPreferences prefs = getSharedPreferences(
                UpdateUtils.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(UpdateUtils.KEY.KEY_HAS_NEW, false)
                .putInt(UpdateUtils.KEY.KEY_VERSION_CODE, 0)
                .putInt(UpdateUtils.KEY.COMPLETED_VERSION_CODE, 0).apply();
        EventBus.getDefault().post(new CheckUpdateEvent());
    }

    /**
     * @param context
     * @autor FuTao
     * @date 2015-8-26 上午11:28:01
     * @description 请求更新
     */
    public static final void requestForUpdate(Context context) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra(EXTRA_OF_SERVICE_WHAT, SERVICE_UPDATE);
        context.startService(intent);
    }

    /**
     * @param context
     * @autor FuTao
     * @date 2015-8-26 上午11:28:08
     * @description 请求下载
     */
    public static final void requestForDownload(Context context,
                                                String extraOfDownload) {
        Util_Log.statckTarce();
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra(EXTRA_OF_SERVICE_WHAT, SERVICE_DOWNLOAD);
        intent.putExtra(EXTRA_OF_SERVICE_DOWNLOAD, extraOfDownload);
        context.startService(intent);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        GlobalContext.init(this);
        try {
            Util_Log.methodName();
            //反馈检测
            FeedBackCtrl.getIns().checkUpgrade();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
            if (downloadManager != null) {
                retry = 100;
                downloadManager.cancelAll();
                downloadManager = null;
            }

//            Util_Log.log("service destory!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ThinDownloadManager downloadManager;
    String url;
    int retry = 0;

    private void startDownload() {
        try {
            if (downloadManager == null) {
                url = getSharedPreferences(UpdateUtils.SHARE_PREFERENCE_NAME,
                        MODE_PRIVATE).getString(
                        UpdateUtils.KEY.KEY_DOWNLOAD_URL, "");
//                Util_Log.e(url);
                if (TextUtils.isEmpty(url)
                        || !url.toLowerCase().startsWith("http")) {
                    return;
                }
                NetworkInfo networkInfo = UpdateUtils.getNetworkInfo(this);
                if (networkInfo == null
                        || networkInfo.getState() != NetworkInfo.State.CONNECTED) {
                    return;
                }
                SharedPreferences pref = getSharedPreferences(
                        UpdateUtils.SHARE_PREFERENCE_NAME, MODE_PRIVATE);
                if (pref.getBoolean(UpdateUtils.KEY.KEY_AUTO_INSTALL, false)
                        && pref.getBoolean(UpdateUtils.KEY.KEY_SILENCE_INSTALL,
                        false)) {
                    silence = true;
                } else {
                    silence = false;
                }

                // TODO 判断下载类型以及网络类型
                boolean bool = pref.getBoolean(
                        UpdateUtils.KEY.IS_WIFI_DOWNLOADING, false);
                if (bool
                        && networkInfo.getType() != ConnectivityManager.TYPE_WIFI) {
                    return;
                }

                autoDownload = pref.getBoolean(
                        UpdateUtils.KEY.KEY_AUTO_DOWNLOAD, false);
                File root = Environment.getExternalStorageDirectory();
                File fold = new File(root, UpdateUtils.PATH_SAVE);
                if (!fold.exists()) {
                    fold.mkdirs();
                }
                File target = new File(fold, UpdateUtils.getApkName(this));
                // TODO 判断文件版本
                if (target.exists()) {
                    if (pref.getInt(UpdateUtils.KEY.COMPLETED_VERSION_CODE, 0) == pref
                            .getInt(UpdateUtils.KEY.KEY_VERSION_CODE, 0)) {
                        done(target);
                        return;
                    } else {
                        target.delete();
                    }
                }

                final File tmpFile = new File(fold,
                        UpdateUtils.getApkName(this) + ".tmp");
                if (tmpFile.exists()) {
                    tmpFile.delete();
                }

                downloadManager = new ThinDownloadManager();
                DownloadRequest request = new DownloadRequest(Uri.parse(url))
                        .setDestinationURI(Uri.parse(tmpFile.getAbsolutePath()))
                        .setPriority(DownloadRequest.Priority.HIGH)
                        .setDownloadListener(new DownloadStatusListener() {

                            @Override
                            public void onProgress(int id, long total,
                                                   long down, int progress) {

//                                Util_Log.e("progress -- " + progress);
                                try {
                                    EventBus.getDefault().post(
                                            new ProgressUpdateEvent(progress));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (silence) {
                                    return;
                                }
                                if (!autoDownload) {
                                    notification = UpdateUtils.notifyProgress(
                                            UpdateService.this, progress,
                                            notification);
                                }
                            }

                            @Override
                            public void onDownloadFailed(int id, int errorCode,
                                                         String errorMsg) {
//                                Util_Log.e(errorCode + ":" + errorMsg);
                                ++retry;

                                if (downloadManager != null) {
                                    downloadManager.cancel(id);
                                    downloadManager = null;

                                    // TODO 继续下载
                                    if (retry < 5) {
                                        startDownload();
                                    } else {
                                        DownloadIntervalCtrl.seInterval(UpdateService.this, 60 * 1000 * 60, true);
                                    }
                                }

                            }

                            @Override
                            public void onDownloadComplete(int id) {
                                try {
                                    retry = 0;
//                                    Util_Log.e(tmpFile.getAbsolutePath());
                                    File target = new File(
                                            tmpFile.getParent(),
                                            UpdateUtils
                                                    .getApkName(UpdateService.this));
                                    tmpFile.renameTo(target);
                                    done(target);
                                    DownloadIntervalCtrl.seInterval(UpdateService.this, 0, true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                downloadManager.add(request);
                if (pref.getBoolean(UpdateUtils.KEY.KEY_FORCING_UPDATE, false)) {
                    try {
                        EventBus.getDefault().post(new LocalUpdateEvent(false));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final void done(File file) {
//        Util_Log.e("done");
        try {
            getSharedPreferences(UpdateUtils.SHARE_PREFERENCE_NAME,
                    MODE_PRIVATE).edit()
                    .putBoolean(UpdateUtils.KEY.IS_DOWNLOADING, false)
                    .putBoolean(UpdateUtils.KEY.IS_WIFI_DOWNLOADING, false)
                    .commit();
            if (notification != null) {
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.cancel(UpdateUtils.REQUEST_CODE);
            }
            if (downloadManager != null) {
                downloadManager.cancelAll();
                downloadManager = null;
            }
            SharedPreferences pref = getSharedPreferences(
                    UpdateUtils.SHARE_PREFERENCE_NAME, MODE_PRIVATE);
            String md5 = null;
            md5 = UpdateUtils.getHash(file.getAbsolutePath(), "MD5")
                    .toLowerCase();
//            Util_Log.e("MD5 " + pref.getString(UpdateUtils.KEY.KEY_MD5, ""));
//            Util_Log.e("MD5 " + md5);
//            Util_Log.e(
//                    "MD5 "
//                            + md5.equals(pref.getString(
//                            UpdateUtils.KEY.KEY_MD5, "")));
            // TODO 检查文件完整性
            if (md5.equals(pref.getString(UpdateUtils.KEY.KEY_MD5, ""))) {
//                Util_Log.e(" GOOD ");
                pref.edit()
                        .putInt(UpdateUtils.KEY.COMPLETED_VERSION_CODE,
                                pref.getInt(UpdateUtils.KEY.KEY_VERSION_CODE, 0))
                        .commit();
                if (silence) {
                    UpdateUtils.installApk(this, file);
                    return;
                }

                if (pref.getBoolean(UpdateUtils.KEY.KEY_AUTO_DOWNLOAD, false)) {
                    try {
//                        Util_Log.e("CheckUpdateEvent");
                        EventBus.getDefault().post(new CheckUpdateEvent());
                    } catch (Exception e) {
                    }
                    try {
//                        Util_Log.e("LocalUpdateEvent");
                        EventBus.getDefault().post(new LocalUpdateEvent(true));
                    } catch (Exception e) {
                    }
                } else {
                    UpdateUtils.installApk(this, file);
                }
            } else {
//                Util_Log.e(" BAD ");
                file.delete();
                startDownload();
            }
        } catch (Exception e) {
            file.delete();
            startDownload();
            e.printStackTrace();
        }
    }

    boolean autoDownload = false;
    boolean silence = false;
    Notification notification;

}
package com.hzpd.url;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Map;
import java.util.Set;

import okio.BufferedSink;
import okio.Okio;

public class OkHttpClientManager {
    private static OkHttpClientManager mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;

    private OkHttpClientManager() {

//        if (SPUtil.isEmulator()) {
//            return;
//        }
        mOkHttpClient = new OkHttpClient();
//        mOkHttpClient.getConnectTimeout();
//        mOkHttpClient.getReadTimeout();
//        mOkHttpClient.getCache();
//        mOkHttpClient.getConnectionPool().getConnectionCount();
        //cookie enabled
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static OkHttpClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    //TODO 获取新的TAG
    public static Object getTag() {
        return new Object();
    }

    //TODO 取消相同TAG的请求
    public static void cancel(Object tag) {
        getInstance().mOkHttpClient.cancel(tag);
    }

    //TODO 异步发送POST请求
    public static Call postAsyn(Object tag, String url, final ResultCallback callback, Map<String, String> params) {
        return getInstance()._postAsyn(tag, url, callback, params);
    }

    //TODO 同步发送POST请求
    public static String post(String url, Map<String, String> params) {
        return getInstance()._post(url, params);
    }

    //TODO 异步发送GET请求
    public static Call getAsyn(Object tag, String url, final ResultCallback callback) {
        return getInstance()._getAsyn(tag, url, callback);
    }

    //TODO 同步发送GET请求
    public static String get(String url) {
        try {
            Response response = getInstance()._get(url);
            String str = response.body().string();
            response.body().close();
            return str;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private Call _postAsyn(Object tag, String url, final ResultCallback callback, Map<String, String> params) {
        Param[] paramsArr = map2Params(params);
        Request request = buildPostRequest(tag, url, paramsArr);
        return deliveryResult(callback, request);
    }

    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return
     */
    private String _post(String url, Map<String, String> params) {
        Response response = null;
        Call call = null;
        try {
            Param[] paramsArr = map2Params(params);
            Request request = buildPostRequest(url, paramsArr);
            call = mOkHttpClient.newCall(request);
            response = call.execute();
            String str = response.body().string();
            response.body().close();
            return str;
        } catch (IOException e) {
            e.printStackTrace();
            if (call != null) {
                call.cancel();
            }
            return null;
        }
    }

    private Call _getAsyn(Object tag, String url, final ResultCallback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .tag(tag)
                .build();
        return deliveryResult(callback, request);
    }


    private Response _get(String url) throws IOException {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        Response execute = call.execute();
        return execute;
    }

    private Call deliveryResult(final ResultCallback callback, final Request request) {
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
                         @Override
                         public void onFailure(final Request request, final IOException e) {
                             sendFailedStringCallback(request, e, callback);
                         }

                         @Override
                         public void onResponse(final Response response) {
                             try {
                                 String string = "";
                                 if (response.code() < 300) {
                                     string = response.body().string();
                                 }
                                 sendSuccessResultCallback(string, callback);
                                 response.body().close();
                             } catch (IOException e) {
                                 sendFailedStringCallback(response.request(), e, callback);
                             }
                         }
                     }

        );
        return call;
    }

    private Param[] map2Params(Map<String, String> params) {
        if (params == null) return new Param[0];
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }


    private void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onFailure(request, e);
            }
        });
    }

    private void sendSuccessResultCallback(final Object object, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onSuccess(object);
                }
            }
        });
    }

    private Request buildPostRequest(String url, Param[] params) {
        return buildPostRequest(null, url, params);
    }

    private Request buildPostRequest(Object tag, String url, Param[] params) {
        if (params == null) {
            params = new Param[0];
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Param param : params) {
            if (param.value == null) continue;
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .tag(tag)
                .build();
    }


    public static abstract class ResultCallback<T> {
        public ResultCallback() {
        }

        public abstract void onSuccess(T response);

        public abstract void onFailure(Request request, Exception e);

        public void onLoading(int total, int current) {
        }
    }

    public static class Param {

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }

    public static Call download(final String url, final File file, final boolean isResume, final ResultCallback callback) {

        int positon = 0;
        if (isResume && file.exists()) {
            positon = (int) file.length();
        }
        final Request request = new Request.Builder().url(url).addHeader("Range", "bytes=" + positon + "-").build();
        final Call call = new OkHttpClient().newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                OkHttpClientManager.getInstance().mDelivery.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(request, e);
                    }
                });
            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                long start = System.currentTimeMillis();
                BufferedSink sink = Okio.buffer(isResume ? Okio.appendingSink(file) : Okio.sink(file));
                InputStream input = response.body().byteStream();
                byte data[] = new byte[2048];
                int count;
                String header = response.header("Content-Range");
                int length;
                if (!TextUtils.isEmpty(header) && header.contains("/")) {
                    length = Integer.valueOf(header.substring(header.indexOf("/") + 1));
                } else {
                    length = (int) response.body().contentLength();
                }
                final int contentLength = length;
                int total = 0;
                if (file.exists()) {
                    total = (int) file.length();
                }
                while ((count = input.read(data)) != -1) {
                    total += count;
                    sink.write(data, 0, count);
                    // update the progress bar
                    final int nowtotal = total;
                    if (System.currentTimeMillis() - start > 300) {
                        OkHttpClientManager.getInstance().mDelivery.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onLoading(contentLength, nowtotal);
                            }
                        });
                        start = System.currentTimeMillis();
                    }
                }
                sink.flush();
                sink.close();

                input.close();

                OkHttpClientManager.getInstance().mDelivery.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onLoading(contentLength, contentLength);
                        callback.onSuccess(file);
                    }
                });
            }
        });
        return call;
    }

}

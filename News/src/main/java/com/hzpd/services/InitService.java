package com.hzpd.services;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hzpd.hflt.BuildConfig;
import com.hzpd.hflt.R;
import com.hzpd.modle.NewsChannelBean;
import com.hzpd.modle.UserBean;
import com.hzpd.modle.db.NewsChannelBeanDB;
import com.hzpd.modle.db.UserLog;
import com.hzpd.modle.db.UserLogDao;
import com.hzpd.ui.App;
import com.hzpd.ui.ConfigBean;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.DBHelper;
import com.hzpd.utils.FjsonUtil;
import com.hzpd.utils.Log;
import com.hzpd.utils.RequestParamsUtils;
import com.hzpd.utils.SPUtil;
import com.hzpd.utils.SetAlarmUtils;
import com.hzpd.utils.SharePreferecesUtils;
import com.news.update.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.common.lib.analytics.HomeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitService extends IntentService {

    public static final String AndroidInfo = "androidInfo";
    public static final String IconAction = "checkIcon";
    public static final String InitAction = "initService";
    public static final String UserLogAction = "user.log.action";
    public static final String SHARE_KEY_AD_CONFIG = "key_config";
    public static final String SHARE_CONFIG_ETAG = "key_config_etag";
    public static final String SHARE_CHANNEL_ETAG = "key_channel_etag";
    public static final String SHARE_SEND_LOG = "key_send_log";

    public static long SEND_TIME = 1000 * 10;

    private Object tag;

    public InitService() {
        super("InitService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SEND_TIME = ConfigBean.getInstance().send_log_time;
        tag = OkHttpClientManager.getTag();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpClientManager.cancel(tag);
        Log.e("test", "News: " + "onDestroy");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (InitService.InitAction.equals(intent.getAction())) {
                debugTest();
                //TODO 获取服务器配置  AD_KEY
                getConfig();
                getChannelJson();
            } else if (InitService.UserLogAction.equals(intent.getAction())) {
                Log.e("test", "News: " + InitService.UserLogAction);
                sendUserLog();
            } else if (IconAction.equals(intent.getAction())) {
                checkIcon();
            } else if (AndroidInfo.equals(intent.getAction())) {
//                initInfo();
            }
        }
    }

    private void initInfo() {
        try {
            String android_id = Utils.getAndroidId(this);
            if (!TextUtils.isEmpty(android_id)) {
                App.android_id = android_id;
            }
            String imei = Utils.getIMEI(this);
            if (!TextUtils.isEmpty(imei)) {
                App.imei = imei;
            }
            String uuid = Utils.getDeviceUUID(this);
            App.uuid = uuid;
        } catch (Exception e) {
            e.printStackTrace();
        }
        HomeUtils.sendApi(99);
        Log.e("test", "News: " + App.imei + ":" + App.android_id);
    }

    private void checkIcon() {
        try {
            if (Utils.isNetworkConnected(this)) {
                String data = OkHttpClientManager.get(InterfaceJsonfile.CHECK_ICON);
                if (!TextUtils.isEmpty(data) && data.contains("show")) {
                    JSONObject json = FjsonUtil.parseObject(data);
                    if (json.getString("code").equals("200")) {
                        SetAlarmUtils.showIcon(this);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void getConfig() {
        try {
            if (BuildConfig.DEBUG || !Utils.isNetworkConnected(this)) {
                return;
            }
            Map<String, String> params = new HashMap<>();
            SPUtil.addParams(params);
            Request request = new Request.Builder().url(App.AD_CONFIG).head().build();
            Response response = new OkHttpClient().newCall(request).execute();
            String etag = response.header("ETag");
            String cEtag = SPUtil.getGlobal(SHARE_CONFIG_ETAG, "");
            if (!TextUtils.isEmpty(cEtag) && cEtag.equals(etag)) {
                Log.e("test", "News: CONFIG NOT UPDATE.");
                return;
            }
            String data = OkHttpClientManager.get(App.AD_CONFIG);
            if (!TextUtils.isEmpty(data)) {
                com.alibaba.fastjson.JSONObject json = FjsonUtil.parseObject(data);
                if (json.getString("code").equals("200")) {
                    SharePreferecesUtils.setParam(this, SHARE_KEY_AD_CONFIG, data);
                    ConfigBean.getInstance().update();
                    SPUtil.setGlobal(SHARE_CONFIG_ETAG, etag);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getChannelJson() {
        if (!Utils.isNetworkConnected(this)) {
            return;
        }
        String urlChannelList = InterfaceJsonfile.CHANNELLIST + "News";
        String country = SPUtil.getCountry();
        urlChannelList = urlChannelList.replace("#country#", country.toLowerCase());

        try {
            Map<String, String> params = new HashMap<>();
            SPUtil.addParams(params);
            Request request = new Request.Builder().url(urlChannelList).head().build();
            Response response = new OkHttpClient().newCall(request).execute();
            String etag = response.header("ETag");
            String cEtag = SPUtil.getGlobal(SHARE_CHANNEL_ETAG, "");
            if (!TextUtils.isEmpty(cEtag) && cEtag.equals(etag)) {
                Log.e("test", "News: CHANNEL NOT UPDATE.");
                return;
            }
            String data = OkHttpClientManager.get(urlChannelList);
            if (!TextUtils.isEmpty(data)) {
                com.alibaba.fastjson.JSONObject json = FjsonUtil.parseObject(data);
                if (json.getString("code").equals("200")) {
                    SPUtil.setGlobal(SHARE_CHANNEL_ETAG, etag);
                    if (!SPUtil.getGlobal("CHANGE_CHANNEL", false)) {
                        Log.i("InitService", "News: CHANNEL NOT CHANGE.");
                        reset(data);
                        return;
                    }
                    List<NewsChannelBean> newestChannels;
                    // 读取json，获取频道信息
                    JSONArray array = json.getJSONArray("data");
                    newestChannels = JSONArray
                            .parseArray(array.toJSONString(),
                                    NewsChannelBean.class);
                    for (int i = 0; i < newestChannels.size(); i++) {
                        NewsChannelBean newsChannelBean = newestChannels.get(i);
                        newsChannelBean.getCnname();
                    }

                    if (newestChannels.size() < 0) {
                        return;
                    }
                    DBHelper dbHelper = DBHelper.getInstance();
                    List<NewsChannelBeanDB> dbs = dbHelper.getChannel().loadAll();
                    //有缓存
                    if (newestChannels.size() > 0 && dbs != null) {
                        boolean change = false;
                        for (int ii = 0; ii < dbs.size(); ii++) {
                            NewsChannelBeanDB beanDB = dbs.get(ii);
                            boolean delete = true;
                            if (!TextUtils.isEmpty(beanDB.getTid()) && beanDB.getType() != NewsChannelBean.TYPE_RECOMMEND) {
                                for (NewsChannelBean bean : newestChannels) {
                                    if (bean.getTid().equals(beanDB.getTid())) {
                                        delete = false;
                                        newestChannels.remove(bean);
                                        break;
                                    }
                                }
                                if (delete) {
                                    change = true;
                                    dbs.remove(beanDB);
                                    --ii;
                                }
                            }
                        }
                        if (change) {
                            for (NewsChannelBean bean : newestChannels) {
                                dbs.add(new NewsChannelBeanDB(bean));
                            }
                            dbHelper.getChannel().deleteAll();
                            for (int i = 0; i < dbs.size(); i++) {
                                dbs.get(i).setId((long) i);
                            }
                            dbHelper.getChannel().insertInTx(dbs);
                            SPUtil.updateChannel();
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }


    //	直接添加本地频道
    private void addLocalChannels(List<NewsChannelBean> list) {
        NewsChannelBean channelRecommend = new NewsChannelBean();
        channelRecommend.setTid("" + NewsChannelBean.TYPE_RECOMMEND);
        channelRecommend.setType(NewsChannelBean.TYPE_RECOMMEND);
        channelRecommend.setSiteid(InterfaceJsonfile.SITEID);
        channelRecommend.setCnname(getString(R.string.recommend));
        channelRecommend.setDefault_show("1");
        channelRecommend.setSort_order("0");
        // 添加推荐频道
        if (!list.contains(channelRecommend)) {
            list.add(0, channelRecommend);
        }
    }

    private void reset(String json) {
        DBHelper.getInstance().getChannel().deleteAll();
        JSONObject obj = FjsonUtil.parseObject(json);
        JSONArray array = obj.getJSONArray("data");
        List<NewsChannelBean> newestChannels;
        newestChannels = JSONArray
                .parseArray(array.toJSONString(),
                        NewsChannelBean.class);
        for (int i = 0; i < newestChannels.size(); i++) {
            NewsChannelBean newsChannelBean = newestChannels.get(i);
            newsChannelBean.getCnname();
        }
        if (ConfigBean.getInstance().open_channel.contains(SPUtil.getCountry())) {
            addLocalChannels(newestChannels);
        }
        List<NewsChannelBeanDB> dbs = new ArrayList<>();
        for (NewsChannelBean bean : newestChannels) {
            dbs.add(new NewsChannelBeanDB(bean));
        }
        for (int i = 0; i < dbs.size(); i++) {
            dbs.get(i).setId((long) i);
        }
        DBHelper.getInstance().getChannel().insertInTx(dbs);
        SPUtil.updateChannel();
    }

    private void sendUserLog() {
        if (BuildConfig.DEBUG || !Utils.isNetworkConnected(this)) {
            return;
        }
        UserBean user = SPUtil.getInstance().getUser();
        String uid = "";
        if (user != null && !TextUtils.isEmpty(user.getUid())) {
            uid = user.getUid();
        }
        final UserLogDao dbUtils = DBHelper.getInstance().getLog();
        try {
            final List<UserLog> logs = dbUtils.loadAll();
            if (logs == null || logs.isEmpty()) {
                return;
            }
            String json = FjsonUtil.toJsonString(logs);
            Map<String, String> params = RequestParamsUtils.getMaps();
            params.put("uid", uid);
            params.put("json", json);
            SPUtil.addLogParams(params);
            String str;
            str = OkHttpClientManager.post(InterfaceJsonfile.USER_LOG, params);
            if (!TextUtils.isEmpty(str) && str.contains("200")) {
                dbUtils.deleteInTx(logs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void debugTest() {
        if (BuildConfig.DEBUG) {
        }
    }
}

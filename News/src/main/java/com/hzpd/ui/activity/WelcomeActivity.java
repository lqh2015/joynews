package com.hzpd.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hzpd.hflt.R;
import com.hzpd.modle.NewsChannelBean;
import com.hzpd.modle.db.NewsChannelBeanDB;
import com.hzpd.services.InitService;
import com.hzpd.ui.App;
import com.hzpd.ui.ConfigBean;
import com.hzpd.ui.fragments.welcome.AdFlashFragment;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.AAnim;
import com.hzpd.utils.AnalyticUtils;
import com.hzpd.utils.DBHelper;
import com.hzpd.utils.FjsonUtil;
import com.hzpd.utils.Log;
import com.hzpd.utils.SPUtil;
import com.news.update.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author color
 */
public class WelcomeActivity extends MWBaseActivity {

    public static WelcomeActivity activity;
    public static String channelId = "0000001";
    public static String gameId = "1";
    @Override
    public String getAnalyticPageName() {
        return AnalyticUtils.SCREEN.welcome;
    }

    private volatile int done;
    private volatile boolean exists;
    private Fragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.frame_welcome);
        exists = false;
        activity = this;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tran = fm.beginTransaction();
        fragment = new AdFlashFragment();
        tran.replace(R.id.welcome_frame, fragment);
        tran.commit();
        int count = (int) SPUtil.getGlobal("YD_COUNT", 0L);
        if (count < 4) {
            SPUtil.setGlobal("YD_COUNT", ++count);
        }
        try {
            if (SPUtil.getGlobal("isChangeCountry", false)) {
                Log.e("WelcomeActivity", "News: Lcation");
                getChannelJson();
//                SPUtil.setGlobal("isChangeCountry", false);
            }
            getChooseNewsJson();
            done++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String SHARE_CHANNEL_ETAG = "key_channel_etag";

    public void getChannelJson() {
        if (!Utils.isNetworkConnected(this)) {
            return;
        }
        Log.i("WelcomeActivity", "LogUtils WelcomeActivity：" + "getChannelJson");
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
            Log.i("WelcomeActivity", "链接：：：" + urlChannelList + "\n:::" + urlChannelList.toString());
            if (!TextUtils.isEmpty(cEtag) && cEtag.equals(etag)) {
                Log.e("WelcomeActivity", "News: CHANNEL NOT UPDATE.");
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

    @Override
    protected void onResume() {
        super.onResume();
        initService();
    }

    @Override
    public void finish() {
        super.finish();
        activity = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment);
            fragment = null;
        }
        super.onDestroy();
    }

    public void initService() {
        try {
            // 初始化服务
            Intent service = new Intent(this, InitService.class);
            service.setAction(InitService.InitAction);
            startService(service);
            // 更新服务
            this.startService(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMainUI() {
        done++;
        if (done > 2) {
//            toindicator();//引导页
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
//            finish();
            AAnim.ActivityFinish(this);
        }
    }


    public void jump(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    //TODO 提前获取推荐频道第一页
    public void getChooseNewsJson() {
        App.getInstance().newTime = "";
        App.getInstance().oldTime = "";
        loadMainUI();
    }

}
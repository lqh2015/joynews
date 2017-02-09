package com.hzpd.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hzpd.adapter.MainPagerAdapter;
import com.hzpd.custorm.MyViewPager;
import com.hzpd.hflt.R;
import com.hzpd.modle.db.NewsChannelBeanDB;
import com.hzpd.modle.db.NewsChannelBeanDBDao;
import com.hzpd.modle.event.RefreshEvent;
import com.hzpd.modle.event.RestartEvent;
import com.hzpd.modle.event.SetThemeEvent;
import com.hzpd.network.Const;
import com.hzpd.services.InitService;
import com.hzpd.ui.App;
import com.hzpd.ui.ConfigBean;
import com.hzpd.ui.fragments.BaseFragment;
import com.hzpd.ui.fragments.FeedbackTagFragment;
import com.hzpd.ui.fragments.NewsFragment;
import com.hzpd.ui.fragments.ZY_DiscoveryFragment;
import com.hzpd.ui.fragments.ZY_RightFragment;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.EventUtils;
import com.hzpd.utils.ExitApplication;
import com.hzpd.utils.Log;
import com.hzpd.utils.SPUtil;
import com.hzpd.utils.SetAlarmUtils;
import com.news.update.LocalUpdateEvent;
import com.news.update.UpdateUtils;
import com.news.update.Utils;

import org.common.lib.analytics.HomeUtils;

import java.io.File;
import java.util.List;

import de.greenrobot.event.EventBus;


public class MainActivity extends BaseActivity {
    public final static String TAG = "NEWS";
    public final static String TAG_DIALOG = "NEWS_DIALOG";

    public MainActivity() {
        super();
    }

    protected SPUtil spu;
    private MyViewPager viewPager;
    private MainPagerAdapter adapter;
    private TextView[] tv_menu;
    private BaseFragment[] fragments;

    @Override
    public void finish() {
        App.isStartApp = false;
        super.finish();
    }

    @Override
    protected int getOnResumeApi() {
        return 102;
    }

    @Override
    protected int getOnStopApi() {
        return 103;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        networkStatus = 9;
        Const.mode = 100;
        HomeUtils.sendApi(200);
        getThisIntent();
        setContentView(R.layout.app_main);

        changeStatusBar();
        try {
            NetworkInfo networkInfo = SPUtil.getNetworkInfo(getApplicationContext());
            if (networkInfo != null && !Const.isSaveMode() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                Const.loadImage = false;
            }
            SetAlarmUtils.showIcon(this);
            spu = SPUtil.getInstance();
            tag = OkHttpClientManager.getTag();
            viewPager = (MyViewPager) findViewById(R.id.main_pager);
            adapter = new MainPagerAdapter(getSupportFragmentManager());
            fragments = new BaseFragment[3];
            tv_menu = new TextView[3];
            tv_menu[0] = (TextView) findViewById(R.id.tv_tab_menu0);
            tv_menu[1] = (TextView) findViewById(R.id.tv_tab_menu1);
            tv_menu[2] = (TextView) findViewById(R.id.tv_tab_menu2);
            for (int i = 0; i < tv_menu.length; i++) {
                final int cur = i;
                tv_menu[i].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        onClickIndex(cur);
                    }
                });
            }
            int index = 0;
            NewsFragment fg = new NewsFragment();
            fragments[index] = fg;
            adapter.add(fragments[index++]);
            if (position > 0) {
                fg.setChannel(position);
            }
//            if (ConfigBean.getInstance().open_channel.contains(SPUtil.getCountry())) {
//
//            } else {
//                NewsChannelBean channelBean = new NewsChannelBean();
//                channelBean.setTid("1");
//                channelBean.setCnname("NEWS");
//                fragments[index] = new NewsItemFragment(channelBean);
//                adapter.add(fragments[index++]);
//            }
            if (ConfigBean.getInstance().open_tag.contains(SPUtil.getCountry())) {
                fragments[index] = new ZY_DiscoveryFragment();
                adapter.add(fragments[index++]);
            } else {
                tv_menu[1].setVisibility(View.GONE);
            }
            fragments[index] = new ZY_RightFragment();
            adapter.add(fragments[index++]);
            viewPager.setOffscreenPageLimit(adapter.getCount());
            viewPager.setAdapter(adapter);
            onClickIndex(0);

            Thread.setDefaultUncaughtExceptionHandler(App.uncaughtExceptionHandler);
            App.isStartApp = true;
            EventUtils.sendStart(this);
            showLocalUpdateDialog(this);
//            showFeedback();
//            UpdateService.requestForUpdate(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int position;

    private void getThisIntent() {
        Intent intent = getIntent();
        String tid = intent.getStringExtra("channelid");
        if (!TextUtils.isEmpty(tid)) {
            Log.i("MainActivity", "channelid:" + tid);
            List<NewsChannelBeanDB> dbs = dbHelper.getChannel().queryBuilder().where(NewsChannelBeanDBDao.Properties.Default_show.eq("1")).build().list();
            int pos = 0;
            if (dbs != null || dbs.size() > 0) {
                for (int i = 0; i < dbs.size(); i++) {
                    if (dbs.get(i).getTid().equals("128")) {
                        pos = i;
                    }
                }
            }
            position = pos;
        }
    }

    public void onClickIndex(int index) {
        int current = viewPager.getCurrentItem();
        viewPager.setCurrentItem(index, false);
        for (int i = 0; i < tv_menu.length; i++) {
            if (index == i) {
                tv_menu[i].setSelected(true);
            } else {
                tv_menu[i].setSelected(false);
            }
        }
        if (index == 0) {
            App.getInstance().setIsVisible(true);
        } else {
            App.getInstance().setIsVisible(false);
        }
        if (index == 0 && current == 0) {
            EventBus.getDefault().post(new RefreshEvent());
        }
    }

    @Override
    public void onBackPressed() {

        if (WelcomeActivity.activity != null) {
            WelcomeActivity.activity.finish();
        }

        //退出程序
        ExitApplication.exit(this);
    }

    private static final String FEEDBACK_TIME = "feedback_time";
    public static final String FEEDBACK_COUNT = "feedback_count";

    public void showFeedback() {
        if (FeedbackTagFragment.shown) {
            return;
        }
        int countFeedBack = (int) SPUtil.getGlobal(FEEDBACK_COUNT, 0L);
        if (countFeedBack < 3) {
            long last = SPUtil.getGlobal(FEEDBACK_TIME, 0L);
            if (last == 0) {
                SPUtil.setGlobal(FEEDBACK_TIME, System.currentTimeMillis());
                return;
            }
            ++countFeedBack;
            if (System.currentTimeMillis() - last > countFeedBack * ConfigBean.getInstance().rate_time) {
                SPUtil.setGlobal(FEEDBACK_COUNT, countFeedBack);
                SPUtil.setGlobal(FEEDBACK_TIME, System.currentTimeMillis());
                FeedbackTagFragment fragment = new FeedbackTagFragment();
                fragment.show(getSupportFragmentManager(), FeedbackTagFragment.TAG);
            }
        }


    }


    private Object tag;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = new Intent(this, InitService.class);
        intent.setAction(InitService.UserLogAction);
        startService(intent);
        // 初始化服务
        Intent service = new Intent(this, InitService.class);
        service.setAction(InitService.InitAction);
        startService(service);
    }

    @Override
    protected void onDestroy() {
        OkHttpClientManager.cancel(tag);
        App.getInstance().newTimeMap.clear();
        super.onDestroy();
    }

    public void onEventMainThread(SetThemeEvent event) {
        recreate();
    }

    public void onEventMainThread(RestartEvent event) {
        if (event.isChangeCountry) {
            SPUtil.setGlobal("isChangeCountry", true);
            onClickIndex(0);
        }
        finish();
    }

    public void onEventMainThread(LocalUpdateEvent event) {
        Log.e("UPDATE", null);
        showLocalUpdateDialog(this);
    }

    /**
     * 弹出更新提示对话框
     */
    public static void showLocalUpdateDialog(FragmentActivity activity) {
        SharedPreferences pref = activity.getSharedPreferences(
                UpdateUtils.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        try {
            if (pref.getBoolean(UpdateUtils.KEY.KEY_SILENCE_INSTALL, false)) {
                return;
            }
            long later = pref.getLong(UpdateUtils.KEY.UPDATE_LATER_TIME, 0L);
            if (later > 10000) {
                if (System.currentTimeMillis() - later > UpdateUtils.UPDATE_LATER_TIME) {
                    pref.edit()
                            .putLong(UpdateUtils.KEY.UPDATE_LATER_TIME, 0L)
                            .apply();
                } else {
                    return;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (pref.getBoolean(UpdateUtils.KEY.KEY_HAS_NEW, false)) {
                boolean forcing = pref.getBoolean(UpdateUtils.KEY.KEY_FORCING_UPDATE, false);

                int versionCode = pref.getInt(UpdateUtils.KEY.KEY_VERSION_CODE, 0);
                if (versionCode <= Utils.getVersionCode(activity)) {
                    Log.e("update", "versionCode "
                            + versionCode + ":" + Utils.getVersionCode(activity));
                    pref.edit().putBoolean(UpdateUtils.KEY.KEY_HAS_NEW,
                            false);
                    return;
                }

                if (!UpdateUtils.isRomVersion(activity.getApplicationContext())) {
                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }


                if (!forcing) {
                    if (pref.getBoolean(UpdateUtils.KEY.IS_DOWNLOADING, false)
                            || pref.getBoolean(UpdateUtils.KEY.IS_WIFI_DOWNLOADING, false)) {
                        return;
                    }
                }
                boolean autoD = pref.getBoolean(UpdateUtils.KEY.KEY_AUTO_DOWNLOAD, false);
                int cVersion = pref.getInt(UpdateUtils.KEY.COMPLETED_VERSION_CODE, 0);
                if (autoD) {
                    File root = Environment.getExternalStorageDirectory();
                    if (root != null) {
                        File fold = new File(root, UpdateUtils.PATH_SAVE);
                        File target = new File(fold, UpdateUtils.getFileName(activity.getApplicationContext()));
                        if (!target.exists() || cVersion < versionCode) {
                            if (!forcing) {
                                return;
                            }
                        } else if (target.exists()) {
                            String md5 = null;
                            md5 = UpdateUtils.getHash(target.getAbsolutePath(), "MD5").toLowerCase();
                            if (!md5.equals(pref.getString(UpdateUtils.KEY.KEY_MD5, ""))) {
                                target.delete();
                                pref.edit().putInt(UpdateUtils.KEY.COMPLETED_VERSION_CODE, 0).apply();
                                if (!forcing) {
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
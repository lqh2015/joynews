package com.hzpd.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.hzpd.hflt.R;
import com.hzpd.network.Const;
import com.hzpd.network.NetworkDialogFragment;
import com.hzpd.network.NetworkEvent;
import com.hzpd.network.NetworkMobileDialogFragment;
import com.hzpd.network.NetworkSpeed;
import com.hzpd.ui.App;
import com.hzpd.utils.AAnim;
import com.hzpd.utils.DBHelper;
import com.hzpd.utils.SPUtil;
import com.hzpd.utils.SystemBarTintManager;
import com.hzpd.utils.TUtils;

import org.common.lib.analytics.ActivityLifecycleAction;
import org.common.lib.analytics.AnalyticCallback;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

public class BaseActivity extends FragmentActivity implements AnalyticCallback {

    private ActivityLifecycleAction action = new ActivityLifecycleAction(this);
    protected FragmentManager fm;
    protected Fragment currentFm;
    protected boolean isResume = false;
    protected Activity activity;
    protected SPUtil spu;
    protected HashMap<String, String> analyMap;
    protected DBHelper dbHelper;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    protected int getOnResumeApi() {
        return 0;
    }

    protected int getOnStopApi() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        activity = this;
        spu = SPUtil.getInstance();
        analyMap = new HashMap<String, String>();
        dbHelper = DBHelper.getInstance();
//        if (SPUtil.isEmulator()) {
//            android.os.Process.killProcess(android.os.Process.myPid());
//        }
        if (App.getInstance().getThemeName().equals("2")) {
            setTheme(R.style.ThemeNight);
        } else {
            setTheme(R.style.ThemeDefault);
        }
        super.onCreate(null);
        action.onCreate(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        changeStatusBar();
        fm = getSupportFragmentManager();

    }

    protected void changeStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.title_bar_color, typedValue, true);
        int color = typedValue.data;
        tintManager.setStatusBarTintColor(color);
//        tintManager.setStatusBarTintResource(R.color.main_title);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        AAnim.ActivityFinish(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        isResume = false;
        fm = null;
        activity = null;
        spu = null;
        super.onDestroy();
        action.onDestory(this);
    }

    @Override
    protected void onPause() {
        isResume = false;
        super.onPause();
        action.onPause(this);
    }


    @Override
    protected void onStart() {
        isResume = true;
        super.onStart();
        action.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isResume = false;
        action.onStop(this);
    }


    @Override
    protected void onResume() {
        isResume = true;
        super.onResume();
        action.onResume(this);
        checkNetwork();
    }

    @Override
    public String getAnalyticPageName() {
        return null;
    }

    public void onEventMainThread(NetworkEvent event) {
        NetworkSpeed.testSpeed();
        checkNetwork();
    }

    public void checkNetwork() {
        NetworkInfo networkInfo = null;
        networkInfo = SPUtil.getNetworkInfo(getApplicationContext());
        if (networkInfo == null) {
            if (networkStatus < Const.NETWORK_OFFLINE) {
                if (!isResume) {
                    return;
                }
                networkStatus = Const.NETWORK_OFFLINE;
                if (!NetworkDialogFragment.shown && SPUtil.isIconShow()) {
                    NetworkDialogFragment fragment = (NetworkDialogFragment) Fragment.instantiate(this, NetworkDialogFragment.class.getName());
                    fragment.show(fm, NetworkDialogFragment.TAG);
                }
            }
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            if (networkStatus != Const.NETWORK_WIFI) {
                if (!isResume) {
                    return;
                }
                networkStatus = Const.NETWORK_WIFI;
                NetworkDialogFragment.dismissIfShow();
                NetworkMobileDialogFragment.dismissIfShow();
                if (Const.isSaveMode()) {
                    TUtils.toast(getString(R.string.switch_wifi));
                }
                Const.setBestMode();
            }
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            if (networkStatus != Const.NETWORK_MOBILE) {
                if (!isResume) {
                    return;
                }
                NetworkDialogFragment.dismissIfShow();
                networkStatus = Const.NETWORK_MOBILE;
                if (!Const.isSaveMode() && !NetworkMobileDialogFragment.shown && SPUtil.isIconShow()) {
                    NetworkMobileDialogFragment fragment = (NetworkMobileDialogFragment) Fragment.instantiate(this, NetworkMobileDialogFragment.class.getName());
                    fragment.show(fm, NetworkMobileDialogFragment.TAG);
                }
            }
        }

    }

    protected static int networkStatus = 9;
}
package com.hzpd.ui.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzpd.custorm.switchbutton.SwitchButton;
import com.hzpd.hflt.R;
import com.hzpd.modle.event.FontSizeEvent;
import com.hzpd.modle.event.RestartEvent;
import com.hzpd.services.ClearCacheService;
import com.hzpd.ui.App;
import com.hzpd.ui.widget.FontTextView;
import com.hzpd.utils.AAnim;
import com.hzpd.utils.AnalyticUtils;
import com.hzpd.utils.AvoidOnClickFastUtils;
import com.hzpd.utils.CODE;
import com.hzpd.utils.GetFileSizeUtil;
import com.hzpd.utils.Log;
import com.hzpd.utils.SPUtil;
import com.hzpd.utils.SharePreferecesUtils;
import com.hzpd.utils.TUtils;
import com.news.update.CheckUpdateEvent;
import com.news.update.UpdateUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sithagi.countrycodepicker.CountryPicker;
import com.sithagi.countrycodepicker.CountryPickerListener;

import java.io.File;

import de.greenrobot.event.EventBus;

public class SettingActivity extends MBaseActivity implements View.OnClickListener {
    @Override
    public String getAnalyticPageName() {
        return AnalyticUtils.SCREEN.setting;
    }

    @Override
    protected int getOnResumeApi() {
        return 124;
    }

    @Override
    protected int getOnStopApi() {
        return 125;
    }

    private TextView stitle_tv_content;
    private LinearLayout zqzx_setting_textsize;
    private FontTextView setting_chosse_textsize;
    private FontTextView zqzx_setting_cache;
    private FontTextView zqzx_setting_tv_version;
    //站点设置
    private SwitchButton sb_use_listener;
    private View loadingView;
    private LinearLayout zqzx_setting_login_out;
    private AlertDialog.Builder mDeleteDialog;
    private TextView setting_choose_country;

    private View zqzx_setting_aboutus;
    private View zqzx_setting_deletecache;
    private View zqzx_setting_feedback;
    private View stitle_ll_back;
    private View zqzx_setting_update;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zqzx_setting_layout);
        super.changeStatusBar();
        initViews();
        stitle_tv_content.setText(R.string.title_settings);//标题
        if (spu.getUser() != null) {
            zqzx_setting_login_out.setVisibility(View.VISIBLE);
        }

        textSize = new String[]{this.getResources().getString(R.string.settings_option_font_large), this.getResources().getString(R.string.settings_option_font_medium), this.getResources().getString(R.string.settings_option_font_small)};//"Besar", "Sedang", "Kecil"
        zqzx_setting_textsize.setOnClickListener(new TextSizeAlertClickListener());
        //设置字体大小
        switch (spu.getTextSize()) {
            case CODE.textSize_big: {
                setting_chosse_textsize.setText(getString(R.string.settings_option_font_large));
            }
            break;
            case CODE.textSize_normal: {
                setting_chosse_textsize.setText(getString(R.string.settings_option_font_medium));
            }
            break;
            case CODE.textSize_small: {
                setting_chosse_textsize.setText(getString(R.string.settings_option_font_small));
            }
            break;
        }

        //推送获取当前状态
        if (spu.getOffTuiSong()) {
            sb_use_listener.setChecked(true);
        } else {
            sb_use_listener.setChecked(false);
        }
        //推送消息
        sb_use_listener.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pushSwitch(isChecked);
                } else {
                    pushSwitch(isChecked);
                }
            }
        });

        //获取版本信息
        zqzx_setting_tv_version.setText(App.getInstance().getVersionName());
        //设置国家
        setting_choose_country = (TextView) findViewById(R.id.setting_choose_country);
        Object country_Name = SharePreferecesUtils.getParam(SettingActivity.this, "CountryName", "Indonesia");
        setting_choose_country.setText("" + country_Name.toString());
        findViewById(R.id.zqzx_setting_choose_country).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CountryPicker picker = CountryPicker.newInstance("SelectCountry");
                picker.setListener(new CountryPickerListener() {

                    @Override
                    public void onSelectCountry(String name, String code, String dialCode) {
                        Log.e("test", "News: " + code);
                        if (code.toLowerCase().equals(SPUtil.getCountry())) {
                            return;
                        }
                        setting_choose_country.setText("" + name);
                        SharePreferecesUtils.setParam(SettingActivity.this, "CountryName", name);
                        SPUtil.setCountry(code);
                        EventBus.getDefault().post(new RestartEvent());
                        restartApplication();
                        finish();
                        picker.dismiss();
                    }
                });
                picker.show(getSupportFragmentManager(), "COUNTRY_CODE_PICKER");
            }
        });

        getCacheSize();
    }

    private void initViews() {
        loadingView = findViewById(R.id.app_progress_bar);
        stitle_tv_content = (TextView) findViewById(R.id.stitle_tv_content);
        zqzx_setting_textsize = (LinearLayout) findViewById(R.id.zqzx_setting_textsize);
        setting_chosse_textsize = (FontTextView) findViewById(R.id.setting_chosse_textsize);
        zqzx_setting_cache = (FontTextView) findViewById(R.id.zqzx_setting_cache);
        zqzx_setting_tv_version = (FontTextView) findViewById(R.id.zqzx_setting_tv_version);
        sb_use_listener = (SwitchButton) findViewById(R.id.sb_use_listener);
        zqzx_setting_aboutus = findViewById(R.id.zqzx_setting_aboutus);
        zqzx_setting_aboutus.setOnClickListener(this);
        zqzx_setting_deletecache = findViewById(R.id.zqzx_setting_deletecache);
        zqzx_setting_deletecache.setOnClickListener(this);
        zqzx_setting_feedback = findViewById(R.id.zqzx_setting_feedback);
        zqzx_setting_feedback.setOnClickListener(this);
        stitle_ll_back = findViewById(R.id.stitle_ll_back);
        stitle_ll_back.setOnClickListener(this);
        zqzx_setting_update = findViewById(R.id.zqzx_setting_update);
        zqzx_setting_update.setOnClickListener(this);
        zqzx_setting_login_out = (LinearLayout) findViewById(R.id.zqzx_setting_login_out);
        zqzx_setting_login_out.setOnClickListener(this);
    }

    private void restartApplication() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void pushSwitch(boolean isChecked) {
//		保存推送开关状态
        spu.setOffTuiSong(isChecked);

    }


    @Override
    public void onClick(View v) {
        if (AvoidOnClickFastUtils.isFastDoubleClick(v)) {
            return;
        }
        switch (v.getId()) {
            case R.id.zqzx_setting_aboutus:
//                Intent intentVedio = new Intent(this, ListActivity.class);
//                startActivity(intentVedio);
                break;
            case R.id.zqzx_setting_deletecache:
                mDeleteDialog = new Builder(this);
                mDeleteDialog.setTitle(R.string.prompt_clear_cache_dialog_title);
                mDeleteDialog.setMessage(R.string.prompt_clear_cache_dialog_msg);
                mDeleteDialog.setNegativeButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                loadingView.setVisibility(View.VISIBLE);
                                activity.startService(new Intent(activity, ClearCacheService.class));
                                handler.sendEmptyMessageDelayed(111, 2000);
                            }
                        });
                mDeleteDialog.setPositiveButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                loadingView.setVisibility(View.GONE);
                            }
                        });
                mDeleteDialog.show();
                break;
            case R.id.zqzx_setting_feedback:
                Intent intent = new Intent(this, ZQ_FeedBackActivity.class);
                startActivity(intent);
                AAnim.ActivityStartAnimation(this);
                break;
            case R.id.stitle_ll_back:
                finish();
                break;
            case R.id.zqzx_setting_update:
                try {
                    getSharedPreferences(UpdateUtils.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE)
                            .edit()
                            .putLong(UpdateUtils.KEY.UPDATE_LATER_TIME, 0L)
                            .apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.zqzx_setting_login_out:
        }
    }


    private String[] textSize;
    private int[] textSize1 = new int[]{CODE.textSize_big, CODE.textSize_normal, CODE.textSize_small};

    /**
     * 菜单弹出窗口
     */
    class TextSizeAlertClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(SettingActivity.this).setTitle(getString(R.string.language)).setItems(textSize, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    setting_chosse_textsize.setText(textSize[which]);
                    spu.setTextSize(textSize1[which]);
                    FontSizeEvent event = new FontSizeEvent();
                    event.setFontSize(textSize1[which]);
                    EventBus.getDefault().post(event);
                    dialog.dismiss();
                }
            }).show();
        }
    }

    public void getCacheSize() {
        GetFileSizeUtil fz = GetFileSizeUtil.getInstance();
        String cacheSize = "0B";
        try {

            cacheSize = "" + fz.FormetFileSize(fz.getFileDirSize(ImageLoader.getInstance().getDiskCache().getDirectory())
                    + fz.getFileDirSize(new File(App.getInstance().getJsonFileCacheRootDir())));

        } catch (Exception e) {
            e.printStackTrace();
        }
        zqzx_setting_cache.setText(cacheSize);
    }

    private void deleteSuccess() {
        loadingView.setVisibility(View.GONE);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (111 == msg.what) {
                zqzx_setting_cache.setText("0B");
                loadingView.setVisibility(View.VISIBLE);
                deleteSuccess();
            } else if (112 == msg.what) {
                TUtils.toast(getString(R.string.toast_bind_failed));
            } else if (113 == msg.what) {
                TUtils.toast(getString(R.string.toast_bind_success));
            }
        }
    };

    public void onEventMainThread(CheckUpdateEvent event) {
        Log.e("UPDATE", null);
        SharedPreferences pref = getSharedPreferences(
                UpdateUtils.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (UpdateUtils.isRomVersion(this) && pref.getBoolean(UpdateUtils.KEY.KEY_SILENCE_INSTALL, false)) {
            TUtils.toast(getString(R.string.update_no_version));
        } else if (!pref.getBoolean(UpdateUtils.KEY.KEY_HAS_NEW, false)) {
            TUtils.toast(getString(R.string.update_no_version));
        } else {
            MainActivity.showLocalUpdateDialog(this);
        }
    }

}
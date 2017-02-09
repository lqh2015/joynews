package com.hzpd.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hzpd.custorm.CircleImageView;
import com.hzpd.hflt.R;
import com.hzpd.modle.ThirdLoginBean;
import com.hzpd.modle.UserBean;
import com.hzpd.modle.event.RestartEvent;
import com.hzpd.modle.event.SetThemeEvent;
import com.hzpd.network.Const;
import com.hzpd.network.NetworkEvent;
import com.hzpd.ui.App;
import com.hzpd.ui.activity.MyCommentsActivity;
import com.hzpd.ui.activity.MyPMColAvtivity;
import com.hzpd.ui.activity.MyPushActivity;
import com.hzpd.ui.activity.RecentlyReadActivity;
import com.hzpd.ui.activity.SettingActivity;
import com.hzpd.ui.activity.ZQ_FeedBackActivity;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.AAnim;
import com.hzpd.utils.AnalyticUtils;
import com.hzpd.utils.AvoidOnClickFastUtils;
import com.hzpd.utils.DisplayOptionFactory;
import com.hzpd.utils.FjsonUtil;
import com.hzpd.utils.Log;
import com.hzpd.utils.RequestParamsUtils;
import com.hzpd.utils.SPUtil;
import com.hzpd.utils.SharePreferecesUtils;
import com.hzpd.utils.TUtils;
import com.sithagi.countrycodepicker.CountryPicker;
import com.sithagi.countrycodepicker.CountryPickerListener;
import com.squareup.okhttp.Request;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class ZY_RightFragment extends BaseFragment implements View.OnClickListener {
    @Override
    public String getAnalyticPageName() {
        return AnalyticUtils.SCREEN.leftMenu;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisible != isVisibleToUser) {
            if (isVisibleToUser) {
                AnalyticUtils.sendGaEvent(getActivity(), AnalyticUtils.CATEGORY.slidMenu, AnalyticUtils.ACTION.viewPage, null, 0L);
                AnalyticUtils.sendUmengEvent(getActivity(), AnalyticUtils.CATEGORY.slidMenu);
                checkMode();
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void checkMode() {
        if (Const.isSaveMode()) {
            image_save_mode.setImageResource(R.drawable.personal_icon_normal);
            page_mode.setText(R.string.page_best_mode);
        } else {
            image_save_mode.setImageResource(R.drawable.personal_icon_save);
            page_mode.setText(R.string.page_save_mode);
        }
    }

    public static final String ACTION_USER = "com.hzpd.cms.user";
    public static final String ACTION_QUIT = "com.hzpd.cms.quit";
    public static final String ACTION_QUIT_LOGIN = "com.hzpd.cms.quit.login";
    private TextView zy_rfrag_tv_login;
    private CircleImageView zy_rfrag_iv_login;
    private LoginQuitBR br;
    private TextView night_mode;
    private TextView page_mode;
    private ImageView image_skin_mode;
    private ImageView image_save_mode;
    private View coverTop;
    private View zy_rfrag_ll_comm, zy_rfrag_ll_collect, zy_rfrag_ll_push, zy_rfrag_ll_setting, zy_rfrag_ll_feedback, zy_page_mode, zy_rfrag_ll_night, zy_rfrag_ll_read, zy_rfrag_ll_download;
    private View zy_rfrag_ll_rate_us;
    private TextView personal_item_text;
    private Object tag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.zy_rightfragment, container, false);
            initViews(view);
            String countryname = null;
            tag = OkHttpClientManager.getTag();
            if (SPUtil.getGlobal("isChangeCounty", false)) {
                String name = SPUtil.getCountryName();
                countryname = name.toUpperCase().charAt(0) + name.substring(1, name.length());
            } else {
                Locale locale = Locale.getDefault();
                String allCountriesCode = CountryPicker.readEncodedJsonString(getActivity());
                JSONArray countrArray = new JSONArray(allCountriesCode);
                for (int i = 0; i < countrArray.length(); i++) {
                    org.json.JSONObject jsonObject = countrArray.getJSONObject(i);
                    String countryName = jsonObject.getString("name");
                    String countryCode = jsonObject.getString("code");
                    if (countryCode.equalsIgnoreCase(locale.getCountry())) {
                        countryname = countryName;
                    }
                    if (countryCode.equalsIgnoreCase("cn")) {
                        countryname = "Indonesia";
                    }
                }
            }
            personal_item_text.setText("" + countryname);
            if (App.getInstance().getThemeName().equals("0")) {
                coverTop.setVisibility(View.GONE);
            } else {
                coverTop.setVisibility(View.VISIBLE);
            }
            switch (App.getInstance().getThemeName()) {
                case "0": {
                    night_mode.setText(getResources().getString(R.string.night_mode));
                    image_skin_mode.setImageResource(R.drawable.personal_icon_night);
                    isChangeSkin = false;
                }
                break;
                case "2": {
                    night_mode.setText(getResources().getString(R.string.day_mode));
                    image_skin_mode.setImageResource(R.drawable.personal_icon_day);
                    isChangeSkin = true;
                }
                break;
            }

            checkMode();
        } catch (Exception e) {

        }
        EventBus.getDefault().register(this);
        return view;
    }

    private void initViews(View view) {
        zy_rfrag_tv_login = (TextView) view.findViewById(R.id.zy_rfrag_tv_login);
        zy_rfrag_iv_login = (CircleImageView) view.findViewById(R.id.zy_rfrag_iv_login);
        night_mode = (TextView) view.findViewById(R.id.night_mode);
        image_skin_mode = (ImageView) view.findViewById(R.id.image_skin_mode);
        image_save_mode = (ImageView) view.findViewById(R.id.image_save_mode);
        coverTop = view.findViewById(R.id.cover_top);
        zy_rfrag_ll_comm = view.findViewById(R.id.zy_rfrag_ll_comm);
        zy_rfrag_ll_comm.setOnClickListener(this);
        zy_rfrag_ll_collect = view.findViewById(R.id.zy_rfrag_ll_collect);
        zy_rfrag_ll_collect.setOnClickListener(this);
        zy_rfrag_ll_push = view.findViewById(R.id.zy_rfrag_ll_push);
        zy_rfrag_ll_push.setOnClickListener(this);
        zy_rfrag_ll_setting = view.findViewById(R.id.zy_rfrag_ll_setting);
        zy_rfrag_ll_setting.setOnClickListener(this);
        zy_rfrag_ll_feedback = view.findViewById(R.id.zy_rfrag_ll_feedback);
        zy_rfrag_ll_feedback.setOnClickListener(this);
        zy_page_mode = view.findViewById(R.id.zy_page_mode);
        page_mode = (TextView) view.findViewById(R.id.page_mode);
        zy_page_mode.setOnClickListener(this);
        zy_rfrag_ll_download = view.findViewById(R.id.zy_rfrag_ll_download);
        personal_item_text = (TextView) view.findViewById(R.id.choose_country);
        zy_rfrag_ll_download.setOnClickListener(this);
        zy_rfrag_ll_night = view.findViewById(R.id.zy_rfrag_ll_night);
        zy_rfrag_ll_night.setOnClickListener(this);
        zy_rfrag_ll_read = view.findViewById(R.id.zy_rfrag_ll_read);
        zy_rfrag_ll_read.setOnClickListener(this);
        zy_rfrag_ll_rate_us = view.findViewById(R.id.zy_rfrag_ll_rate_us);
        zy_rfrag_ll_rate_us.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            br = new LoginQuitBR();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ZY_RightFragment.ACTION_QUIT);
            filter.addAction(ZY_RightFragment.ACTION_USER);
            filter.addAction(ZY_RightFragment.ACTION_QUIT_LOGIN);
            activity.registerReceiver(br, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null != spu.getUser()) {
            Log.i("test", "userimg-->" + spu.getUser().getAvatar_path());
            SPUtil.displayImage(spu.getUser().getAvatar_path(), zy_rfrag_iv_login,
                    DisplayOptionFactory.Avatar.options);
            zy_rfrag_tv_login.setText("" + spu.getUser().getNickname());
        }

    }

    public void thirdlogin(ThirdLoginBean tlb) {
        Map<String, String> params = RequestParamsUtils.getMaps();
        params.put("userid", tlb.getUserid());
        params.put("gender", tlb.getGender());
        params.put("nickname", tlb.getNickname());
        params.put("photo", tlb.getPhoto());
        params.put("third", tlb.getThird());
        params.put("is_ucenter", "0");
        SPUtil.addParams(params);
        OkHttpClientManager.postAsyn(tag
                , InterfaceJsonfile.thirdLogin
                , resultCallback
                , params);
    }

    private OkHttpClientManager.ResultCallback resultCallback = new OkHttpClientManager.ResultCallback() {
        @Override
        public void onSuccess(Object response) {
            try {
                JSONObject obj = FjsonUtil
                        .parseObject(response.toString());
                if (null == obj) {
                    return;
                }
                if (200 == obj.getIntValue("code")) {
                    UserBean user = FjsonUtil.parseObject(obj.getString("data"), UserBean.class);
                    spu.setUser(user);
                } else if (isVisible) {
//                                TUtils.toast(obj.getString("msg"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Request request, Exception e) {
            Log.i("test", "test login failed");
        }

    };


    @Override
    public void onDestroyView() {
        OkHttpClientManager.cancel(tag);
        super.onDestroyView();
    }

    private List<String> permissions = Arrays.asList("public_profile", "user_friends");
    private boolean isChangeSkin;


    private void restartApplication() {
        final Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (AvoidOnClickFastUtils.isFastDoubleClick(v))
            return;
        boolean flag = false;
        Intent mIntent = new Intent();
        switch (v.getId()) {
            case R.id.zy_rfrag_ll_comm: {
                mIntent.setClass(activity, MyCommentsActivity.class);
                flag = true;
            }
            break;
            case R.id.zy_rfrag_ll_collect: {
                Log.i("", "shoucang--");
                mIntent.setClass(activity, MyPMColAvtivity.class);
                mIntent.putExtra("type", "collection");
                flag = true;
            }
            break;
            case R.id.zy_rfrag_ll_push: {
                Log.i("", "shoucang--push");
                mIntent.setClass(activity, MyPushActivity.class);
                mIntent.putExtra("type", "pushmsg");
                flag = true;
            }
            break;
            case R.id.zy_rfrag_ll_setting: {
                mIntent.setClass(activity, SettingActivity.class);
                flag = true;
            }
            break;
            case R.id.zy_rfrag_ll_feedback: {
                mIntent.setClass(activity, ZQ_FeedBackActivity.class);
                mIntent.putExtra("FEEDBACK_FROM", "ZY_RightFragment");
                flag = true;
            }
            break;
            case R.id.zy_page_mode: {
                //TODO 省流量模式
                Const.toggleMode();
                checkMode();
                if (Const.isSaveMode()) {
                    TUtils.toast(getString(R.string.switch_normal));
                } else {
                    TUtils.toast(getString(R.string.switch_save_flow));
                }
                flag = false;
            }
            break;
            case R.id.zy_rfrag_ll_download: {
//                Toast.makeText(getActivity(), "语言。。。", Toast.LENGTH_SHORT).show();
                final CountryPicker picker = CountryPicker.newInstance(getActivity().getString(R.string.select_country));
                picker.setListener(new CountryPickerListener() {

                    @Override
                    public void onSelectCountry(String name, String code, String dialCode) {
                        if (code.toLowerCase().equals(SPUtil.getCountry())) {
                            return;
                        }
                        SharePreferecesUtils.setParam(getActivity(), "CountryName", name);
                        personal_item_text.setText("" + name);
                        SPUtil.setCountry(code);
                        SPUtil.setCountryName(name);
                        SPUtil.setGlobal("isChangeCounty", true);
//                      重新设置
                        EventBus.getDefault().post(new RestartEvent(true));
                        restartApplication();
                        picker.dismiss();
                    }
                });

                picker.show(getActivity().getSupportFragmentManager(), "COUNTRY_CODE_PICKER");
            }
            break;
            case R.id.zy_rfrag_ll_night: {
                if (isChangeSkin) {
                    night_mode.setText(getResources().getString(R.string.night_mode));
                    image_skin_mode.setImageResource(R.drawable.personal_icon_night);
                    App.getInstance().setThemeName("" + 0);
                    EventBus.getDefault().post(new SetThemeEvent());
                    isChangeSkin = false;
                } else {
                    night_mode.setText(getResources().getString(R.string.day_mode));
                    image_skin_mode.setImageResource(R.drawable.personal_icon_day);
                    App.getInstance().setThemeName("" + 2);
                    EventBus.getDefault().post(new SetThemeEvent());
                    isChangeSkin = true;
                }
            }
            break;
            case R.id.zy_rfrag_ll_read: {
                mIntent.setClass(activity, RecentlyReadActivity.class);
                flag = true;
            }
            break;
            case R.id.zy_rfrag_ll_rate_us: {
                mIntent = SPUtil.getIntent(getActivity());
                if (mIntent == null) {
                    flag = false;
                } else {
                    flag = true;
                }
            }
            break;
            default:
                break;
        }
        if (flag) {
            startActivity(mIntent);
            AAnim.ActivityStartAnimation(activity);
        }
    }

    public class LoginQuitBR extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ZY_RightFragment.ACTION_USER)) {
                setLogin();
            } else if (action.equals(ZY_RightFragment.ACTION_QUIT)) {
                setQuit();
                Log.i("test", "setquit");
            } else if (action.equals(ZY_RightFragment.ACTION_QUIT_LOGIN)) {
                setQuit();
                Log.i("test", "setquitlogin");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    }
                }, 500);
            }
        }
    }

    private void setLogin() {
        Log.i("test", "imgUrl-->" + spu.getUser().getAvatar_path() + "  name-->" + spu.getUser().getNickname());
        SPUtil.displayImage(spu.getUser().getAvatar_path(), zy_rfrag_iv_login,
                DisplayOptionFactory.Avatar.options);
        zy_rfrag_tv_login.setText(spu.getUser().getNickname());
        // -----
    }

    private void setQuit() {
        zy_rfrag_iv_login.setImageResource(R.drawable.news_detail_avatar);
        zy_rfrag_tv_login.setText(R.string.login);
    }

    @Override
    public void onDestroy() {
        try {
            activity.unregisterReceiver(br);
        } catch (Exception e) {

        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    public void onEventMainThread(NetworkEvent event) {
        if (isAdded()) {
            NetworkInfo networkInfo = SPUtil.getNetworkInfo(getActivity());
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Const.setBestMode();
                checkMode();
            }
        }
    }


}
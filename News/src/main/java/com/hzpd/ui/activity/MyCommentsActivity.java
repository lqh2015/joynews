package com.hzpd.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.hzpd.adapter.MycommentsAdapter;
import com.hzpd.custorm.CircleImageView;
import com.hzpd.hflt.R;
import com.hzpd.modle.MycommentsBean;
import com.hzpd.modle.XF_UserInfoBean;
import com.hzpd.ui.App;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.AnalyticUtils;
import com.hzpd.utils.DisplayOptionFactory;
import com.hzpd.utils.FjsonUtil;
import com.hzpd.utils.RequestParamsUtils;
import com.hzpd.utils.SPUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCommentsActivity extends MBaseActivity implements View.OnClickListener {

    private CircleImageView xf_pinfo_iv_avatar;//头像
    private TextView xf_pinfo_tv_nickname;//昵称
    private TextView xf_pinfo_tv_level_alias;//级别
    private TextView xf_pinfo_tv_level;//级别
    private TextView xf_pinfo_tv_score;//分数
    private NumberProgressBar xf_pinfo_npb;//进度条
    private TextView xf_pinfo_tv_regtime;//注册时间
    private TextView xf_pinfo_tv_levelup;//升级提示
    private View app_progress_bar;

    @Override
    public String getAnalyticPageName() {
        return AnalyticUtils.SCREEN.myComment;
    }

    @Override
    protected int getOnResumeApi() {
        return 118;
    }

    @Override
    protected int getOnStopApi() {
        return 119;
    }


    private TextView stitle_tv_content;
    private View pushmsg_tv_empty;
    private LinearLayout mycomments_title;
    private TextView mycoms_text;
    private ListView listView;
    private int Page = 1;
    private static final int PageSize = 1500;
    private boolean mFlagRefresh;
    private MycommentsAdapter adapter;
    private View coverTop;
    private View stitle_ll_back;
    private Object tag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycomment_layout);
        super.changeStatusBar();
        initViews();
        coverTop = findViewById(R.id.cover_top);
        if (App.getInstance().getThemeName().equals("0")) {
            coverTop.setVisibility(View.GONE);
        } else {
            coverTop.setVisibility(View.VISIBLE);
        }
        stitle_tv_content.setText(R.string.comment_mine);
        listView = (ListView) findViewById(R.id.list_view);
        tag = OkHttpClientManager.getTag();
        setHeadView();
        listView.setEmptyView(pushmsg_tv_empty);
        adapter = new MycommentsAdapter(this);
        listView.setAdapter(adapter);

        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Page = 1;
                getInfoFromServer();
            }
        }, 600);

        getUserInfoFromServer();

    }

    private void initViews() {
        stitle_ll_back = findViewById(R.id.stitle_ll_back);
        stitle_ll_back.setOnClickListener(this);
        stitle_tv_content = (TextView) findViewById(R.id.stitle_tv_content);
        pushmsg_tv_empty = findViewById(R.id.pushmsg_tv_empty);
        mycomments_title = (LinearLayout) findViewById(R.id.mycomments_title);
        mycoms_text = (TextView) findViewById(R.id.mycoms_text);
        app_progress_bar = findViewById(R.id.app_progress_bar);
    }

    private void setHeadView() {
        View headView = LayoutInflater.from(activity).inflate(R.layout.my_comment_headview, null);
        headView.findViewById(R.id.xf_pinfo_iv_back).setVisibility(View.GONE);
        headView.findViewById(R.id.xf_pinfo_iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });
        xf_pinfo_iv_avatar = (CircleImageView) headView.findViewById(R.id.xf_pinfo_iv_avatar);
        xf_pinfo_tv_nickname = (TextView) headView.findViewById(R.id.xf_pinfo_tv_nickname);
        xf_pinfo_tv_level_alias = (TextView) headView.findViewById(R.id.xf_pinfo_tv_level_alias);
        xf_pinfo_tv_level = (TextView) headView.findViewById(R.id.xf_pinfo_tv_level);
        xf_pinfo_tv_score = (TextView) headView.findViewById(R.id.xf_pinfo_tv_score);
        xf_pinfo_npb = (NumberProgressBar) headView.findViewById(R.id.xf_pinfo_npb);
        xf_pinfo_tv_regtime = (TextView) headView.findViewById(R.id.xf_pinfo_tv_regtime);
        xf_pinfo_tv_levelup = (TextView) headView.findViewById(R.id.xf_pinfo_tv_levelup);
        listView.addHeaderView(headView);
    }

    @Override
    protected void onDestroy() {
        OkHttpClientManager.cancel(tag);
        super.onDestroy();
    }

    private XF_UserInfoBean userInfoBean;

    private void getUserInfoFromServer() {
        if (spu.getUser() == null) {
            app_progress_bar.setVisibility(View.GONE);
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("uid", "" + spu.getUser().getUid());
        params.put("siteid", InterfaceJsonfile.SITEID);
        SPUtil.addParams(params);
        OkHttpClientManager.postAsyn(tag, InterfaceJsonfile.XF_USERINFO, getUserInfoFromServerResultCallback, params);
    }

    private OkHttpClientManager.ResultCallback getUserInfoFromServerResultCallback = new OkHttpClientManager.ResultCallback<String>() {
        @Override
        public void onFailure(Request request, Exception e) {
        }

        @Override
        public void onSuccess(String response) {
            try {
                JSONObject obj = FjsonUtil
                        .parseObject(response);
                if (null != obj) {
                    if (200 == obj.getIntValue("code")) {
                        userInfoBean = FjsonUtil.parseObject(obj.getString("data")
                                , XF_UserInfoBean.class);
                        setUserInfo();
                    } else {
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void setUserInfo() {
        if (null == userInfoBean) {
            return;
        }
        int progress = 0;
        int maxProgress = 0;
        String sprogress = userInfoBean.getExp();
        String slastProgress = userInfoBean.getLastexp();
        if (!TextUtils.isEmpty(sprogress)
                && TextUtils.isDigitsOnly(sprogress)
                && TextUtils.isDigitsOnly(slastProgress)) {
            try {
                progress = Integer.parseInt(sprogress);
                int lastProgress = Integer.parseInt(slastProgress);
                if (-1 == lastProgress) {
                    maxProgress = progress;
                } else {
                    maxProgress = lastProgress + progress;
                }

            } catch (Exception e) {
            }
        }

        xf_pinfo_npb.setMax(maxProgress);
        xf_pinfo_npb.setProgress(progress);
        if (!TextUtils.isEmpty(userInfoBean.getAvatar_path())) {
            SPUtil.displayImage(userInfoBean.getAvatar_path()
                    , xf_pinfo_iv_avatar
                    , DisplayOptionFactory.Avatar.options);
        }
        xf_pinfo_tv_nickname.setText(userInfoBean.getNickname());
        xf_pinfo_tv_level_alias.setText("" + userInfoBean.getAlias());
        xf_pinfo_tv_regtime.setText(getString(R.string.sgin_time) + userInfoBean.getRegtime());
    }

    private void getInfoFromServer() {
        if (null == spu.getUser()) {
            return;
        }
        String uid = spu.getUser().getUid();
        Map<String, String> params = RequestParamsUtils.getMapWithU();
        params.put("uid", uid);
        params.put("siteid", InterfaceJsonfile.SITEID);
        params.put("Page", Page + "");
        params.put("PageSize", PageSize + "");
        params.put("session", "");
        SPUtil.addParams(params);
        OkHttpClientManager.postAsyn(tag
                , InterfaceJsonfile.XF_MYCOMMENTS
                , getInfoFromServerResultCallback
                , params);
    }

    private OkHttpClientManager.ResultCallback getInfoFromServerResultCallback = new OkHttpClientManager.ResultCallback() {
        @Override
        public void onFailure(Request request, Exception e) {
            if (!mFlagRefresh) {
                app_progress_bar.setVisibility(View.GONE);
                Page--;
            }
        }

        @Override
        public void onSuccess(Object response) {
            JSONObject obj = null;
            try {
                app_progress_bar.setVisibility(View.GONE);
                obj = FjsonUtil.parseObject(response.toString());
                if (null == obj) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (200 == obj.getIntValue("code")) {
                List<MycommentsBean> mlist = FjsonUtil.parseArray(obj.getString("data"), MycommentsBean.class);
                if (null == mlist) {
                    return;
                }
                adapter.appendData(mlist, mFlagRefresh);
                adapter.notifyDataSetChanged();

                String counts = obj.getString("count");
                if (counts != null) {
//						mycomments_title.setVisibility(View.VISIBLE);
                    mycoms_text.setText(counts + " comments");
                }

            } else {
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stitle_ll_back:
                finish();
        }
    }

}

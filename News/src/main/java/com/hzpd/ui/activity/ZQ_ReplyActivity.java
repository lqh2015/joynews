package com.hzpd.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hzpd.hflt.R;
import com.hzpd.modle.ReplayBean;
import com.hzpd.modle.db.NewsBeanDB;
import com.hzpd.modle.db.NewsBeanDBDao;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.AnalyticUtils;
import com.hzpd.utils.AvoidOnClickFastUtils;
import com.hzpd.utils.EventUtils;
import com.hzpd.utils.Log;
import com.hzpd.utils.RequestParamsUtils;
import com.hzpd.utils.SPUtil;
import com.hzpd.utils.TUtils;
import com.squareup.okhttp.Request;

import java.util.Map;

public class ZQ_ReplyActivity extends MBaseActivity implements View.OnClickListener {
    @Override
    public String getAnalyticPageName() {
        return AnalyticUtils.SCREEN.comment;
    }

    @Override
    protected int getOnResumeApi() {
        return 130;
    }

    @Override
    protected int getOnStopApi() {
        return 131;
    }

    private EditText zq_reply_et_content;
    private ImageView zq_reply_share_iv;
    private TextView stitle_tv_content;
    private View stitle_ll_back;
    private ImageView iv_reply_share;
    private boolean isShare = false;
    private ReplayBean bean;
    private View loadingView;
    private RelativeLayout rl_share1;
    private View zq_reply_tv_send;
    private Object tag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zq_reply_layout);
        Intent intent = getIntent();
        if (null != intent) {
            bean = (ReplayBean) intent.getSerializableExtra("replay");
        }
        super.changeStatusBar();
        tag = OkHttpClientManager.getTag();
        initViews();

        rl_share1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShare) {
                    isShare = true;
                } else {
                    isShare = false;
                }
            }
        });

    }

    private void initViews() {
        stitle_tv_content = (TextView) findViewById(R.id.stitle_tv_content);
        stitle_tv_content.setText(R.string.comment);
        zq_reply_et_content = (EditText) findViewById(R.id.zq_reply_et_content);
        zq_reply_share_iv = (ImageView) findViewById(R.id.zq_reply_share_iv);
        zq_reply_share_iv.setOnClickListener(this);
        loadingView = findViewById(R.id.app_progress_bar);
        rl_share1 = (RelativeLayout) findViewById(R.id.rl_share1);
        stitle_ll_back = findViewById(R.id.stitle_ll_back);
        stitle_ll_back.setOnClickListener(this);
        zq_reply_tv_send = findViewById(R.id.zq_reply_tv_send);
        zq_reply_tv_send.setOnClickListener(this);
        iv_reply_share = (ImageView) findViewById(R.id.iv_reply_share);

    }

    @Override
    protected void onDestroy() {
        OkHttpClientManager.cancel(tag);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("replay", bean);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (null == bean) {
            bean = (ReplayBean) savedInstanceState.getSerializable("replay");
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    // 发表评论
    private void sendComment(final String content, final String comcount) {
        spu.getUser();
        Map<String, String> params = RequestParamsUtils.getMapWithU();
        params.put("uid", spu.getUser().getUid());
        params.put("title", bean.getTitle());
        params.put("type", bean.getType());//"News"
        params.put("nid", bean.getId());
        params.put("content", content);
        params.put("json_url", bean.getJsonUrl());
        params.put("smallimg", bean.getImgUrl());
        params.put("siteid", InterfaceJsonfile.SITEID);
        SPUtil.addParams(params);
        OkHttpClientManager.postAsyn(tag, InterfaceJsonfile.PUBLISHCOMMENT, new resultCallback(comcount), params);
    }

    public class resultCallback extends OkHttpClientManager.ResultCallback {

        resultCallback() {

        }

        private String content;

        resultCallback(String content) {
            this.content = content;
        }

        @Override
        public void onFailure(Request request, Exception e) {
            loadingView.setVisibility(View.GONE);
            TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_failure, getString(R.string.toast_server_no_response));
        }

        @Override
        public void onSuccess(Object response) {
            try {
                loadingView.setVisibility(View.GONE);
                JSONObject obj = null;
                try {
                    obj = JSONObject.parseObject(response.toString());
                } catch (Exception e) {
                    return;
                }
                if (200 == obj.getIntValue("code")) {
//                        setComcountsId(bean.getId(), comcount);
                    TUtils.ToastUpAndDown(ZQ_ReplyActivity.this, null, R.drawable.notice_succeed, getString(R.string.comment_ok));
                    Intent resultIntent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", content);
                    bundle.putBoolean("isShare", isShare);
                    resultIntent.putExtras(bundle);
                    setResult(RESULT_OK, resultIntent);
                    EventUtils.sendComment(activity);
                    finish();
                } else {
                    TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_failure, getString(R.string.toast_fail_to_comment));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void setComcountsId(String nid, String comcount) {
        Log.i("ZQ_ReplyActivity", "setComcountsId start");
        try {
            Log.i("ZQ_ReplyActivity", "setComcountsId 1");
            NewsBeanDB nbdb = dbHelper.getNewsList().queryBuilder().where(NewsBeanDBDao.Properties.Nid.eq(nid)).build().unique();
            if (nbdb != null) {
                Log.i("ZQ_ReplyActivity", "setComcountsId nbdb!=null counts:" + comcount);
                int counts = Integer.parseInt(comcount) + 1;
                nbdb.setComcount("" + counts);
                Log.i("ZQ_ReplyActivity", "setComcountsId counts:" + counts);
                dbHelper.getNewsList().update(nbdb);
            } else {
                Log.i("ZQ_ReplyActivity", "setComcountsId nbdb==null");
                nbdb.setComcount("1");
                dbHelper.getNewsList().update(nbdb);
            }
        } catch (Exception e) {
            Log.i("ZQ_ReplyActivity", "setComcountsId ");
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        if (AvoidOnClickFastUtils.isFastDoubleClick(v)) {
            return;
        }
        switch (v.getId()) {
            case R.id.stitle_ll_back:
                finish();
                break;
            case R.id.zq_reply_tv_cancle: {
                finish();
            }
            break;
            case R.id.zq_reply_tv_send: {
                String comcount = bean.getComcount();
                String comment = zq_reply_et_content.getText().toString();
                if (null == comment || "".equals(comment)) {
                    TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_failure, getString(R.string.toast_input_cannot_be_empty));
                    return;
                }
                loadingView.setVisibility(View.VISIBLE);
                sendComment(comment, comcount);
            }
            break;

            default:
                break;
        }
    }
}
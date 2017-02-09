package com.hzpd.ui.activity;

/**
 * Created by taoshuang on 2015/10/8.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.hzpd.hflt.R;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.AnalyticUtils;
import com.hzpd.utils.EventUtils;
import com.hzpd.utils.Log;
import com.hzpd.utils.RequestParamsUtils;
import com.hzpd.utils.SPUtil;
import com.hzpd.utils.TUtils;
import com.squareup.okhttp.Request;

import java.util.Map;

public class ZQ_ReplyCommentActivity extends MBaseActivity implements View.OnClickListener {

    @Override
    public String getAnalyticPageName() {
        return AnalyticUtils.SCREEN.comment;
    }

    @Override
    protected int getOnResumeApi() {
        return 136;
    }

    @Override
    protected int getOnStopApi() {
        return 137;
    }

    private EditText zq_reply_et_content;
    private View zq_reply_tv_cancle;
    private View zq_reply_tv_send;
    private String bean;
    private Object tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zq_reply_comment_layout);
        zq_reply_et_content = (EditText) findViewById(R.id.zq_reply_et_content);
        zq_reply_tv_cancle = findViewById(R.id.zq_reply_tv_cancle);
        zq_reply_tv_cancle.setOnClickListener(this);
        zq_reply_tv_send = findViewById(R.id.zq_reply_tv_send);
        zq_reply_tv_send.setOnClickListener(this);
        tag = OkHttpClientManager.getTag();
        Intent intent = getIntent();
        if (null != intent) {
            bean = intent.getStringExtra("USER_UID");
            Log.e("test", "bean-->" + bean);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zq_reply_tv_cancle: {
                finish();
            }
            break;
            case R.id.zq_reply_tv_send: {

                String comment = zq_reply_et_content.getText().toString();
                if (null == comment || "".equals(comment)) {
                    TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_failure, getString(R.string.toast_input_cannot_be_empty));
                    return;
                }
                sendComment(comment);
            }
            break;

            default:
                break;
        }
    }

    // 发表评论
    private void sendComment(String content) {
        if (spu.getUser() == null) {
            return;
        }
        String uid = spu.getUser().getUid();
        Map<String, String> params = RequestParamsUtils.getMapWithU();
        params.put("uid", uid);
        params.put("type", "2");
        params.put("nid", bean);
        params.put("content", content);
        params.put("siteid", InterfaceJsonfile.SITEID);
        SPUtil.addParams(params);
        OkHttpClientManager.postAsyn(tag
                , InterfaceJsonfile.PUBLISHCOMMENTCOMENT// InterfaceApi.mSendComment
                , resultCallback, params);
    }

    private OkHttpClientManager.ResultCallback resultCallback = new OkHttpClientManager.ResultCallback() {
        @Override
        public void onSuccess(Object response) {
            if (response == null) {
                return;
            }
            JSONObject obj = null;
            try {
                obj = JSONObject.parseObject(response.toString());
            } catch (Exception e) {
                return;
            }
            if (obj == null) {
                return;
            }
            if (200 == obj.getIntValue("code")) {
                EventUtils.sendComment(activity);
                finish();
            } else {
                TUtils.toast(getString(R.string.toast_fail_to_comment));
            }
        }

        @Override
        public void onFailure(Request request, Exception e) {
            TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_failure, getString(R.string.toast_server_no_response));
        }

    };


    @Override
    protected void onDestroy() {
        OkHttpClientManager.cancel(tag);
        super.onDestroy();
    }


}
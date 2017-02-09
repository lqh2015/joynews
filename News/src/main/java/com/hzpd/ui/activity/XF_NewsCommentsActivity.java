package com.hzpd.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.color.tools.mytools.LogUtils;
import com.hzpd.adapter.MyCommentListAdapter;
import com.hzpd.hflt.R;
import com.hzpd.modle.MyCommentListBean;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.FjsonUtil;
import com.hzpd.utils.Log;
import com.hzpd.utils.RequestParamsUtils;
import com.hzpd.utils.SPUtil;
import com.hzpd.utils.SharePreferecesUtils;
import com.hzpd.utils.TUtils;
import com.squareup.okhttp.Request;

import java.util.List;
import java.util.Map;

public class XF_NewsCommentsActivity extends MBaseActivity implements View.OnClickListener {

    private View stitle_ll_back;
    private TextView stitle_tv_content;
    private View app_progress_bar;
    private View data_empty;

    private int Page = 1;
    private static final int PageSize = 10;
    private MyCommentListAdapter myCommentListAdapter;
    private String nid;
    private RecyclerView recyclerview;

    private int lastVisibleItem;
    private LinearLayoutManager vlinearLayoutManager;
    private Object tag;

    @Override
    protected int getOnResumeApi() {
        return 134;
    }

    @Override
    protected int getOnStopApi() {
        return 135;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.newscomments_layout);
            super.changeStatusBar();
            nid = getIntent().getStringExtra("News_nid");
            Log.i("test", "nid--->" + nid);
            tag = OkHttpClientManager.getTag();
            initViews();
            getInfoFromServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        stitle_ll_back = findViewById(R.id.stitle_ll_back);
        stitle_ll_back.setOnClickListener(this);
        stitle_tv_content = (TextView) findViewById(R.id.stitle_tv_content);
        stitle_tv_content.setText(getString(R.string.comment));
        app_progress_bar = findViewById(R.id.app_progress_bar);
        data_empty = findViewById(R.id.data_empty);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        vlinearLayoutManager = new LinearLayoutManager(this);
        vlinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(vlinearLayoutManager);
        myCommentListAdapter = new MyCommentListAdapter(this, this);
        recyclerview.setAdapter(myCommentListAdapter);
        recyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == myCommentListAdapter.getItemCount()) {
                    myCommentListAdapter.setShowLoading(true);
                    getInfoFromServer();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = vlinearLayoutManager.findLastVisibleItemPosition();
            }
        });

    }


    private void getInfoFromServer() {
        try {
            Map<String, String> params = RequestParamsUtils.getMaps();
            params.put("Page", Page + "");
            params.put("PageSize", PageSize + "");
            params.put("nid", nid);
            params.put("type", "News");
            params.put("siteid", InterfaceJsonfile.SITEID);
            SPUtil.addParams(params);
            OkHttpClientManager.postAsyn(tag, InterfaceJsonfile.CHECKCOMMENT, getInfoFromServerResultCallback, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OkHttpClientManager.ResultCallback getInfoFromServerResultCallback = new OkHttpClientManager.ResultCallback() {
        @Override
        public void onSuccess(Object response) {
            Log.i("test", "onSuccess");
            app_progress_bar.setVisibility(View.GONE);
            JSONObject obj = FjsonUtil.parseObject(response.toString());
            myCommentListAdapter.setShowLoading(false);
            if (null == obj) {
                data_empty.setVisibility(View.VISIBLE);
                return;
            }

            if (200 == obj.getIntValue("code")) {
                List<MyCommentListBean> mlist = FjsonUtil.parseArray(obj.getString("data"), MyCommentListBean.class);
                if (null == mlist) {
                    data_empty.setVisibility(View.VISIBLE);
                    return;
                }
                Page++;
                Log.i("MyCommentListBean", "MyCommentListBean" + mlist.toString());
                myCommentListAdapter.appendData(mlist);
            } else {
                data_empty.setVisibility(View.VISIBLE);
                TUtils.toast(getString(R.string.toast_no_data_now));
            }
        }

        @Override
        public void onFailure(Request request, Exception e) {
            Log.i("test", "onFailure");
            data_empty.setVisibility(View.VISIBLE);
            app_progress_bar.setVisibility(View.GONE);
            myCommentListAdapter.setShowLoading(false);
        }

    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.stitle_ll_back) {
            finish();
        } else if (v.getTag() != null & v.getTag() instanceof MyCommentListAdapter.ItemViewHolder) {
            final MyCommentListAdapter.ItemViewHolder viewHolder = (MyCommentListAdapter.ItemViewHolder) v.getTag();
            Log.i("MyCommentListAdapter", "MyCommentListAdapter");
            try {
                viewHolder.up_icon.setEnabled(false);
                Log.e("holder.digNum", "holder.digNum");
                if (null == spu.getUser()) {
                    return;
                }
                Log.e("test", "点赞" + viewHolder.item.getCid());
                Log.i(getLogTag(), "uid-" + spu.getUser().getUid() + "  mType-News" + " nid-" + viewHolder.item.getCid());
                final Map<String, String> params = RequestParamsUtils.getMapWithU();
                params.put("uid", spu.getUser().getUid());
                params.put("type", "News");
                params.put("nid", viewHolder.item.getCid());
                params.put("siteid", InterfaceJsonfile.SITEID);
                SPUtil.addParams(params);
                OkHttpClientManager.postAsyn(tag
                        , InterfaceJsonfile.PRISE1//InterfaceApi.mPraise
                        , new OkHttpClientManager.ResultCallback() {
                            @Override
                            public void onSuccess(Object response) {
                                Log.d(getLogTag(), "赞-->" + response.toString());
                                JSONObject obj = JSONObject.parseObject(response.toString());
                                if (200 == obj.getInteger("code")) {
                                    Log.e("", "m---->" + viewHolder.item.getPraise());
                                    SharePreferecesUtils.setParam(XF_NewsCommentsActivity.this, "" + viewHolder.item.getCid(), "1");
                                    if (TextUtils.isDigitsOnly(viewHolder.item.getPraise())) {
                                        viewHolder.up_icon.setImageResource(R.drawable.details_icon_likeit);
                                        int i = Integer.parseInt(viewHolder.item.getPraise());
                                        i++;
                                        LogUtils.i("i---->" + i);
                                        viewHolder.comment_up_num.setText(i + "");
                                        viewHolder.item.setPraise(i + "");
                                        myCommentListAdapter.notifyDataSetChanged();
                                        viewHolder.up_icon.setEnabled(false);
                                    }
                                } else {
                                    viewHolder.up_icon.setEnabled(true);
                                }
                            }

                            @Override
                            public void onFailure(Request request, Exception e) {
                                Log.e(getLogTag(), "赞failed!");
                                TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_failure, getString(R.string.toast_server_no_response));
                                viewHolder.up_icon.setEnabled(true);
                            }
                        }, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        OkHttpClientManager.cancel(tag);
        super.onDestroy();
    }
}

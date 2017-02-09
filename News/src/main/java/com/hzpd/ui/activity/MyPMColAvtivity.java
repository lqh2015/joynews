package com.hzpd.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.color.tools.mytools.LogUtils;
import com.hzpd.adapter.CollectionAdapter;
import com.hzpd.adapter.PushmsgAdapter;
import com.hzpd.hflt.R;
import com.hzpd.modle.CollectionDataBean;
import com.hzpd.modle.CollectionJsonBean;
import com.hzpd.modle.NewsBean;
import com.hzpd.modle.VideoItemBean;
import com.hzpd.modle.db.JsonbeanDao;
import com.hzpd.modle.db.NewsItemBeanForCollection;
import com.hzpd.modle.db.NewsItemBeanForCollectionDao;
import com.hzpd.ui.App;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.AAnim;
import com.hzpd.utils.AvoidOnClickFastUtils;
import com.hzpd.utils.FjsonUtil;
import com.hzpd.utils.Log;
import com.hzpd.utils.RequestParamsUtils;
import com.hzpd.utils.SPUtil;
import com.hzpd.utils.TUtils;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author color
 *         推送和收藏页面
 */

public class MyPMColAvtivity extends MBaseActivity implements View.OnClickListener {
    @Override
    public String getAnalyticPageName() {
        if ("pushmsg".equals(type)) {
            return "我的推送页";
        } else {
            return "我的收藏页";
        }
    }

    @Override
    protected int getOnResumeApi() {
        return 120;
    }

    @Override
    protected int getOnStopApi() {
        return 121;
    }


    private TextView stitle_tv_content;
    private ListView pushmsg_lv;
    private View pushmsg_tv_empty;

    private int Page = 1;//页数
    private static final int PageSize = 100; //每页大小

    private boolean mFlagRefresh = true;//刷新还是加载

    private PushmsgAdapter pmgadapter;
    private CollectionAdapter colladAdapter;

    private String type;
    private View coverTop;
    private View stitle_ll_back;
    private Object tag;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (null == intent) {
            return;
        }
        try {
            type = intent.getStringExtra("type");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.mypushmsg_layout);
            initViews();
            tag = OkHttpClientManager.getTag();
            init();
            if (App.getInstance().getThemeName().equals("0")) {
                coverTop.setVisibility(View.GONE);
            } else {
                coverTop.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.changeStatusBar();
    }

    private void initViews() {
        stitle_ll_back = findViewById(R.id.stitle_ll_back);
        stitle_ll_back.setOnClickListener(this);
        stitle_tv_content = (TextView) findViewById(R.id.stitle_tv_content);
        pushmsg_lv = (ListView) findViewById(R.id.pushmsg_lv);
        pushmsg_tv_empty = findViewById(R.id.pushmsg_tv_empty);
        findViewById(R.id.mycomments_title).setVisibility(View.GONE);
        coverTop = findViewById(R.id.cover_top);
    }


    OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            CollectionJsonBean cb = (CollectionJsonBean) colladAdapter.getItem(position);
//            deletePop(view, cb, position);
            return true;
        }
    };


    private void init() {
        Intent intent = getIntent();
        if (null == intent) {
            return;
        }
        type = intent.getStringExtra("type");
        pushmsg_lv.setEmptyView(pushmsg_tv_empty);

        if ("pushmsg".equals(type)) {
            stitle_tv_content.setText(R.string.prompt_my_msg);
            pmgadapter = new PushmsgAdapter(this);
            pushmsg_lv.setAdapter(pmgadapter);
        } else {
            stitle_tv_content.setText(R.string.prompt_collect);
            colladAdapter = new CollectionAdapter(this);
            pushmsg_lv.setAdapter(colladAdapter);
        }


        pushmsg_lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if ("pushmsg".equals(type)) {

                } else {
                    mycollectionItemclick(parent, view, position, id);
                }
            }
        });

        pushmsg_lv.setOnItemLongClickListener(onItemLongClickListener);

        pushmsg_lv.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ("pushmsg".equals(type)) {

                } else {
                    colladAdapter.clear();
                    getDbCache();
                }
            }
        }, 500);

    }

    private void mycollectionItemclick(AdapterView<?> parent, View view,
                                       int position, long id) {

        if (AvoidOnClickFastUtils.isFastDoubleClick(view))
            return;
        CollectionJsonBean cb = (CollectionJsonBean) colladAdapter.getItem(position);
        CollectionDataBean cdb = cb.getData();
        Intent intent = new Intent();
        intent.putExtra("from", "collection");
        boolean flag = false;//是否是预定类型
        try {
            NewsItemBeanForCollection bean = dbHelper.getCollectionDBUitls().queryBuilder()
                    .where(NewsItemBeanForCollectionDao.Properties.Nid.eq(cb.getData().getId())).build().unique();
            if (bean != null) {
                cdb.setNid(bean.getNid());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //跳转脚标）1新闻  2图集  3视频 4html5
        LogUtils.i("type-->" + cb.getType());
        if ("1".equals(cb.getType())) {//
            intent.setClass(MyPMColAvtivity.this, NewsDetailActivity.class);
            NewsBean nb = new NewsBean();
            nb.setNid(cdb.getNid());
            nb.setSid("0");
            nb.setTitle(cdb.getTitle());
            nb.setJson_url(cdb.getJson_url());
            nb.setType(cb.getType());
            nb.setTid(cdb.getTid());
//            nb.setNid(cdb.getNid());
            nb.setUpdate_time(cdb.getTime());
            nb.setImgs(cdb.getImgs());
            nb.setFav(cdb.getFav());
            nb.setCopyfrom(cdb.getCopyfrom());
            nb.setComcount(cdb.getComcount());
            intent.putExtra("newbean", nb);

            flag = true;
        } else if ("2".equals(cb.getType())) {
            intent.setClass(MyPMColAvtivity.this, NewsAlbumActivity.class);
            intent.putExtra("pid", cb.getId());
            intent.putExtra("json_url", cb.getData().getJson_url());
            flag = true;
        } else if ("3".equals(cb.getType())) {
            VideoItemBean vib = new VideoItemBean(cb);
            intent.setClass(MyPMColAvtivity.this, VideoPlayerActivity.class);
            intent.putExtra("vib", vib);
            flag = true;
        } else if ("4".equals(cb.getType())) {
            intent.setClass(MyPMColAvtivity.this, NewsDetailActivity.class);
            NewsBean nb = new NewsBean();
            nb.setCopyfrom(cdb.getCopyfrom());
            nb.setFav(cdb.getFav());
            nb.setComcount(cdb.getComcount());
            nb.setNid(cdb.getNid());
            nb.setSid("0");
            nb.setComflag("0");
            nb.setTitle(cb.getData().getTitle());
            nb.setJson_url(cb.getData().getJson_url());
            nb.setType(cb.getType());
            nb.setTid(cb.getData().getTid());
            nb.setUpdate_time(cb.getData().getTime());
            nb.setImgs(cdb.getImgs());
            intent.putExtra("newbean", nb);

            flag = true;
        }

        if (flag) {
            startActivity(intent);
            AAnim.ActivityStartAnimation(activity);
        }

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!isResume) {
                return;
            }
            if (1 == msg.what) {
                List<CollectionJsonBean> list = (List<CollectionJsonBean>) msg.obj;
                colladAdapter.appendData(list, mFlagRefresh);
                colladAdapter.notifyDataSetChanged();
            }
            mFlagRefresh = false;
        }
    };


    private void getDbCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<NewsItemBeanForCollection> list = dbHelper.getCollectionDBUitls().queryBuilder()
                            .orderDesc(NewsItemBeanForCollectionDao.Properties.Id)
                            .limit(PageSize).offset((Page - 1) * PageSize)
                            .build().list();
                    if (!isResume) {
                        return;
                    }
                    if (null != list && list.size() > 0) {
                        LogUtils.i("list.size-->" + list.toString());
                        ArrayList<CollectionJsonBean> mlist = new ArrayList<CollectionJsonBean>();
                        for (NewsItemBeanForCollection nifc : list) {
                            mlist.add(nifc.getCollectionJsonBean());
                        }
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = mlist;
                        handler.sendMessage(msg);
                        pushmsg_lv.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!isResume) {
                                    return;
                                }
                            }
                        });

                    } else {
                        pushmsg_lv.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!isResume) {
                                    return;
                                }
                                getCollectionInfoFromServer();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(500);
                }

            }
        }).start();
    }

    //获取数据
    private void getCollectionInfoFromServer() {
        if (null != spu.getUser()) { //登录
            Map<String, String> params = RequestParamsUtils.getMapWithU();
            params.put("page", Page + "");
            params.put("pagesize", PageSize + "");
            params.put("siteid", InterfaceJsonfile.SITEID);
            SPUtil.addParams(params);
            OkHttpClientManager.postAsyn(tag
                    , InterfaceJsonfile.COLLECTIONLIST//InterfaceApi.collection
                    , getCollectionInfoFromServerResultCallback
                    , params
            );
        }
    }

    private OkHttpClientManager.ResultCallback getCollectionInfoFromServerResultCallback = new OkHttpClientManager.ResultCallback() {
        @Override
        public void onSuccess(Object response) {
            try {
                JSONObject obj = FjsonUtil.parseObject(response.toString());
                if (null == obj) {
                    return;
                }
                if (200 == obj.getIntValue("code")) {
                    List<CollectionJsonBean> mlist = FjsonUtil.parseArray(obj.getString("data")
                            , CollectionJsonBean.class);
                    LogUtils.e("" + mlist.toString());
                    if (null == mlist) {
                        return;
                    }

                    LogUtils.i("listsize-->" + mlist.size());

                    colladAdapter.appendData(mlist, mFlagRefresh);
                    colladAdapter.notifyDataSetChanged();

                } else {
                    if (!mFlagRefresh) {
                        Page--;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mFlagRefresh = false;
        }

        @Override
        public void onFailure(Request request, Exception e) {
            if (!mFlagRefresh) {
                Page--;
            }
            mFlagRefresh = false;
            TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_failure, getString(R.string.toast_cannot_connect_to_server));
        }
    };

    private void deletePop(View v, final CollectionJsonBean cb, final int position) {

        if (cb != null) {
            final PopupWindow mPopupWindow = new PopupWindow(this);
            LinearLayout pv = (LinearLayout) LayoutInflater.from(this).inflate(
                    R.layout.comment_delete_pop, null);
            View mTwo = (View) pv.findViewById(R.id.comment_delete_ll);//删除
            mTwo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();

                    //本地数据库获取
                    try {
                        List<NewsItemBeanForCollection> nibfc = dbHelper.getCollectionDBUitls().loadAll();
                        if (nibfc != null && nibfc.size() > 0) {

                            Log.e("", "");

                            if ("2".equals(cb.getType())) {
                                dbHelper.getJsonbeanDao().queryBuilder()
                                        .where(JsonbeanDao.Properties.Fid.eq(cb.getId()))
                                        .buildDelete().executeDeleteWithoutDetachingEntities();
                            }
                            dbHelper.getCollectionDBUitls().queryBuilder()
                                    .where(NewsItemBeanForCollectionDao.Properties.Nid.eq(cb.getData().getId()))
                                    .buildDelete().executeDeleteWithoutDetachingEntities();
                            TUtils.ToastUpAndDown(MyPMColAvtivity.this, null, R.drawable.notice_succeed, getString(R.string.toast_delete_success));
                            colladAdapter.deleteItem(position);
                            colladAdapter.notifyDataSetChanged();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        TUtils.ToastLeftAndRight(MyPMColAvtivity.this, null, R.drawable.notice_failure, getString(R.string.toast_delete_failed));
                    }

                    //网络获取
                    if (spu.getUser() != null) {
                        Map<String, String> params = RequestParamsUtils.getMapWithU();
                        params.put("id", cb.getId());
                        SPUtil.addParams(params);
                        OkHttpClientManager.postAsyn(tag
                                , InterfaceJsonfile.DELETECOLLECTION//InterfaceApi.deletecollection
                                , new deletePopResultCallback(position), params);
                    }
                }
            });
            mPopupWindow.setContentView(pv);
            mPopupWindow.setWindowLayoutMode(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
            mPopupWindow.setBackgroundDrawable(dw);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setFocusable(true);

            mPopupWindow.showAsDropDown(v,
                    v.getWidth() / 2 - 30,
                    -v.getHeight());

        }
    }

    private class deletePopResultCallback extends OkHttpClientManager.ResultCallback {
        int position;

        deletePopResultCallback(int position) {
            this.position = position;
        }

        deletePopResultCallback() {

        }

        @Override
        public void onSuccess(Object response) {
            JSONObject obj = null;
            try {
                obj = JSONObject.parseObject(response.toString());
                if (200 == obj.getIntValue("code")) {
                    TUtils.toast(getString(R.string.toast_delete_success));
                    colladAdapter.deleteItem(position);
                    colladAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                return;
            }
        }

        @Override
        public void onFailure(Request request, Exception e) {
            TUtils.ToastLeftAndRight(activity, null, R.drawable.notice_failure, getString(R.string.toast_cannot_connect_to_server));
        }
    }

    ;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stitle_ll_back:
                finish();
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(null);
        OkHttpClientManager.cancel(tag);
        super.onDestroy();
    }
}
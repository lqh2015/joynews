package com.hzpd.ui.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.hzpd.adapter.XF_UserCommentsAdapter;
import com.hzpd.hflt.R;
import com.hzpd.modle.XF_UserCommentsBean;
import com.hzpd.modle.XF_UserInfoBean;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.FjsonUtil;
import com.hzpd.utils.Log;
import com.hzpd.utils.SPUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class XF_PersonalInfoFragment extends BaseFragment {

    private XF_UserCommentsAdapter adapter;
    private String uid;
    private XF_UserInfoBean userInfoBean;

    private int page = 1;
    private static final int pagesize = 10;
    private RecyclerView recylerlist;
    private int lastVisibleItem;
    private LinearLayoutManager vlinearLayoutManager;
    private Object tag;
    private boolean isScroll = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xf_personalinfofm_layout, container, false);
        tag = OkHttpClientManager.getTag();
        recylerlist = (RecyclerView) view.findViewById(R.id.recylerlist);
        vlinearLayoutManager = new LinearLayoutManager(getActivity());
        recylerlist.setLayoutManager(vlinearLayoutManager);
        recylerlist.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isScroll && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
                    adapter.setShowLoading(true);
                    getCommentsFromServer();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = vlinearLayoutManager.findLastVisibleItemPosition();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    protected void initData() {
        Bundle bundle = getArguments();
        uid = bundle.getString("uid");
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        getUserInfoFromServer();
    }

    private void getUserInfoFromServer() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("siteid", InterfaceJsonfile.SITEID);
        SPUtil.addParams(params);
        OkHttpClientManager.postAsyn(tag
                , InterfaceJsonfile.XF_USERINFO
                , getUserInfoFromServerResultCallback
                , params);
    }

    private OkHttpClientManager.ResultCallback getUserInfoFromServerResultCallback = new OkHttpClientManager.ResultCallback() {
        @Override
        public void onSuccess(Object response) {
            try {
                if (!isAdded()) {
                    return;
                }
                String json = response.toString();
                JSONObject obj = FjsonUtil.parseObject(json);
                if (null == obj) {
                    return;
                }
                if (200 == obj.getIntValue("code")) {
                    userInfoBean = FjsonUtil.parseObject(obj.getString("data")
                            , XF_UserInfoBean.class);
                    adapter = new XF_UserCommentsAdapter(activity, userInfoBean);
                    recylerlist.setAdapter(adapter);
                    getCommentsFromServer();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Request request, Exception e) {

        }

    };


    private void getCommentsFromServer() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("siteid", InterfaceJsonfile.SITEID);
        params.put("page", page + "");
        params.put("pagesize", pagesize + "");
        SPUtil.addParams(params);
        OkHttpClientManager.postAsyn(tag
                , InterfaceJsonfile.XF_MYCOMMENTS
                , getCommentsFromServerResultCallback
                , params
        );
    }

    private OkHttpClientManager.ResultCallback getCommentsFromServerResultCallback = new OkHttpClientManager.ResultCallback() {
        @Override
        public void onSuccess(Object response) {
            try {
                String json = response.toString();
                JSONObject obj = FjsonUtil.parseObject(json);
                if (null != obj) {
                    page++;
                    if (200 == obj.getIntValue("code")) {
                        List<XF_UserCommentsBean> list = FjsonUtil.parseArray(obj.getString("data")
                                , XF_UserCommentsBean.class);
                        if (list == null || list.size() == 0) {
                            return;
                        }
                        adapter.appendData(list, false);
                        if (list.size() < 10) {
                            adapter.setShowLoading(false);
                            isScroll = false;
                        } else {
                            adapter.setShowLoading(true);
                            isScroll = true;
                        }
                    } else {
                        adapter.setShowLoading(false);
                        isScroll = false;
                    }
                } else {
                    page--;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Request request, Exception e) {
            Log.i("test", "测试 onFailure getCommentsFromServer page--");
            page--;
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OkHttpClientManager.cancel(tag);
    }

}
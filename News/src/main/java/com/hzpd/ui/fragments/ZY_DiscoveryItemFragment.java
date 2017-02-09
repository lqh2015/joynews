package com.hzpd.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.CustomSwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alibaba.fastjson.JSONObject;
import com.hzpd.adapter.DiscoveryItemNewAdapter;
import com.hzpd.hflt.R;
import com.hzpd.modle.DiscoveryItemBean;
import com.hzpd.modle.event.TagEvent;
import com.hzpd.modle.event.TagSubscriptionSuccessEvent;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.AnalyticUtils;
import com.hzpd.utils.FjsonUtil;
import com.hzpd.utils.Log;
import com.hzpd.utils.RequestParamsUtils;
import com.hzpd.utils.SPUtil;
import com.hzpd.utils.SharePreferecesUtils;
import com.news.update.Utils;
import com.squareup.okhttp.Request;

import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class ZY_DiscoveryItemFragment extends BaseFragment implements View.OnClickListener {

    final static String DISCOVERY_CATEGERY = "discovery_category";
    private RecyclerView discovery_recyclerview;
    private DiscoveryItemNewAdapter newAdapter;

    private View data_empty;
    private Button click_refresh_btn;
    private View app_progress_bar;
    private CustomSwipeRefreshLayout mSwipeRefreshWidget;

    @Override
    public String getAnalyticPageName() {
        return AnalyticUtils.SCREEN.leftMenu;
    }

    private int lastVisibleItem;
    private LinearLayoutManager vlinearLayoutManager;
    private Object tag;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        try {
            EventBus.getDefault().register(this);
            view = inflater.inflate(R.layout.zy_discovery_item_fragment, container, false);
            tag = OkHttpClientManager.getTag();
            data_empty = view.findViewById(R.id.data_empty);
            mSwipeRefreshWidget = (CustomSwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
            click_refresh_btn = (Button) view.findViewById(R.id.click_refresh_btn);
            click_refresh_btn.setOnClickListener(this);
            app_progress_bar = view.findViewById(R.id.app_progress_bar);
            discovery_recyclerview = (RecyclerView) view.findViewById(R.id.discovery_recyclerview);
            //设置布局管理器
            vlinearLayoutManager = new LinearLayoutManager(getActivity());
            vlinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            discovery_recyclerview.setLayoutManager(vlinearLayoutManager);
            newAdapter = new DiscoveryItemNewAdapter(getContext(), this);
            discovery_recyclerview.setAdapter(newAdapter);
            mSwipeRefreshWidget.setColorSchemeResources(R.color.google_blue);
            mSwipeRefreshWidget.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mSwipeRefreshWidget.setRefreshing(true);
                    Log.i("test", "ZY_DiscoveryItemFragment===onCreate");
                    refresh();
                }
            });
            discovery_recyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == newAdapter.getItemCount()) {
                        newAdapter.setShowLoading(true);

                        getDiscoveryServer();
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    lastVisibleItem = vlinearLayoutManager.findLastVisibleItemPosition();
                }
            });

            getDiscoveryServer();
        } catch (Exception e) {

        }
        return view;
    }

    private void refresh() {
        discovery_recyclerview.scrollToPosition(0);
        Page = 1;
        getDiscoveryServer();
    }

    private int Page = 1;
    private int pageSize = 10;
    private boolean isClearOld;
    private boolean isFirst;

    private void getDiscoveryServer() {
        Log.i("discovery", "discovery  getDiscoveryServer");
        if (Page == 1) {
            Log.i("discovery", "discovery  Page==1");
            String json = SharePreferecesUtils.getParam(getActivity(), DISCOVERY_CATEGERY, "").toString();
            if (!TextUtils.isEmpty(json)) {
                parseJson(json);
                isClearOld = true;
            } else {
                data_empty.setVisibility(View.VISIBLE);
                app_progress_bar.setVisibility(View.GONE);
                isFirst = true;
            }

        }
        Map<String, String> params = RequestParamsUtils.getMaps();
        params.put("Page", Page + "");
        params.put("PageSize", pageSize + "");
        SPUtil.addParams(params);
        OkHttpClientManager.postAsyn(tag
                , InterfaceJsonfile.discovery_url
                , resultCallback
                , params);
    }

    private OkHttpClientManager.ResultCallback resultCallback = new OkHttpClientManager.ResultCallback() {
        @Override
        public void onSuccess(Object response) {
            mSwipeRefreshWidget.setRefreshing(false);
            isFirst = false;
            data_empty.setVisibility(View.GONE);
            app_progress_bar.setVisibility(View.GONE);
            try {
                if (Page == 1) {
                    if (isAdded()) {
                        SharePreferecesUtils.setParam(getActivity(), DISCOVERY_CATEGERY, response.toString());
                    }
                }
                Page++;
                boolean flag = parseJson(response.toString());
                if (!flag) {
                    newAdapter.setShowLoading(false);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Request request, Exception e) {
            mSwipeRefreshWidget.setRefreshing(false);
            if (isFirst) {
                data_empty.setVisibility(View.VISIBLE);
                app_progress_bar.setVisibility(View.GONE);
            }
            newAdapter.setShowLoading(false);
        }
    };


    private boolean parseJson(String json) {
        boolean flag = false;
        if (!TextUtils.isEmpty(json)) {
            Log.i("discovery", "discovery  json!=null");
            JSONObject obj = FjsonUtil.parseObject(json);
            if (null == obj) {
                return false;
            }
            if (200 == obj.getIntValue("code")) {
                Log.i("discovery", "discovery  code==200");

                List<DiscoveryItemBean> mlist = FjsonUtil.parseArray(obj.getString("data")
                        , DiscoveryItemBean.class);
                if (null == mlist) {
                    return false;
                } else {
                    for (int i = 0; i < mlist.size(); i++) {
                        if (mlist.get(i).getNews() == null || mlist.get(i).getNews().size() < 1) {
                            DiscoveryItemBean bean = mlist.get(i);
                            mlist.remove(bean);
                            i--;
                        }
                    }
                    newAdapter.appendData(mlist, isClearOld);
                    isClearOld = false;
                    return true;
                }

            } else {
                Page--;
            }
        }
        return flag;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void onEventMainThread(TagSubscriptionSuccessEvent event) {
        newAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        OkHttpClientManager.cancel(tag);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == R.id.click_refresh_btn) {
                app_progress_bar.setVisibility(View.VISIBLE);
                data_empty.setVisibility(View.GONE);
                getDiscoveryServer();
            }
            if (v.getTag() != null && v.getTag() instanceof DiscoveryItemNewAdapter.ItemViewHolder) {
                Log.i("DiscoveryItemNewAdapter.ItemViewHolder", "DiscoveryItemNewAdapter.ItemViewHolder");
                DiscoveryItemNewAdapter.ItemViewHolder viewHolder = (DiscoveryItemNewAdapter.ItemViewHolder) v.getTag();
                {
                    Log.i("DiscoveryItemNewAdapter", "DiscoveryItemNewAdapter  viewHolder.tv_subscribe  onClick");
                    viewHolder.tv_subscribe.setBackgroundResource(R.drawable.discovery_pilihan_item_corners_bg);
                    viewHolder.tv_subscribe.setTextColor(getActivity().getResources().getColor(R.color.white));
//                    Drawable nav_up = getActivity().getResources().getDrawable(R.drawable.discovery_image_select);
//                    nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
                    viewHolder.tv_subscribe.setCompoundDrawables(null, null, null, null);
                    viewHolder.tv_subscribe.setText(getActivity().getString(R.string.discovery_followed));
                    EventBus.getDefault().post(new TagEvent(viewHolder.tagBean));
                    EventBus.getDefault().post(new TagSubscriptionSuccessEvent());
                    if (Utils.isNetworkConnected(getActivity())) {
                        Map<String, String> params = RequestParamsUtils.getMapWithU();
                        if (spu.getUser() != null) {
                            params.put("uid", spu.getUser().getUid() + "");
                        }
                        params.put("tagId", viewHolder.tagBean.getId() + "");
                        SPUtil.addParams(params);

                        OkHttpClientManager.postAsyn(tag
                                , InterfaceJsonfile.tag_click_url
                                , null
                                , params);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
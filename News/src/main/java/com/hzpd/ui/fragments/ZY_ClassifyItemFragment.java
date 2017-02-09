package com.hzpd.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSONObject;
import com.hzpd.adapter.ClassifyItemAdapter;
import com.hzpd.adapter.ClassifyItemListAdapter;
import com.hzpd.hflt.R;
import com.hzpd.modle.TagBean;
import com.hzpd.modle.event.ClassifItemEvent;
import com.hzpd.modle.event.TagEvent;
import com.hzpd.modle.event.TagSubscriptionSuccessEvent;
import com.hzpd.ui.App;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.AnalyticUtils;
import com.hzpd.utils.FjsonUtil;
import com.hzpd.utils.Log;
import com.hzpd.utils.RequestParamsUtils;
import com.hzpd.utils.SPUtil;
import com.hzpd.utils.SharePreferecesUtils;
import com.hzpd.utils.TUtils;
import com.news.update.Utils;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class ZY_ClassifyItemFragment extends BaseFragment implements View.OnClickListener {

    @Override
    public String getAnalyticPageName() {
        return AnalyticUtils.SCREEN.leftMenu;
    }

    private RecyclerView hRecyclerView;
    private ClassifyItemAdapter adapter;

    private RecyclerView vRecyclerView;
    private ClassifyItemListAdapter sampleAdapter;

    private int Page = 1;
    private int pageSize = 10;
    private int lastVisibleItem;
    private FrameLayout progress_container;
    private LinearLayoutManager vlinearLayoutManager;
    private Object tag;
    private View data_empty;
    private Button click_refresh_btn;
    private View app_progress_bar;
    boolean addLoading = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
        }
        View view = null;
        try {
            view = inflater.inflate(R.layout.zy_classify_item_fragment, container, false);
            data_empty = view.findViewById(R.id.data_empty);
            click_refresh_btn = (Button) view.findViewById(R.id.click_refresh_btn);
            click_refresh_btn.setOnClickListener(this);
            app_progress_bar = view.findViewById(R.id.app_progress_bar);
            tag = OkHttpClientManager.getTag();
            progress_container = (FrameLayout) view.findViewById(R.id.progress_container);
            hRecyclerView = (RecyclerView) view.findViewById(R.id.id_recyclerview_horizontal);
            //设置布局管理器
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            hRecyclerView.setLayoutManager(linearLayoutManager);
            adapter = new ClassifyItemAdapter(getActivity(), this);
            //设置适配器
            hRecyclerView.setAdapter(adapter);
            hRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (changing) {
                            changing = false;
                            first = 0;
                            return;
                        }
                        try {
                            int position = 0;
                            do {
                                View view = recyclerView.getChildAt(position);
                                if (view == null) {
                                    return;
                                }
                                Object tag = view.getTag();
                                if (tag != null) {
                                    ClassifyItemAdapter.ItemViewHolder holder = (ClassifyItemAdapter.ItemViewHolder) tag;
                                    if (first == 0) {
                                        first = holder.postion;
                                    }
                                    DisplayMetrics displayMetrics = App.getInstance().getResources().getDisplayMetrics();
                                    int target = displayMetrics.widthPixels / 2;
                                    if (view.getLeft() < target && view.getRight() > target) {
                                        holder.classify_item.performClick();
                                    }
                                }
                                ++position;
                            } while (position < adapter.getItemCount());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });

            vRecyclerView = (RecyclerView) view.findViewById(R.id.id_recyclerview_vertical);
            //设置布局管理器
            vlinearLayoutManager = new LinearLayoutManager(getActivity());
            vlinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            vRecyclerView.setLayoutManager(vlinearLayoutManager);
            sampleAdapter = new ClassifyItemListAdapter(getActivity(), this);
            //设置适配器
            vRecyclerView.setAdapter(sampleAdapter);

            vRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (addLoading && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == sampleAdapter.getItemCount()) {
                        addLoading = false;
                        sampleAdapter.setShowLoading(true);
                        vPage++;
                        getClassifyVerServer(id);
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    lastVisibleItem = vlinearLayoutManager.findLastVisibleItemPosition();
                }
            });

            String json = (String) SharePreferecesUtils.getParam(getActivity(), KEY_CATEGERY, "");
            if (!TextUtils.isEmpty(json)) {
                parseJson(json, false);
            }
            getClassifyHorServer();
            app_progress_bar.setVisibility(View.GONE);
            data_empty.setVisibility(View.VISIBLE);

        } catch (Exception e) {

        }
        return view;
    }


    private boolean isFirst;
    final static String KEY_CATEGERY = "tag_category";

    /**
     * 获取水平数据
     */
    private void getClassifyHorServer() {
        if (!Utils.isNetworkConnected(App.getInstance())) {
            app_progress_bar.setVisibility(View.GONE);
            if (adapter.list == null || adapter.list.size() == 0) {
                data_empty.setVisibility(View.VISIBLE);
            }
            if (isVisible) {
                TUtils.toast(getString(R.string.toast_check_network));
            }
            return;
        }

        final Map<String, String> params = RequestParamsUtils.getMaps();
        params.put("Page", String.valueOf(Page));
        params.put("PageSize", String.valueOf(pageSize));
        SPUtil.addParams(params);
        OkHttpClientManager.postAsyn(tag
                , InterfaceJsonfile.classify_top_url
                , getClassifyHorServerResultCallback
                , params);
    }

    private OkHttpClientManager.ResultCallback getClassifyHorServerResultCallback = new OkHttpClientManager.ResultCallback() {
        @Override
        public void onSuccess(Object response) {
            isFirst = false;
            data_empty.setVisibility(View.GONE);
            app_progress_bar.setVisibility(View.GONE);
            try {
                parseJson(response.toString(), true);
                if (isAdded()) {
                    SharePreferecesUtils.setParam(getActivity(), KEY_CATEGERY, response.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Request request, Exception e) {
            if (isFirst) {
                app_progress_bar.setVisibility(View.GONE);
                data_empty.setVisibility(View.VISIBLE);
            }
        }
    };


    public void parseJson(String json, boolean isCache) {
        Log.e("ZY_ClassifyItemFragment", "parseJsonError：" + isCache);
        try {
            JSONObject obj = FjsonUtil.parseObject(json);
            if (null == obj) {
                return;
            }
            if (200 == obj.getIntValue("code")) {
                List<TagBean> mlist = FjsonUtil.parseArray(obj.getString("data")
                        , TagBean.class);
                if (null == mlist) {
                    return;
                }
                adapter.appendData(mlist);
                id = mlist.get(0).getId();
                if (isCache) {
                    getClassifyVerServer(id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //categoryId , int , 必选 ，  tag大类ID
    //page , int ,可选， 页码，默认值：1
    //pageSize , int ，可选， 每页数量，默认值：10
    private int vPage = 1;
    private int tpageSize = 10;
    private boolean isClearOld = true;

    private void getClassifyVerServer(final String id) {
        if (!Utils.isNetworkConnected(getActivity())) {
            if (vPage == 1) {
                isClearOld = true;
                String json = (String) SharePreferecesUtils.getParam(getActivity(), KEY_CATEGERY + id, "");
                parseJsonTag(json);
                releaseHandler();
            }
            if (isVisible) {
                TUtils.toast(getString(R.string.toast_check_network));
            }
            return;
        }

        Map<String, String> params = RequestParamsUtils.getMaps();
        params.put("categoryId", id);
        params.put("Page", String.valueOf(vPage));
        params.put("PageSize", String.valueOf(tpageSize));
        SPUtil.addParams(params);
        OkHttpClientManager.postAsyn(tag
                , InterfaceJsonfile.classify_url
                , getClassifyVerServerResultCallback
                , params);
    }

    private OkHttpClientManager.ResultCallback getClassifyVerServerResultCallback = new OkHttpClientManager.ResultCallback() {
        @Override
        public void onSuccess(Object response) {
            try {
                progress_container.setVisibility(View.GONE);
                boolean flag = parseJsonTag(response.toString());
                if (!flag) {
                    sampleAdapter.setShowLoading(false);
                }
                if (vPage == 1) {
                    if (isAdded()) {
                        SharePreferecesUtils.setParam(getActivity(), KEY_CATEGERY + id, response.toString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Request request, Exception e) {
            sampleAdapter.setShowLoading(false);
        }

    };

    public boolean parseJsonTag(String json) {
        boolean flag = false;
        try {
            if (!TextUtils.isEmpty(json)) {
                JSONObject obj = FjsonUtil.parseObject(json);
                if (null == obj) {
                    addLoading = false;
                    return false;
                }
                if (200 == obj.getIntValue("code")) {
                    addLoading = true;
                    List<TagBean> mlist = FjsonUtil.parseArray(obj.getString("data"), TagBean.class);
                    Log.i("List<TagBean>", "List<TagBean>=" + mlist.size());
                    if (null != mlist && mlist.size() > 0) {
                        progress_container.setVisibility(View.GONE);
                        sampleAdapter.appendData(mlist, isClearOld);
                        isClearOld = false;
                        sampleAdapter.setShowLoading(false);
                        if (mlist.size() < 9) {
                            sampleAdapter.setShowLoading(false);
                            addLoading = false;
                        } else {
                            sampleAdapter.setShowLoading(true);
                        }
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private String id;

    public void onEventMainThread(ClassifItemEvent event) {
        id = event.getId();
        isClearOld = true;
        vPage = 1;
        progress_container.setVisibility(View.VISIBLE);
        releaseHandler();
        getClassifyVerServer(id);
    }

    public void releaseHandler() {
    }

    public void onEventMainThread(TagSubscriptionSuccessEvent event) {
        sampleAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroy() {
        try {
            EventBus.getDefault().unregister(this);
            super.onDestroy();
        } catch (Exception e) {
        }
    }

    boolean changing = false;
    int first = 0;

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == R.id.click_refresh_btn) {
                app_progress_bar.setVisibility(View.VISIBLE);
                getClassifyHorServer();
            }
            if (v.getTag() != null & v.getTag() instanceof ClassifyItemAdapter.ItemViewHolder) {
                ClassifyItemAdapter.ItemViewHolder holder = (ClassifyItemAdapter.ItemViewHolder) v.getTag();
                if (v.isSelected()) {
                    moveHead(holder);
                    return;
                }
                if (adapter.last != null) {
                    adapter.last.classify_item.setSelected(false);
                    adapter.last.mImg.setLayoutParams(SPUtil.NORMAL);
                }
                v.setSelected(true);
                holder.mImg.setLayoutParams(SPUtil.LARGE);
                moveHead(holder);
                EventBus.getDefault().post(new ClassifItemEvent("" + holder.tagBean.getId()));
            } else if (v.getTag() != null & v.getTag() instanceof ClassifyItemListAdapter.ItemViewHolder) {
                ClassifyItemListAdapter.ItemViewHolder viewHolder = (ClassifyItemListAdapter.ItemViewHolder) v.getTag();
                Log.i("ZY_ClassifyItemFragment", "订阅点击");
                viewHolder.tv_subscribe.setBackgroundResource(R.drawable.discovery_pilihan_item_corners_bg);
                viewHolder.tv_subscribe.setTextColor(getActivity().getResources().getColor(R.color.white));
//                Drawable nav_up = getActivity().getResources().getDrawable(R.drawable.discovery_image_select);
//                nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
                viewHolder.tv_subscribe.setCompoundDrawables(null, null, null, null);
                viewHolder.tv_subscribe.setText(getActivity().getString(R.string.discovery_followed));
                EventBus.getDefault().post(new TagEvent(viewHolder.tagBean));
                EventBus.getDefault().post(new TagSubscriptionSuccessEvent());
                if (Utils.isNetworkConnected(getActivity())) {
                    Map<String, String> params = new HashMap<>();
                    if (spu.getUser() != null) {
                        params.put("uid", spu.getUser().getUid() + "");
                    }
                    params.put("tagId", viewHolder.tagBean.getId() + "");
                    SPUtil.addParams(params);

                    OkHttpClientManager.postAsyn(tag, InterfaceJsonfile.tag_click_url, null, params);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getTarget(ClassifyItemAdapter.ItemViewHolder holder) {
        changing = true;
        DisplayMetrics displayMetrics = App.getInstance().getResources().getDisplayMetrics();
        int itemWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 95, displayMetrics);
        int target = (displayMetrics.widthPixels - itemWidth) / 2;

        if (adapter.last.postion >= first && holder.postion > adapter.last.postion) {
            target += (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, displayMetrics);
        }
        return target;
    }

    private void moveHead(ClassifyItemAdapter.ItemViewHolder holder) {
        int target = getTarget(holder);
        int left = holder.itemView.getLeft();
        adapter.last = holder;
        hRecyclerView.smoothScrollBy(left - target, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OkHttpClientManager.cancel(tag);
    }
}
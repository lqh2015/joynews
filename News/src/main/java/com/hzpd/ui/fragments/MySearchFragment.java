package com.hzpd.ui.fragments;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hzpd.adapter.NewsItemListViewAdapter;
import com.hzpd.hflt.R;
import com.hzpd.modle.NewsBean;
import com.hzpd.ui.App;
import com.hzpd.ui.activity.NewsAlbumActivity;
import com.hzpd.ui.activity.NewsDetailActivity;
import com.hzpd.ui.activity.ZhuanTiActivity;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.AAnim;
import com.hzpd.utils.AvoidOnClickFastUtils;
import com.hzpd.utils.FjsonUtil;
import com.hzpd.utils.Log;
import com.hzpd.utils.RequestParamsUtils;
import com.hzpd.utils.SPUtil;
import com.news.update.Utils;
import com.squareup.okhttp.Request;

import java.util.List;
import java.util.Map;

public class MySearchFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerView recyclerView;

    private NewsItemListViewAdapter adapter;
    private View loadingView;

    private boolean isRefresh = false;
    private int page = 1;
    private static final int pageSize = 15;

    public static final String SEARCH_KEY = "search_key";
    public static final String is_Refresh = "is_Refresh";
    String con;

    private Object tag;
    private View background_empty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.search_main_layout, container, false);
        recyclerView = (RecyclerView) mView.findViewById(R.id.search_listview_id);
        loadingView = mView.findViewById(R.id.app_progress_bar);
        background_empty = mView.findViewById(R.id.background_empty);
        tag = OkHttpClientManager.getTag();
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    RecyclerView.ItemDecoration itemDecoration = new MyItemDecoration();

    int padding = 20;

    class MyItemDecoration extends RecyclerView.ItemDecoration {
        Paint mPaint;

        MyItemDecoration() {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(0xfff5f5f5);
        }

        @Override
        public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
            final int childSize = parent.getChildCount();
            for (int i = 0; i < childSize; i++) {
                final View child = parent.getChildAt(i);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int top = child.getBottom() + layoutParams.bottomMargin;
                final int bottom = top + padding;
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, 0, 0, padding);
        }
    }

    private LinearLayoutManager layoutManager;
    NewsItemListViewAdapter.CallBack callBack;
    boolean addLoading = false;

    private void init() {
        con = getArguments().getString(SEARCH_KEY);
        isRefresh = getArguments().getBoolean(is_Refresh);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NewsItemListViewAdapter(activity, this, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (addLoading && !adapter.showLoading) {
                    addLoading = false;
                    int count = adapter.getItemCount();
                    adapter.showLoading = true;
                    adapter.notifyItemInserted(count);
                }
            }
        });

        callBack = new NewsItemListViewAdapter.CallBack() {
            @Override
            public void loadMore() {
                Log.i("loadMore", "loadMore");
                getSearchData(con);
            }
        };
        adapter.callBack = callBack;

//        recyclerView.addItemDecoration(itemDecoration);
        padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        getSearchData(con);
    }

    public void reSearch(String content) {
        page = 1;
        adapter.clear();
        loadingView.setVisibility(View.VISIBLE);
        getSearchData(content);
    }

    public void getSearchData(String content) {
        if (!Utils.isNetworkConnected(App.getInstance())) {
            if (adapter.list == null && adapter.list.size() == 0) {
                background_empty.setVisibility(View.VISIBLE);
            }
            return;
        }
        con = content;
        Map<String, String> params = RequestParamsUtils.getMaps();
        params.put("siteid", InterfaceJsonfile.SITEID);
        params.put("content", content);
        params.put("Page", "" + page);
        params.put("PageSize", "" + pageSize);
        SPUtil.addParams(params);
        OkHttpClientManager.postAsyn(tag
                , InterfaceJsonfile.SEARCH
                , resultCallback
                , params);

    }

    private OkHttpClientManager.ResultCallback resultCallback = new OkHttpClientManager.ResultCallback() {
        @Override
        public void onSuccess(Object response) {
            try {
                if (!isAdded()) {
                    return;
                }
                JSONObject obj = FjsonUtil.parseObject(response.toString());
                if (null == obj) {
                    if (adapter.list == null && adapter.list.size() == 0) {
                        background_empty.setVisibility(View.VISIBLE);
                    }
                    loadingView.setVisibility(View.GONE);
                    return;
                }
                if (200 == obj.getIntValue("code")) {
                    loadingView.setVisibility(View.GONE);
                    page++;
                    addLoading = true;
                    List<NewsBean> l = FjsonUtil.parseArray(
                            obj.getString("data"), NewsBean.class);
                    if (null == l || l.size() == 0) {
                        if (adapter.list == null && adapter.list.size() == 0) {
                            background_empty.setVisibility(View.VISIBLE);
                        }
                        return;
                    }
                    adapter.checkList(l);
                    adapter.appendData(l, false, false);
                    if (page > 1 && adapter.showLoading) {
                        int count = adapter.getItemCount();
                        adapter.showLoading = false;
                        adapter.notifyItemRemoved(count);
                    }
                    if (l.size() < 14) {
                        adapter.showLoading = false;
                        addLoading = false;
                    } else {
                        adapter.showLoading = true;
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    loadingView.setVisibility(View.GONE);
                    background_empty.setVisibility(View.VISIBLE);
                    if (!isRefresh) {
                        page--;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Request request, Exception e) {
            if (!isAdded()) {
                return;
            }
            if (adapter.list == null && adapter.list.size() == 0) {
                background_empty.setVisibility(View.VISIBLE);
            }
            adapter.showLoading = false;
            loadingView.setVisibility(View.GONE);
            if (!isRefresh) {
                page--;
            }
        }
    };

    @Override
    public void onDestroyView() {
        OkHttpClientManager.cancel(tag);
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        if (AvoidOnClickFastUtils.isFastDoubleClick(view)) {
            return;
        }
        TextView title = (TextView) view.findViewById(R.id.newsitem_title);
        if (null != title) {
            title.setTextColor(getResources().getColor(R.color.grey_font));
        }

        NewsBean nb = (NewsBean) view.getTag();
        Intent mIntent = new Intent();
        mIntent.putExtra("newbean", nb);
        mIntent.putExtra("from", "newsitem");
        adapter.setReadedId(nb);
        ////////////////////////////
        //1新闻  2图集  3直播 4专题  5关联新闻 6视频
        if ("1".equals(nb.getRtype())) {
            mIntent.setClass(getActivity(), NewsDetailActivity.class);
        } else if ("2".equals(nb.getRtype())) {
            mIntent.setClass(getActivity(), NewsAlbumActivity.class);
        } else if ("3".equals(nb.getRtype())) {
            mIntent.setClass(getActivity(), NewsDetailActivity.class);//直播界面
        } else if ("4".equals(nb.getRtype())) {
            mIntent.setClass(getActivity(), ZhuanTiActivity.class);
        } else if ("5".equals(nb.getRtype())) {
            mIntent.setClass(getActivity(), NewsDetailActivity.class);
        } else if ("6".equals(nb.getRtype())) {
//					mIntent.setClass(getActivity(),VideoPlayerActivity.class);
            mIntent.setClass(getActivity(), NewsDetailActivity.class);
        } else if ("7".equals(nb.getRtype())) {
            mIntent.setClass(getActivity(), NewsDetailActivity.class);
        } else {
            return;
        }

        activity.startActivityForResult(mIntent, 0);
        AAnim.ActivityStartAnimation(getActivity());
    }

}
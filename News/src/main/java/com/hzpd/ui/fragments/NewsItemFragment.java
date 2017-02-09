package com.hzpd.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.CustomSwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hzpd.adapter.NewsItemListViewAdapter;
import com.hzpd.hflt.BuildConfig;
import com.hzpd.hflt.R;
import com.hzpd.modle.NewsBean;
import com.hzpd.modle.NewsChannelBean;
import com.hzpd.modle.db.NewsBeanDB;
import com.hzpd.modle.event.FontSizeEvent;
import com.hzpd.modle.event.RefreshEvent;
import com.hzpd.network.LoadImageEvent;
import com.hzpd.ui.App;
import com.hzpd.ui.ConfigBean;
import com.hzpd.ui.activity.NewsAlbumActivity;
import com.hzpd.ui.activity.NewsDetailActivity;
import com.hzpd.ui.activity.VideoPlayerActivity;
import com.hzpd.ui.activity.ZhuanTiActivity;
import com.hzpd.ui.interfaces.I_Control;
import com.hzpd.ui.interfaces.I_SetList;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.AAnim;
import com.hzpd.utils.AnalyticUtils;
import com.hzpd.utils.AvoidOnClickFastUtils;
import com.hzpd.utils.FjsonUtil;
import com.hzpd.utils.Log;
import com.hzpd.utils.RequestParamsUtils;
import com.hzpd.utils.SPUtil;
import com.hzpd.utils.TUtils;
import com.hzpd.utils.db.NewsListDbTask;
import com.news.update.Utils;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class NewsItemFragment extends BaseFragment implements I_Control, View.OnClickListener {
    public final static String PREFIX = "C:";

    @Override
    public String getAnalyticPageName() {
        return null;
    }

    private boolean isVisibleCurrent;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i("NewsItemFragment", "订阅 isVisibleToUser::::" + isVisibleToUser);
        isVisibleCurrent = isVisibleToUser;
    }

    private NewsItemListViewAdapter adapter;
    private NewsChannelBean channelbean;//本频道
    private String newsItemPath;//本频道根目录flash
    private int page = 1;
    private int lastPage = 1; // 记录之前的
    private static final int pageSize = 15;//
    private NewsListDbTask newsListDbTask; //新闻列表数据库
    // 是否刷新最新数据
    private boolean mFlagRefresh = true;
    private boolean isRefresh = true;//是否首次加载
    private boolean isNeedRefresh = true;
    private int position = -1;
    private TextView update_counts;

    private CustomSwipeRefreshLayout mSwipeRefreshWidget;
    private RecyclerView mRecyclerView;

    NewsItemListViewAdapter.CallBack callBack;

    public NewsItemFragment() {

    }

    @SuppressLint("ValidFragment")
    public NewsItemFragment(NewsChannelBean channelbean) {
        this.channelbean = channelbean;
        setTitle(channelbean.getCnname());
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsItemPath = App.getInstance().getJsonFileCacheRootDir();

        newsListDbTask = new NewsListDbTask(activity);
    }


    public String getTitle() {
        return channelbean.getCnname();
    }

    RecyclerView.LayoutManager layoutManager;
    private View background_empty;

    boolean addLoading = false;

    private boolean isRefreshCounts = true;
    boolean pullRefresh = false;
    private Object tag;

    private View app_progress_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        lastPage = page;
        page = 1;
        isRefresh = true;
        View view = inflater.inflate(R.layout.news_channel_fragment, container, false);
        view.findViewById(R.id.main_top_layout).setVisibility(View.GONE);
        app_progress_bar = view.findViewById(R.id.app_progress_bar);
        app_progress_bar.setVisibility(View.VISIBLE);
//        if (!ConfigBean.getInstance().open_channel.contains(SPUtil.getCountry())) {
//            view.findViewById(R.id.main_top_layout).setVisibility(View.VISIBLE);
//            View main_top_search = view.findViewById(R.id.main_top_search);
//            main_top_search.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (AvoidOnClickFastUtils.isFastDoubleClick(v))
//                        return;
//                    Intent mIntent = new Intent();
//                    mIntent.setClass(getActivity(), SearchActivity.class);
//                    startActivity(mIntent);
//                    AAnim.ActivityStartAnimation(getActivity());
//                }
//            });
//        }
        background_empty = view.findViewById(R.id.background_empty);
        update_counts = (TextView) view.findViewById(R.id.update_counts);
        background_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeRefreshWidget.setRefreshing(true);
                refresh();
            }
        });
        mSwipeRefreshWidget = (CustomSwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recylerlist);

        tag = OkHttpClientManager.getTag();

        mSwipeRefreshWidget.setColorSchemeResources(R.color.google_blue);

        mSwipeRefreshWidget.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshWidget.setRefreshing(true);
                Log.i("NewsItemFragment", "更新 上拉刷新");
//                isRefreshCounts=true;
//                adapter.showLoading = false;
                refresh();
            }
        });
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //解决RecyclerView和SwipeRefreshLayout共用存在的bug
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mSwipeRefreshWidget.setEnabled(topRowVerticalPosition >= 0);

                if (addLoading && !adapter.showLoading) {
                    addLoading = false;
                    int count = adapter.getItemCount();
                    adapter.showLoading = true;
                    adapter.notifyItemInserted(count);
                }
            }
        });
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        layoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new NewsItemListViewAdapter(activity, this, true);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.getItemAnimator().setRemoveDuration(800);
        callBack = new NewsItemListViewAdapter.CallBack() {
            @Override
            public void loadMore() {
                Log.i("NewsItemFragment", "更新 下拉加载");
                isRefreshCounts = false;
                getServerList("loadMore");
            }
        };
        adapter.callBack = callBack;
        return view;
    }

    private void refresh() {
        mRecyclerView.scrollToPosition(0);
        pullRefresh = true;
        lastPage = page;
        page = 1;
        isRefreshCounts = true; //TODO hide
        mFlagRefresh = true;
        getFlash();
        getServerList("refresh");
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        lastPage = page;
        page = 1;
        mFlagRefresh = true;
        firstLoading = false;
        if (!ConfigBean.getInstance().open_channel.contains(SPUtil.getCountry())) {
            loadData();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        Log.e("onDestroyView", "onDestroyView " + getAnalyticPageName());
        OkHttpClientManager.cancel(tag);
        EventBus.getDefault().unregister(this);
        isLoading = false;
        adapter.appendData(null, true, false);
        mRecyclerView.setAdapter(null);
        mRecyclerView.removeAllViews();
        mRecyclerView = null;
        adapter = null;
        super.onDestroyView();
    }

    boolean firstLoading = false;

    public void loadData() {
        if (firstLoading) {
            return;
        }
        if (page == 1 && mRecyclerView != null) {
            firstLoading = true;
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getFlash();
                    getDbList();
                    isRefresh = true;
                }
            }, 200);
        }
    }

//    private String firstNid;

    //新闻列表
    @Override
    public void getDbList() {
        try {
            newsListDbTask.findList(channelbean, page, pageSize, new I_SetList<NewsBeanDB>() {
                @Override
                public void setList(List<NewsBeanDB> list) {
                    final long start = System.currentTimeMillis();
                    if (null != list && list.size() > 5) {
                        app_progress_bar.setVisibility(View.GONE);
                        List<NewsBean> nbList = new ArrayList<>();
                        for (NewsBeanDB nbdb : list) {
                            NewsBean newsBean = nbdb.getNewsBean();
                            newsBean.setCnname(channelbean.getCnname());
                            nbList.add(newsBean);
                            background_empty.setVisibility(View.GONE);
                        }
                        //                    firstNid = nbList.get(0).getNid();
                        addLoading = true;
                        if (nbList != null && adapter != null) {
                            adapter.appendData(nbList, mFlagRefresh, true);
                        }
                        Log.e("test", "News:getDbList  3=" + (System.currentTimeMillis() - start));
                    }
                    getServerList("getDbList");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean isLoading = false;

    //获取新闻list
    @Override
    public void getServerList(String nids) {
        if (isLoading || channelbean == null) {
            return;
        }
        if (adapter == null) {
            return;
        }
        if (!Utils.isNetworkConnected(App.getInstance())) {
            showEmpty();
            if (App.getInstance().getIsVisible() && isVisible) {
                TUtils.toast(getString(R.string.toast_check_network));
            }
            return;
        }
        Map<String, String> params = RequestParamsUtils.getMaps();
        params.put("siteid", InterfaceJsonfile.SITEID);
        params.put("tid", channelbean.getTid());
        params.put("tagId", channelbean.getId());
        params.put("Page", "" + page);
        params.put("PageSize", "" + pageSize);
        if (page == 1 && adapter.getItemCount() > 3) {
            String newTimew = App.getInstance().newTimeMap.get(channelbean.toString());
            newTimew = newTimew == null ? "" : newTimew;
            params.put("newTime", newTimew);
        }
        SPUtil.addCallback(params, channelbean.gerKey());
        isLoading = true;
        Log.i("NewsFragment","参数   &tid="+params.get("tid")+"&siteid="+params.get("siteid")+"&tagId="
                +params.get("tagId")+"&country="+params.get("country")+"&Page="+params.get("Page")+"&PageSize="+params.get("PageSize"));
        OkHttpClientManager.postAsyn(tag
                , InterfaceJsonfile.NEWSLIST
                , resultCallback
                , params);
    }

    private OkHttpClientManager.ResultCallback resultCallback = new OkHttpClientManager.ResultCallback() {
        @Override
        public void onSuccess(Object response) {
            app_progress_bar.setVisibility(View.GONE);
            try {
                isLoading = false;
                mSwipeRefreshWidget.setRefreshing(false);
                final JSONObject obj = FjsonUtil.parseObject(response.toString());
                if (null != obj) {
                    Log.i("NewsItemFragment", "show obj=" + obj);
                    if (!TextUtils.isEmpty(obj.getString(SPUtil.CALLBACK))) {
                        SPUtil.setGlobal(SPUtil.CALLBACK + channelbean.gerKey(), obj.getString(SPUtil.CALLBACK));
                    }
                    //缓存更新
                    setData(obj);

                    page++;
                }
                if (adapter.getItemCount() < 2) {
                    showEmpty();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Request request, Exception e) {
            showEmpty();
            if (App.getInstance().getIsVisible() && isVisibleCurrent) {
                TUtils.toast(getString(R.string.toast_cannot_connect_network));
            }
            AnalyticUtils.sendGaEvent(getActivity(), AnalyticUtils.ACTION.networkErrorOnList, null, null, 0L);
            AnalyticUtils.sendUmengEvent(getActivity(), AnalyticUtils.ACTION.networkErrorOnList);
        }
    };

    private void showEmpty() {
        try {
            if (!isAdded()) {
                return;
            }
            if (adapter.list == null || adapter.list.size() == 0) {
                background_empty.setVisibility(View.VISIBLE);
            }
            isLoading = false;
            pullRefresh = false;
            app_progress_bar.setVisibility(View.GONE);
            mSwipeRefreshWidget.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean isSame = true;

    //服务端返回数据处理
    @Override
    public void setData(JSONObject obj) {
        //数据处理
        switch (obj.getIntValue("code")) {
            case 200: {
                List<NewsBean> list = FjsonUtil.parseArray(obj.getString("data"), NewsBean.class);
                if (null != list && list.size() > 0) {
                    for (NewsBean bean : list) {
                        if (BuildConfig.DEBUG) {

                        }

                        bean.setTagId(channelbean.getId());
                        bean.setCnname(channelbean.getCnname());
                    }

                    Log.i("NewsFragment", "列表List.size()==" + list.size());
                    newsListDbTask.saveList(list, null);
                    if (list.size() > 0 && page > 1) {
                        adapter.checkList(list);
                    }
                    if (isRefreshCounts) {
                        update_counts.setVisibility(View.VISIBLE);
                        update_counts.setText(String.format(getString(R.string.update_counts), list.size()));
                        update_counts.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                update_counts.setVisibility(View.GONE);
                            }
                        }, 2000);
                    }

                    if (page == 1) {
                        adapter.removeOld();
                    }
                    if (list.size() >= 7) {
                        adapter.showLoading = true;
                    } else {
                        mRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addLoading = true;
                            }
                        }, 2000);
                        adapter.hidLoading();
                        addLoading = false;
                    }
                    adapter.appendData(list, mFlagRefresh, false);
                    background_empty.setVisibility(View.GONE);
                    if (page == 1) {
                        App.getInstance().newTimeMap.put(channelbean.toString(), obj.getString("newTime"));
                    }
                }

            }
            break;
            default: {
                if (page > 1 && adapter.showLoading) {
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addLoading = true;
                        }
                    }, 2000);
                    adapter.hidLoading();
                    addLoading = false;
                }
                if (pullRefresh) {
                    page = --lastPage;
                    if (isVisible) {
                        update_counts.setVisibility(View.VISIBLE);
                        update_counts.setText(getString(R.string.pull_to_refresh_reached_end));
                        update_counts.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                update_counts.setVisibility(View.GONE);
                            }
                        }, 2000);
                    }
                }
            }
            break;
        }
        pullRefresh = false;
        mFlagRefresh = false;
    }

    //获取幻灯
    private void getFlash() {
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onEventMainThread(FontSizeEvent event) {
        adapter.setFontSize(event.getFontSize());
    }

    public void onEventMainThread(LoadImageEvent event) {
        if (isAdded() && adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void onEventMainThread(RefreshEvent event) {
        if (event.force || isVisible) {
            mSwipeRefreshWidget.setRefreshing(true);
            refresh();
        }
    }




    //点击操作
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
        //1新闻  2图集  3直播 4专题  5关联新闻 6视频
//TODO 视频新闻
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
            mIntent.setClass(getActivity(), VideoPlayerActivity.class);
        } else if ("7".equals(nb.getRtype())) {
            mIntent.setClass(getActivity(), NewsDetailActivity.class);
        } else {
            return;
        }

        activity.startActivityForResult(mIntent, 0);
        AAnim.ActivityStartAnimation(getActivity());
    }

}

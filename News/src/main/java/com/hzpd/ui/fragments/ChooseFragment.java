package com.hzpd.ui.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.CustomSwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hzpd.adapter.NewsItemListViewAdapter;
import com.hzpd.hflt.R;
import com.hzpd.modle.NewsBean;
import com.hzpd.modle.NewsChannelBean;
import com.hzpd.modle.db.NewsBeanDB;
import com.hzpd.modle.event.FontSizeEvent;
import com.hzpd.modle.event.RefreshEvent;
import com.hzpd.network.LoadImageEvent;
import com.hzpd.ui.App;
import com.hzpd.ui.activity.NewsAlbumActivity;
import com.hzpd.ui.activity.NewsDetailActivity;
import com.hzpd.ui.activity.VideoPlayerActivity;
import com.hzpd.ui.activity.ZhuanTiActivity;
import com.hzpd.ui.interfaces.I_SetList;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.AAnim;
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

/**
 * 推荐频道
 */
public class ChooseFragment extends BaseFragment implements View.OnClickListener {


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i("NewsItemFragment", "订阅 isVisibleToUser:" + "::::" + isVisibleToUser);
    }

    public final static String PREFIX = "C:";

    private NewsChannelBean channelbean;//本频道
    private NewsListDbTask newsListDbTask; //新闻列表数据库
    private boolean isRefresh = true;//是否首次加载
    private CustomSwipeRefreshLayout mSwipeRefreshWidget;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private NewsItemListViewAdapter adapter;

    private int page = 1;
    private static final int pageSize = 15;//
    private boolean loading = false;
    private View background_empty;
    private RecyclerView.OnScrollListener onScrollListener;
    private View app_progress_bar;

    @Override
    public String getAnalyticPageName() {
        return PREFIX + "REKOMENDASI";
    }

    private TextView update_counts;
    boolean addLoading = false;
    NewsItemListViewAdapter.CallBack callBack;
    private boolean isRefreshCounts = true;

    @SuppressLint("ValidFragment")
    public ChooseFragment(NewsChannelBean channelbean) {
        this.channelbean = channelbean;
    }

    public ChooseFragment() {
        this.channelbean = new NewsChannelBean();
        channelbean.setTid("" + NewsChannelBean.TYPE_RECOMMEND);
        channelbean.setType(NewsChannelBean.TYPE_RECOMMEND);
        channelbean.setCnname(getString(R.string.recommend));
    }


    private boolean pullRefresh;

    private int color;
    private Object tag;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //解决RecyclerView和SwipeRefreshLayout共用存在的bug
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mSwipeRefreshWidget.setEnabled(topRowVerticalPosition >= 0);
                if (addLoading && !adapter.showLoading) {
                    addLoading = false;
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.showLoading = true;
                            adapter.notifyDataSetChanged();
                        }
                    }, 300);
                }
            }

        };
        FrameLayout view = (FrameLayout) inflater.inflate(R.layout.choose_fragment, container, false);
        update_counts = (TextView) view.findViewById(R.id.update_counts);
        app_progress_bar = view.findViewById(R.id.app_progress_bar);
        tag = OkHttpClientManager.getTag();
        TypedValue typedValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.choose_fragment_line, typedValue, true);
        color = typedValue.data;
        background_empty = view.findViewById(R.id.background_empty);
        background_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeRefreshWidget.setRefreshing(true);
            }
        });
        mSwipeRefreshWidget = (CustomSwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recylerlist);
        mSwipeRefreshWidget.setColorSchemeResources(R.color.google_blue);
        mSwipeRefreshWidget.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullRefresh = true;
                page = 1;
                isRefreshCounts = true;
                getFlash();
                getServerList("onRefresh");
            }
        });


        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setOnScrollListener(onScrollListener);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new NewsItemListViewAdapter(getActivity(), this, true);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.getItemAnimator().setRemoveDuration(800);
        callBack = new NewsItemListViewAdapter.CallBack() {
            @Override
            public void loadMore() {
                if (loading) {
                    return;
                }
                isRefreshCounts = false;
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 2;
                        getFlash();
                        getServerList("callBack");
                    }
                }, 10);
            }
        };
        adapter.callBack = callBack;

        return view;
    }

    //获取幻灯
    private void getFlash() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsListDbTask = new NewsListDbTask(activity);
    }

    @Override
    public String getTitle() {
        return channelbean.getCnname();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        loading = false;
        page = 1;
        firstLoading = false;
        getDbList();
    }

    boolean firstLoading = false;

    public void loadData() {
        if (firstLoading) {
            return;
        }
        if (page == 1 && mRecyclerView != null) {
            isRefreshCounts = true;
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


    public void showFeedback() {
//        if (FeedbackTagFragment.shown) {
//            return;
//        }
//        FeedbackTagFragment fragment = new FeedbackTagFragment();
//        fragment.show(getActivity().getSupportFragmentManager(), FeedbackTagFragment.TAG);
        Intent intent = SPUtil.getIntent(getActivity());
        if (intent != null) {
            startActivity(intent);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        OkHttpClientManager.cancel(tag);
    }

    //新闻列表
    public void getDbList() {
        newsListDbTask.findList(channelbean, page, pageSize, new I_SetList<NewsBeanDB>() {
            @Override
            public void setList(List<NewsBeanDB> list) {
                if (!isAdded()) {
                    return;
                }
                if (null != list && list.size() > 0) {
                    isRefresh = false;
                    app_progress_bar.setVisibility(View.GONE);
                    background_empty.setVisibility(View.GONE);
                    List<NewsBean> nbList = new ArrayList<NewsBean>();
                    for (NewsBeanDB nbdb : list) {
                        NewsBean newsBean = nbdb.getNewsBean();
                        newsBean.setCnname(channelbean.getCnname());
                        nbList.add(newsBean);
                    }
                    for (NewsBean newsBean : nbList) {
                        if (newsBean.getType().equals("99")) {
                            nbList.remove(newsBean);
                            nbList.add(0, newsBean);
                            break;
                        }
                    }

                    adapter.showLoading = true;
                    adapter.setData(nbList);
                    background_empty.setVisibility(View.GONE);

                    //自动刷新
                    mRecyclerView.scrollToPosition(0);
                    mSwipeRefreshWidget.setRefreshing(true);
                    pullRefresh = true;
                    page = 1;
                    getServerList("");
                } else {
                    getServerList("");
                }
            }

        });
    }

    //获取新闻list
    public void getServerList(String nids) {
        Log.e("setData", "处理中 setData  isRefreshCounts" + nids);
        if (!isAdded() || !Utils.isNetworkConnected(getActivity())) {
            app_progress_bar.setVisibility(View.GONE);
            if (adapter.list == null || adapter.list.size() == 0) {
                background_empty.setVisibility(View.VISIBLE);
            }
            if (App.getInstance().getIsVisible() && isVisible) {
                TUtils.toast(getString(R.string.toast_check_network));
            }
            mSwipeRefreshWidget.setRefreshing(false);
            return;
        }
        Map<String, String> params = RequestParamsUtils.getMaps();
        params.put("siteid", InterfaceJsonfile.SITEID);
        params.put("newTime", App.getInstance().newTime);
        params.put("oldTime", App.getInstance().oldTime);
        params.put("Page", "" + page);
        params.put("PageSize", "" + pageSize);
        SPUtil.addCallback(params, channelbean.gerKey());
        OkHttpClientManager.postAsyn(tag
                , InterfaceJsonfile.CHANNEL_RECOMMEND_NEW
                , resultCallback
                , params
        );
    }

    private OkHttpClientManager.ResultCallback resultCallback = new OkHttpClientManager.ResultCallback() {
        @Override
        public void onSuccess(Object response) {
            try {
                loading = false;
                isRefresh = false;
                if (!isAdded()) {
                    return;
                }
                app_progress_bar.setVisibility(View.GONE);
                background_empty.setVisibility(View.GONE);
                try {
                    mSwipeRefreshWidget.setRefreshing(false);
                    final JSONObject obj = FjsonUtil.parseObject(response.toString());
                    if (null != obj) {
                        if (!TextUtils.isEmpty(obj.getString(SPUtil.CALLBACK))) {
                            SPUtil.setGlobal(SPUtil.CALLBACK + channelbean.gerKey(), obj.getString(SPUtil.CALLBACK));
                        }
                        setData(obj);//处理数据
                        try {
                            if (!TextUtils.isEmpty(obj.getString("newTime"))) {
                                App.getInstance().newTime = obj.getString("newTime");
                            } else if (!TextUtils.isEmpty(obj.getString("oldTime"))) {
                                App.getInstance().oldTime = obj.getString("oldTime");
                            }
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e) {
                    onFailure(null, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Request request, Exception e) {
            loading = false;
            isRefresh = false;
            pullRefresh = false;
            if (!isAdded()) {
                return;
            }
            if (App.getInstance().getIsVisible()) {
                TUtils.toast(getString(R.string.toast_cannot_connect_network));
            }
            mSwipeRefreshWidget.setRefreshing(false);
        }

    };


    //服务端返回数据处理
    public void setData(JSONObject obj) {
        Log.i("setData", "setData  isRefreshCounts" + isRefreshCounts);
        //数据处理
        switch (obj.getIntValue("code")) {
            case 200: {
                List<NewsBean> list = FjsonUtil.parseArray(obj.getString("data"), NewsBean.class);

                if (list != null) {
                    for (NewsBean bean : list) {
                        bean.setTid(channelbean.getTid());
                    }
                    newsListDbTask.saveList(list, null);
                    adapter.checkList(list);
                }
//                if (BuildConfig.DEBUG) {
//                    for (int i = 0; i < list.size(); i++) {
//                        NewsBean newsBean = list.get(i);
//                        if (BuildConfig.DEBUG && "99".equals(newsBean.getType())) {
//                            newsBean.setType("23");
//                            newsBean.setJson_url("http://2449.vod.myqcloud.com/2449_43b6f696980311e59ed467f22794e792.f20.mp4");
//                        }
//                    }
//                }
                if (null != list) {
                    if (isRefreshCounts && list.size() > 0) {
                        update_counts.setVisibility(View.VISIBLE);
                        update_counts.setText(String.format(getString(R.string.update_counts), list.size()));
                        update_counts.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                update_counts.setVisibility(View.GONE);
                            }
                        }, 2000);
                    } else if (list.size() == 0) {
                        update_counts.setVisibility(View.VISIBLE);
                        update_counts.setText(getString(R.string.pull_to_refresh_reached_end));
                        update_counts.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                update_counts.setVisibility(View.GONE);
                            }
                        }, 2000);

                        adapter.showLoading = false;
                        addLoading = false;
                    }
                    mRecyclerView.stopScroll();
                    if (!TextUtils.isEmpty(obj.getString("newTime")) || page == 1) {
                        adapter.addTop(list);
                    } else {
                        adapter.addBottom(list);
                        if (list.size() >= 7) {
                            adapter.showLoading = true;
                        } else {
                            adapter.showLoading = false;
                            addLoading = false;
                        }
                    }
                    background_empty.setVisibility(View.GONE);

                }

                adapter.showLoading = true;
                if (list.size() == 0 && page > 1) {
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addLoading = true;
                        }
                    }, 2000);
                    adapter.hidLoading();
                    addLoading = false;
                }

            }
            break;
            default: {
                if (pullRefresh) {
                    if (App.getInstance().getIsVisible()) {
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
                if (adapter.showLoading) {
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addLoading = true;
                        }
                    }, 2000);
                    adapter.hidLoading();
                    addLoading = false;
                }
            }
        }
        isRefresh = false;
        pullRefresh = false;
    }

    @Override
    public void onPause() {
        super.onPause();
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

    public void onEventMainThread(FontSizeEvent event) {
        adapter.setFontSize(event.getFontSize());
    }

    public void onEventMainThread(LoadImageEvent event) {
        if (isAdded() && adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void onEventMainThread(RefreshEvent event) {
        if (isVisible || event.force) {
            if (event.force) {
                adapter.setData(new ArrayList<NewsBean>());
            }
            isRefresh = true;
            mRecyclerView.scrollToPosition(0);
            mSwipeRefreshWidget.setRefreshing(true);
            pullRefresh = true;
            page = 1;
            getServerList("");
            isRefreshCounts = false;
        }
    }
}

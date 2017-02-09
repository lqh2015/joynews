package com.hzpd.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hzpd.adapter.NewsItemListViewAdapter;
import com.hzpd.hflt.R;
import com.hzpd.modle.NewsBean;
import com.hzpd.modle.db.NewsBeanDB;
import com.hzpd.modle.db.NewsBeanDBDao;
import com.hzpd.ui.App;
import com.hzpd.utils.AAnim;
import com.hzpd.utils.AvoidOnClickFastUtils;
import com.hzpd.utils.DBHelper;
import com.hzpd.utils.Log;
import com.hzpd.utils.db.NewsListDbTask;

import java.util.ArrayList;
import java.util.List;

public class RecentlyReadActivity extends MBaseActivity implements View.OnClickListener {

    @Override
    protected int getOnResumeApi() {
        return 126;
    }

    @Override
    protected int getOnStopApi() {
        return 127;
    }


    private TextView stitle_tv_content;

    private RecyclerView recylerlist;
    private NewsItemListViewAdapter adapter;
    private View coverTop;
    private View pushmsg_tv_empty;
    private TextView stitle_tv_delete;
    List<NewsBeanDB> list;
    private NewsListDbTask newsListDbTask; //新闻列表数据库

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recently_read_layout);
        super.changeStatusBar();
        newsListDbTask = new NewsListDbTask(this);
        pushmsg_tv_empty = findViewById(R.id.pushmsg_tv_empty);
        stitle_tv_content = (TextView) findViewById(R.id.stitle_tv_content);
        stitle_tv_content.setText(getResources().getString(R.string.recently_read));
//        stitle_tv_delete = (TextView) findViewById(R.id.stitle_tv_delete);
//        stitle_tv_delete.setVisibility(View.VISIBLE);
//        stitle_tv_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (list != null || list.size() > 0) {
//                    for (int i = 0; i < list.size(); i++) {
//                        NewsBeanDB newsBeanDB = list.get(i);
//                        newsBeanDB.setIsreaded("0");
//                        DBHelper.getInstance().getNewsList().update(newsBeanDB);
//                    }
//                    setRead();
////                    adapter.notifyDataSetChanged();
//                }
//            }
//        });
        findViewById(R.id.stitle_ll_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        coverTop = findViewById(R.id.cover_top);
        if (App.getInstance().getThemeName().equals("0")) {
            coverTop.setVisibility(View.GONE);
        } else {
            coverTop.setVisibility(View.VISIBLE);
        }
        recylerlist = (RecyclerView) findViewById(R.id.recylerlist);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recylerlist.setLayoutManager(layoutManager);
        adapter = new NewsItemListViewAdapter(RecentlyReadActivity.this, this, false);
//        recylerlist = search_listview_id.getRefreshableView();
        recylerlist.setAdapter(adapter);

        setRead();
    }

    private void setRead() {
        long startTime = System.currentTimeMillis();
        try {
            Log.e("News", "News: startTime1==" + (System.currentTimeMillis() - startTime));
            list = DBHelper.getInstance().getNewsList()
                    .queryBuilder().where(NewsBeanDBDao.Properties.Isreaded.eq("1"))
                    .orderDesc(NewsBeanDBDao.Properties.Id)
                    .build()
                    .list();

            Log.e("News", "News: startTime2==" + (System.currentTimeMillis() - startTime));
            if (null != list && list.size() > 0) {
                pushmsg_tv_empty.setVisibility(View.GONE);
                Log.i("isreaded", "isreaded" + list + ":::" + list.size());
                List<NewsBean> nblist = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    NewsBean bean = new NewsBean(list.get(i));
//                    Log.i("MyPushActivity", "MyPushActivity" + bean.getNid());
                    nblist.add(bean);
                }
                adapter.appendData(nblist, false, false);
            } else {
                pushmsg_tv_empty.setVisibility(View.VISIBLE);
            }
            Log.e("News", "News: startTime3==" + (System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Log.i("onClick", "onClick:nb==" + nb.getJson_url() + "\nnb.getRtype==" + nb.getRtype());
        Intent mIntent = new Intent();
        mIntent.putExtra("newbean", nb);
        mIntent.putExtra("from", "newsitem");
//        adapter.setReadedId(nb);
        ////////////////////////////
        //1新闻  2图集  3直播 4专题  5关联新闻 6视频
        if ("1".equals(nb.getRtype())) {
            mIntent.setClass(this, NewsDetailActivity.class);
        } else if ("2".equals(nb.getRtype())) {
            mIntent.setClass(this, NewsAlbumActivity.class);
        } else if ("3".equals(nb.getRtype())) {
            mIntent.setClass(this, NewsDetailActivity.class);//TODO 直播界面
        } else if ("4".equals(nb.getRtype())) {
            mIntent.setClass(this, ZhuanTiActivity.class);
        } else if ("5".equals(nb.getRtype())) {
            mIntent.setClass(this, NewsDetailActivity.class);
        } else if ("6".equals(nb.getRtype())) {
//					mIntent.setClass(getActivity(),VideoPlayerActivity.class);
            mIntent.setClass(this, NewsDetailActivity.class);
        } else if ("7".equals(nb.getRtype())) {
            mIntent.setClass(this, NewsDetailActivity.class);
        } else {
            return;
        }

        activity.startActivityForResult(mIntent, 0);
        AAnim.ActivityStartAnimation(this);
    }
}
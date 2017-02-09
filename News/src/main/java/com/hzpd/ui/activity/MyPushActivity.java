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
import com.hzpd.modle.db.PushBeanDB;
import com.hzpd.modle.db.PushBeanDBDao;
import com.hzpd.utils.AAnim;
import com.hzpd.utils.AvoidOnClickFastUtils;
import com.hzpd.utils.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class MyPushActivity extends MBaseActivity implements View.OnClickListener {

    private TextView stitle_tv_content;

    private RecyclerView recylerlist;
    private NewsItemListViewAdapter adapter;

    private View pushmsg_tv_empty;

    @Override
    protected int getOnResumeApi() {
        return 122;
    }

    @Override
    protected int getOnStopApi() {
        return 123;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_layout);
        super.changeStatusBar();
        stitle_tv_content = (TextView) findViewById(R.id.stitle_tv_content);
        stitle_tv_content.setText(R.string.prompt_my_msg);
        pushmsg_tv_empty = findViewById(R.id.pushmsg_tv_empty);
        findViewById(R.id.stitle_ll_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recylerlist = (RecyclerView) findViewById(R.id.recylerlist);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recylerlist.setLayoutManager(layoutManager);
        adapter = new NewsItemListViewAdapter(MyPushActivity.this, this, false);
        recylerlist.setAdapter(adapter);

        try {
            List<PushBeanDB> list = DBHelper.getInstance().getPushList().queryBuilder().orderDesc(PushBeanDBDao.Properties.Id).list();
            if (null != list && list.size() > 0) {
                pushmsg_tv_empty.setVisibility(View.GONE);
                List<NewsBean> nblist = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    NewsBean bean = new NewsBean(list.get(i));
                    nblist.add(bean);
//                    Log.i("MyPushActivity", "MyPushActivity" + nblist.toString());
                }
                adapter.appendData(nblist, false, false);
            } else {
                pushmsg_tv_empty.setVisibility(View.VISIBLE);
            }
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
        Intent mIntent = new Intent();
        mIntent.putExtra("newbean", nb);
        mIntent.putExtra("from", "newsitem");
        adapter.setReadedId(nb);
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
package com.hzpd.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.color.tools.mytools.LogUtils;
import com.hzpd.adapter.RecommendDragAdapter;
import com.hzpd.adapter.editcolumn.DragAdapter;
import com.hzpd.adapter.editcolumn.LastEditColumnAdapter;
import com.hzpd.custorm.DragGrid;
import com.hzpd.custorm.OtherGridView;
import com.hzpd.hflt.R;
import com.hzpd.modle.NewsChannelBean;
import com.hzpd.modle.db.NewsChannelBeanDB;
import com.hzpd.modle.db.NewsChannelBeanDBDao;
import com.hzpd.modle.event.ChangeChannelEvent;
import com.hzpd.modle.event.ChannelSortedList;
import com.hzpd.ui.App;
import com.hzpd.utils.AAnim;
import com.hzpd.utils.AnalyticUtils;
import com.hzpd.utils.AvoidOnClickFastUtils;
import com.hzpd.utils.Log;
import com.hzpd.utils.SPUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MyEditColumnActivity extends MBaseActivity implements View.OnClickListener {

    @Override
    public String getAnalyticPageName() {
        return AnalyticUtils.SCREEN.edit;
    }

    private DragGrid editcolumn_dragGridView;
    private OtherGridView editcolumn_gridview;
    private TextView stitle_tv_content;

    private LastEditColumnAdapter myAllAdapter;//所有title适配器
    private RecommendDragAdapter dragAdapter;    //显示条目适配器

    //适配器内容
    private List<NewsChannelBean> channelData;
    private List<NewsChannelBean> myAllList;

    private TextView editcolumn_item_tv;
    private TextView text_editcolumn;
    private TextView editcolum_explain;

    private ChannelSortedList csl;
    private boolean isEdit;
    private View coverTop;
    private View stitle_ll_back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.editcolumn_my_layout);
            super.changeStatusBar();
            initViews();
            findViewById(R.id.ll_choose_channel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AvoidOnClickFastUtils.isFastDoubleClick(v)) {
                        return;
                    }

                    Log.i("onClick", "onClick");
                    Intent intent = new Intent(MyEditColumnActivity.this, SearchActivity.class);
                    startActivity(intent);
                    AAnim.ActivityStartAnimation(MyEditColumnActivity.this);
                    isEdit = false;
                    editcolum_explain.setVisibility(View.GONE);
                    dragAdapter.isEditItem(isEdit);
                    myAllAdapter.isEditItem(isEdit);
                    text_editcolumn.setText(getString(R.string.editcolumn_edit));
                    editcolumn_dragGridView.isEditColumn(isEdit);
                }
            });
            stitle_tv_content.setText(R.string.prompt_column_subscribe);
            text_editcolumn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEdit) {
                        isEdit = true;
                        Log.e("isEdit", "isEdit" + true);
                        editcolum_explain.setVisibility(View.VISIBLE);
                        dragAdapter.isEditItem(isEdit);
                        myAllAdapter.isEditItem(isEdit);
                        editcolumn_dragGridView.isEditColumn(isEdit);
                        text_editcolumn.setText(getString(R.string.editcolumn_ok));
                    } else {
                        isEdit = false;
                        editcolum_explain.setVisibility(View.GONE);
                        dragAdapter.isEditItem(isEdit);
                        myAllAdapter.isEditItem(isEdit);
                        text_editcolumn.setText(getString(R.string.editcolumn_edit));
                        editcolumn_dragGridView.isEditColumn(isEdit);
                        Log.e("isEdit", "isEdit" + false);
                    }
                }
            });
            init();
            parseChannelJson();
            reReadVisibleChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        editcolumn_dragGridView = (DragGrid) findViewById(R.id.editcolumn_dragGridView);
        editcolumn_gridview = (OtherGridView) findViewById(R.id.editcolumn_gridview);
        stitle_tv_content = (TextView) findViewById(R.id.stitle_tv_content);
        editcolumn_item_tv = (TextView) findViewById(R.id.editcolumn_item_tv);
        text_editcolumn = (TextView) findViewById(R.id.text_editcolumn);
        editcolum_explain = (TextView) findViewById(R.id.editcolum_explain);
        stitle_ll_back = findViewById(R.id.stitle_ll_back);
        stitle_ll_back.setOnClickListener(this);
        coverTop = findViewById(R.id.cover_top);
        if (App.getInstance().getThemeName().equals("0")) {
            coverTop.setVisibility(View.GONE);
        } else {
            coverTop.setVisibility(View.VISIBLE);
        }
    }


    private void init() {
        try {
            List<NewsChannelBeanDB> dbs = dbHelper.getChannel().queryBuilder()
                    .where(NewsChannelBeanDBDao.Properties.Default_show.eq("1")).build().list();
            channelData = new ArrayList<>();
            for (NewsChannelBeanDB beanDB : dbs) {
                channelData.add(new NewsChannelBean(beanDB));
            }
            if (channelData == null) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("channelData", "channelData" + channelData.size());
        // 不显示在新闻tab栏的频道列表
        myAllList = new ArrayList<>();
        myAllAdapter = new LastEditColumnAdapter(this);
        editcolumn_gridview.setAdapter(myAllAdapter);

        csl = new ChannelSortedList();
    }

    //修改频道缓存配置，清除缓存时不删除频道
    private String getChannelInfoCacheSavePath() {
        return App.getInstance().getAllDiskCacheDir()
                + File.separator
                + App.mTitle;
    }

    //TODO 是否有修改
    boolean modified = false;
    private int selPosition = -1;

    private void reReadVisibleChannel() {

        try {
            dragAdapter = new RecommendDragAdapter(this, channelData);
            editcolumn_dragGridView.setAdapter(dragAdapter);
            editcolumn_dragGridView.text_editcolumn = text_editcolumn;
            editcolumn_dragGridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        final int position, long id) {
                    LogUtils.e("position-->" + position);
                    if (!isEdit) {
                        selPosition = position;
                        finish();
                        return;
                    }
                    if (position == 0 || position == 1) {
                        return;
                    }

                    modified = true;
                    final NewsChannelBean ncb = channelData.get(position + DragAdapter.hiddenNum);
                    channelData.remove(position + DragAdapter.hiddenNum);
                    dragAdapter.setList(channelData);
                    myAllAdapter.addData(ncb);
                }
            });
            editcolumn_dragGridView.setOnDragListener(new DragGrid.OnDragListener() {
                @Override
                public void onDrag() {
                    Log.e("editcolumn_dragGridView", "editcolumn_dragGridView--->onDrag");
                    modified = true;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param ncb
     * @param from     true取消订阅
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param position
     */
    public void addAnim(NewsChannelBean ncb, final boolean from,
                        float startX, float startY
            , float endX, float endY, final int position) {
        PropertyValuesHolder pvhX = null;
        PropertyValuesHolder pvhY = null;

        pvhX = PropertyValuesHolder.ofFloat("x", startX, endX);
        pvhY = PropertyValuesHolder.ofFloat("y", startY, endY);

        editcolumn_item_tv.setText(ncb.getCnname());

        ObjectAnimator objAnim = ObjectAnimator
                .ofPropertyValuesHolder(editcolumn_item_tv, pvhX, pvhY);

        objAnim.setDuration(500);
        objAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                LogUtils.i("animStart");
                editcolumn_item_tv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                LogUtils.i("animend");
                editcolumn_item_tv.setVisibility(View.GONE);
                if (from) {
                    LogUtils.i("channelData-->" + channelData.size() + "  " + (position + DragAdapter.hiddenNum));
                    myAllAdapter.setAnim(false);
                    channelData.remove(position + DragAdapter.hiddenNum);
                    dragAdapter.setList(channelData);
                    myAllAdapter.notifyDataSetChanged();
                } else {
                    LogUtils.i("myAllList-->" + myAllList.size() + " " + position);
                    dragAdapter.setAnim(false);
                    myAllList.remove(position);
                    myAllAdapter.setList(myAllList);
                    dragAdapter.notifyDataSetChanged();
                }
            }
        });

        objAnim.start();

    }


    private void parseChannelJson() {

        try {
            // 读取json解析出所有的频道
            List<NewsChannelBeanDB> dbs = dbHelper.getChannel().queryBuilder()
                    .where(NewsChannelBeanDBDao.Properties.Default_show.eq("0")).build().list();
            myAllList = new ArrayList<>();
            for (NewsChannelBeanDB beanDB : dbs) {
                myAllList.add(new NewsChannelBean(beanDB));
            }
            if (myAllList != null) {
                myAllAdapter.setList(myAllList);
                editcolumn_gridview.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            final View view, final int position, long id) {
                        final NewsChannelBean ncb = (NewsChannelBean) myAllAdapter.getItem(position);
                        modified = true;
                        channelData.add(ncb);
                        dragAdapter.setList(channelData);
                        myAllList.remove(position);
                        myAllAdapter.setList(myAllList);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeData() {
        //TODO save
        try {
            for (NewsChannelBean bean : channelData) {
                bean.setDefault_show("1");
            }
            for (NewsChannelBean bean : myAllList) {
                bean.setDefault_show("0");
            }
            List<NewsChannelBean> all = new ArrayList<>();
            all.addAll(channelData);
            all.addAll(myAllList);
            List<NewsChannelBeanDB> dbs = new ArrayList<>();
            for (NewsChannelBean bean : all) {
                dbs.add(new NewsChannelBeanDB(bean));
            }
            dbHelper.getChannel().deleteAll();
            for (int i = 0; i < dbs.size(); i++) {
                dbs.get(i).setId((long) i);
            }
            dbHelper.getChannel().insertInTx(dbs);
            csl.setSaveTitleList(channelData);
            SPUtil.updateChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (!modified) {
            csl = null;
        }
        if (isEdit) {
            selPosition = -1;
        }
        if (modified) {
            writeData();
            modified = false;
        }
        EventBus.getDefault().post(new ChangeChannelEvent(csl, selPosition));
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stitle_ll_back:
                finish();
                break;
        }
    }
}

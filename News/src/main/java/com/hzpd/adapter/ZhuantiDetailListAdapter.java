package com.hzpd.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzpd.hflt.R;
import com.hzpd.modle.NewsBean;
import com.hzpd.modle.SubjectItemColumnsBean;
import com.hzpd.modle.db.NewsBeanDB;
import com.hzpd.modle.db.NewsBeanDBDao;
import com.hzpd.ui.App;
import com.hzpd.ui.interfaces.I_Result;
import com.hzpd.utils.CalendarUtil;
import com.hzpd.utils.DBHelper;
import com.hzpd.utils.DisplayOptionFactory;
import com.hzpd.utils.SPUtil;
import com.hzpd.utils.db.NewsListDbTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class ZhuantiDetailListAdapter extends RecyclerView.Adapter {

    private Activity context;
    private LayoutInflater inflater;
    final static int TYPE_HEAD = 0x10;
    final static int TYPE_CLOUMN_HEAD = 0x11;

    final static int TYPE_THREEPIC = 0x13;
    final static int TYPE_LEFTPIC = 0x14;
    final static int TYPE_BIGPIC = 0x15;
    final static int TYPE_LARGE = 0x16;

    private HashSet<String> readedNewsSet;
    private NewsListDbTask newsListDbTask;
    DBHelper dbHelper;


    private LinkedHashMap<SubjectItemColumnsBean, List<NewsBean>> columnList = new LinkedHashMap<>();
    View.OnClickListener onClickListener;

    private NewsBean nb;

    public ZhuantiDetailListAdapter(Activity context, View.OnClickListener onClickListener, NewsBean nb) {
        this.context = context;
        this.onClickListener = onClickListener;
        this.inflater = LayoutInflater.from(context);
        this.nb = nb;
        readedNewsSet = new HashSet<String>();
        newsListDbTask = new NewsListDbTask(context);
        dbHelper = DBHelper.getInstance();
    }

    public void setReadedId(String nid) {
        readedNewsSet.add(nid);

        try {
            NewsBeanDB nbdb = dbHelper.getNewsList().queryBuilder().where(NewsBeanDBDao.Properties.Nid.eq(nid)).build().unique();
            nbdb.setNid(nid);
            nbdb.setIsreaded("1");
            dbHelper.getNewsList().update(nbdb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        isAdd = false;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        isAdd = true;
    }

    boolean isAdd = true;

    public void getReaded(List<NewsBean> list) {
        if (null == list) {
            return;
        }
        for (final NewsBean bean : list) {

            newsListDbTask.isRead(bean.getNid(), new I_Result() {
                @Override
                public void setResult(Boolean flag) {
                    try {
                        if (isAdd && flag) {
                            readedNewsSet.add(bean.getNid());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void appendData(SubjectItemColumnsBean column, List<NewsBean> list,
                           Boolean isClearOldList) {

        int last = getItemCount();
        List<NewsBean> oldList = columnList.get(column);
        if (null == oldList) {
            oldList = new ArrayList<NewsBean>();
        } else {
            if (isClearOldList) {
                oldList.clear();
            }
        }
        oldList.addAll(list);
        getReaded(list);
        columnList.put(column, oldList);
        int count = getItemCount();
        if (last > 0) {
            notifyItemRangeChanged(last, count - last);
        } else {
            notifyDataSetChanged();
        }
    }

    public void clearData() {
        columnList.clear();
    }


    public class HeadViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public HeadViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class ColumnViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ColumnViewHolder(View itemView) {
            super(itemView);
        }
    }


    //三联图
    private class VHThree extends RecyclerView.ViewHolder {
        private TextView newsitem_title;
        private TextView tv3;
        private ImageView img0;
        private ImageView img1;
        private ImageView img2;
        private ImageView newsitem_foot;
        private TextView newsitem_comments;
        private TextView newsitem_source;
        private TextView newsitem_collectcount;
        private ImageView item_type_iv;

        public VHThree(View v) {
            super(v);
            newsitem_title = (TextView) v.findViewById(R.id.newsitem_title);
            tv3 = (TextView) v.findViewById(R.id.newsitem_time);
            newsitem_comments = (TextView) v.findViewById(R.id.newsitem_commentcount);
            newsitem_source = (TextView) v.findViewById(R.id.newsitem_source);
            newsitem_collectcount = (TextView) v.findViewById(R.id.newsitem_collectcount);
            newsitem_foot = (ImageView) v.findViewById(R.id.nli_foot);
            img0 = (ImageView) v.findViewById(R.id.news_3_item1);
            img1 = (ImageView) v.findViewById(R.id.news_3_item2);
            img2 = (ImageView) v.findViewById(R.id.news_3_item3);
            item_type_iv = (ImageView) v.findViewById(R.id.item_type_iv);
            v.setOnClickListener(onClickListener);
        }
    }

    //左边图片，右title，评论，时间，脚标
    private class VHLeftPic extends RecyclerView.ViewHolder {
        private TextView newsitem_title;
        private ImageView nli_foot;
        private TextView newsitem_source;
        private TextView newsitem_collectcount;
        private TextView newsitem_commentcount;
        private TextView newsitem_time;
        private ImageView newsitem_img;
        private ImageView item_type_iv;

        public VHLeftPic(View v) {
            super(v);
            newsitem_title = (TextView) v.findViewById(R.id.newsitem_title);
            nli_foot = (ImageView) v.findViewById(R.id.nli_foot);
            newsitem_source = (TextView) v.findViewById(R.id.newsitem_source);
            newsitem_collectcount = (TextView) v.findViewById(R.id.newsitem_collectcount);
            newsitem_commentcount = (TextView) v.findViewById(R.id.newsitem_commentcount);
            newsitem_time = (TextView) v.findViewById(R.id.newsitem_time);
            newsitem_img = (ImageView) v.findViewById(R.id.newsitem_img);
            item_type_iv = (ImageView) v.findViewById(R.id.item_type_iv);
            v.setOnClickListener(onClickListener);
        }
    }

    //大图
    private class VHLargePic extends RecyclerView.ViewHolder {
        private TextView newsitem_title;
        private ImageView nli_foot;
        private TextView newsitem_source;
        private TextView newsitem_collectcount;
        private TextView newsitem_commentcount;
        private TextView newsitem_time;
        private ImageView newsitem_img;
        private ImageView item_type_iv;

        public VHLargePic(View v) {
            super(v);
            newsitem_title = (TextView) v.findViewById(R.id.newsitem_title);
            nli_foot = (ImageView) v.findViewById(R.id.nli_foot);
            newsitem_source = (TextView) v.findViewById(R.id.newsitem_source);
            newsitem_collectcount = (TextView) v.findViewById(R.id.newsitem_collectcount);
            newsitem_commentcount = (TextView) v.findViewById(R.id.newsitem_commentcount);
            newsitem_time = (TextView) v.findViewById(R.id.newsitem_time);
            newsitem_img = (ImageView) v.findViewById(R.id.newsitem_img);
            item_type_iv = (ImageView) v.findViewById(R.id.item_type_iv);
            v.setOnClickListener(onClickListener);
        }
    }

    private class VHBigPic extends RecyclerView.ViewHolder {

        private ImageView news_big_item1;


        public VHBigPic(View v) {
            super(v);
//            news_big_item1 = (ImageView) v.findViewById(R.id.news_big_item1);
            v.setOnClickListener(onClickListener);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEAD:
                View headView
                        = inflater.inflate(R.layout.subject_detail_head_layout,
                        parent, false);
                HeadViewHolder headViewHolder = new HeadViewHolder(headView);
                headViewHolder.imageView = (ImageView) headView.findViewById(R.id.zhuanti_header_iv);
                headViewHolder.textView = (TextView) headView.findViewById(R.id.zhuanti_tv_title);
                return headViewHolder;
            case TYPE_CLOUMN_HEAD:
                View columnView
                        = inflater.inflate(R.layout.zhuanti_column_item,
                        parent, false);
                ColumnViewHolder columnViewHolder = new ColumnViewHolder(columnView);
                columnViewHolder.textView = (TextView) columnView.findViewById(R.id.zhuanti_tv_column);
                return columnViewHolder;
            case TYPE_THREEPIC://三张连图
                View threePic = inflater.inflate(
                        R.layout.news_3_item_layout, parent, false);
                VHThree threeHolder = new VHThree(threePic);
                return threeHolder;
            case TYPE_LEFTPIC://普通
                View leftPicView = inflater.inflate(
                        R.layout.news_list_item_layout, parent, false);
                VHLeftPic leftHolder = new VHLeftPic(leftPicView);
                return leftHolder;
            case TYPE_BIGPIC:

                return null;
            case TYPE_LARGE://大图
                View largeView = inflater.inflate(
                        R.layout.news_large_item_layout, parent, false);
                VHLargePic largeHolder = new VHLargePic(largeView);
                return largeHolder;
            default:
                return new HeadViewHolder(new Button(context));
        }
    }


    public Object getItem(int position) {
        if (position == 0) {
            return null;
        }
        int columnCounts = 1;
        Set<SubjectItemColumnsBean> sets = columnList.keySet();
        for (SubjectItemColumnsBean sicb : sets) {
            if (position == columnCounts) {
                return sicb.getCname();
            } else {
                columnCounts += 1;
                int start = columnCounts;
                List<NewsBean> nbList = columnList.get(sicb);
                if (null != nbList && nbList.size() > 0) {
                    columnCounts += nbList.size();
                    if (position < columnCounts) {
                        NewsBean bean = nbList.get(position - start);
                        return bean;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            int type = getItemViewType(position);
            switch (type) {
                case TYPE_HEAD: {
                    HeadViewHolder headViewHolder = (HeadViewHolder) holder;
                    if (!TextUtils.isEmpty(nb.getTitle()))
                        headViewHolder.textView.setText(nb.getTitle());
                    String imgs[] = nb.getImgs();
                    String img = "";
                    if (null != imgs && imgs.length > 0) {
                        img = imgs[0];
                    }
                    SPUtil.displayImage(img, headViewHolder.imageView, DisplayOptionFactory.Small.options);
                }
                break;
                case TYPE_BIGPIC: {
                    //没有参数
                }
                break;
                case TYPE_CLOUMN_HEAD: {
                    ColumnViewHolder columnViewHolder = (ColumnViewHolder) holder;
                    columnViewHolder.textView.setText(getItem(position).toString() + "");
                }
                break;
                case TYPE_LEFTPIC: {
                    NewsBean bean = (NewsBean) getItem(position);

                    holder.itemView.setTag(bean);
                    VHLeftPic vhLeftPic = (VHLeftPic) holder;
                    //                vhLeftPic.newsitem_title.setTextSize(fontSize);
                    vhLeftPic.newsitem_title.setText(bean.getTitle());
                    vhLeftPic.newsitem_title.setTextColor(App.getInstance().getResources().getColor(R.color.item_title));
                    if (readedNewsSet.contains(bean.getNid())) {
                        vhLeftPic.newsitem_title.setTextColor(App.getInstance().getResources().getColor(R.color.grey_font));
                    } else {
                        vhLeftPic.newsitem_title.setTextColor(App.getInstance().getResources().getColor(R.color.item_title));
                    }
                    SPUtil.setAtt(vhLeftPic.item_type_iv, bean.getAttname());

                    String fav = bean.getFav();
                    if (!TextUtils.isEmpty(fav)) {

                        int fav_counts = Integer.parseInt(fav);
                        if (fav_counts > 0) {
                            vhLeftPic.newsitem_collectcount.setVisibility(View.VISIBLE);
                            vhLeftPic.newsitem_collectcount.setText(fav_counts + "");
                        } else {
                            vhLeftPic.newsitem_collectcount.setVisibility(View.GONE);
                        }
                    } else {
                        vhLeftPic.newsitem_collectcount.setVisibility(View.GONE);
                    }

                    String from = bean.getCopyfrom();
                    if (!TextUtils.isEmpty(from)) {
                        vhLeftPic.newsitem_source.setVisibility(View.VISIBLE);
                        vhLeftPic.newsitem_source.setText(from);
                    } else {
                        vhLeftPic.newsitem_source.setVisibility(View.GONE);
                    }

                    String comcount = bean.getComcount();
                    if (!TextUtils.isEmpty(comcount)) {
                        int counts = Integer.parseInt(comcount);
                        if (counts > 0) {
                            vhLeftPic.newsitem_commentcount.setVisibility(View.VISIBLE);
                            bean.setComcount(counts + "");
                            vhLeftPic.newsitem_commentcount.setText(counts + "");
                        } else {
                            vhLeftPic.newsitem_commentcount.setVisibility(View.GONE);
                        }
                    } else {
                        vhLeftPic.newsitem_commentcount.setVisibility(View.GONE);
                    }


                    if (CalendarUtil.friendlyTime(bean.getUpdate_time(), context) == null) {
                        vhLeftPic.newsitem_time.setText("");
                    } else {
                        vhLeftPic.newsitem_time.setText(CalendarUtil.friendlyTime(bean.getUpdate_time(), context));
                    }


                    vhLeftPic.newsitem_img.setVisibility(View.VISIBLE);
                    vhLeftPic.nli_foot.setVisibility(View.GONE);


                    if ("1".equals(bean.getType())) {
                        vhLeftPic.newsitem_img.setVisibility(View.GONE);
                    }

                    if (bean.getSid() != null && !"0".equals(bean.getSid())) {
                        vhLeftPic.nli_foot.setImageResource(R.drawable.zq_subscript_issue);
                        vhLeftPic.nli_foot.setVisibility(View.VISIBLE);
                    }
                    SPUtil.setRtype(bean.getRtype(), vhLeftPic.nli_foot);

                    vhLeftPic.newsitem_img.setImageResource(R.drawable.default_bg);

                    if (vhLeftPic.newsitem_img.getVisibility() == View.VISIBLE
                            && null != bean.getImgs()
                            && bean.getImgs().length > 0) {
                        SPUtil.displayImage(bean.getImgs()[0], vhLeftPic.newsitem_img,
                                DisplayOptionFactory.Small.options);
                    } else {
                        vhLeftPic.newsitem_img.setVisibility(View.GONE);
                    }


                }
                break;
                case TYPE_LARGE: {
                    NewsBean bean = (NewsBean) getItem(position);
                    holder.itemView.setTag(bean);
                    VHLargePic vhLargePic = (VHLargePic) holder;
                    vhLargePic.newsitem_title.setText(bean.getTitle());
                    vhLargePic.newsitem_title.setTextColor(App.getInstance()
                            .getResources().getColor(R.color.item_title));
                    if (readedNewsSet.contains(bean.getNid())) {
                        vhLargePic.newsitem_title.setTextColor(App.getInstance()
                                .getResources().getColor(R.color.grey_font));
                    } else {
                        vhLargePic.newsitem_title.setTextColor(App.getInstance()
                                .getResources().getColor(R.color.item_title));
                    }

                    SPUtil.setAtt(vhLargePic.item_type_iv, bean.getAttname());

                    String fav = bean.getFav();
                    if (!TextUtils.isEmpty(fav)) {
                        int fav_counts = Integer.parseInt(fav);
                        if (fav_counts > 0) {
                            vhLargePic.newsitem_collectcount.setVisibility(View.VISIBLE);
                            vhLargePic.newsitem_collectcount.setText(fav_counts + "");
                        } else {
                            vhLargePic.newsitem_collectcount.setVisibility(View.GONE);
                        }
                    } else {
                        vhLargePic.newsitem_collectcount.setVisibility(View.GONE);
                    }
                    String from = bean.getCopyfrom();
                    if (!TextUtils.isEmpty(from)) {
                        vhLargePic.newsitem_source.setVisibility(View.GONE);
                    } else {
                        vhLargePic.newsitem_source.setText(from);
                        vhLargePic.newsitem_source.setVisibility(View.VISIBLE);
                    }
                    String comcount = bean.getComcount();
                    if (!TextUtils.isEmpty(comcount)) {
                        int counts = Integer.parseInt(comcount);
                        if (counts > 0) {
                            vhLargePic.newsitem_commentcount.setVisibility(View.VISIBLE);
                            bean.setComcount(counts + "");
                            vhLargePic.newsitem_commentcount.setText(counts + "");
                        } else {
                            vhLargePic.newsitem_commentcount.setVisibility(View.GONE);
                        }
                    } else {
                        vhLargePic.newsitem_commentcount.setVisibility(View.GONE);
                    }


                    if (CalendarUtil.friendlyTime(bean.getUpdate_time(), context) == null) {
                        vhLargePic.newsitem_time.setText("");
                    } else {
                        vhLargePic.newsitem_time.setText(CalendarUtil.friendlyTime(bean.getUpdate_time(), context));
                    }

                    vhLargePic.newsitem_img.setVisibility(View.VISIBLE);
                    vhLargePic.nli_foot.setVisibility(View.GONE);


                    if (!"0".equals(bean.getSid())) {
                        vhLargePic.nli_foot.setImageResource(R.drawable.zq_subscript_issue);
                        vhLargePic.nli_foot.setVisibility(View.VISIBLE);
                    }
                    SPUtil.setRtype(bean.getRtype(), vhLargePic.nli_foot);

                    if (vhLargePic.newsitem_img.getVisibility() == View.VISIBLE
                            && null != bean.getImgs()
                            && bean.getImgs().length > 0) {
                        SPUtil.displayImage(bean.getImgs()[0], vhLargePic.newsitem_img,
                                DisplayOptionFactory.Small.options);
                    } else {
                        SPUtil.displayImage("", vhLargePic.newsitem_img,
                                DisplayOptionFactory.Small.options);
                    }
                }
                break;
                case TYPE_THREEPIC: {
                    NewsBean bean = (NewsBean) getItem(position);

                    VHThree vhThree = (VHThree) holder;
                    holder.itemView.setTag(bean);
                    vhThree.newsitem_title.setText(bean.getTitle());
                    vhThree.newsitem_title.setTextColor(App.getInstance()
                            .getResources().getColor(R.color.item_title));
                    if (readedNewsSet.contains(bean.getNid())) {
                        vhThree.newsitem_title.setTextColor(App.getInstance()
                                .getResources().getColor(R.color.grey_font));
                    } else {
                        vhThree.newsitem_title.setTextColor(App.getInstance()
                                .getResources().getColor(R.color.item_title));
                    }

                    vhThree.img0.setImageResource(R.drawable.default_bg);
                    vhThree.img1.setImageResource(R.drawable.default_bg);
                    vhThree.img2.setImageResource(R.drawable.default_bg);
                    String from = bean.getCopyfrom();
                    if (!TextUtils.isEmpty(from)) {
                        vhThree.newsitem_source.setVisibility(View.VISIBLE);
                        vhThree.newsitem_source.setText(from);
                    } else {
                        vhThree.newsitem_source.setVisibility(View.GONE);
                    }

                    String fav = bean.getFav();
                    if (!TextUtils.isEmpty(fav)) {
                        int fav_counts = Integer.parseInt(fav);
                        if (fav_counts > 0) {
                            vhThree.newsitem_collectcount.setVisibility(View.VISIBLE);
                            vhThree.newsitem_collectcount.setText(fav_counts + "");
                        } else {
                            vhThree.newsitem_collectcount.setVisibility(View.GONE);
                        }
                    } else {
                        vhThree.newsitem_collectcount.setVisibility(View.GONE);
                    }
                    String comcount = bean.getComcount();
                    if (!TextUtils.isEmpty(comcount)) {
                        int counts = Integer.parseInt(comcount);
                        if (counts > 0) {
                            vhThree.newsitem_comments.setVisibility(View.VISIBLE);
                            bean.setComcount(counts + "");
                            vhThree.newsitem_comments.setText(counts + "");
                        } else {
                            vhThree.newsitem_comments.setVisibility(View.GONE);
                        }
                    } else {
                        vhThree.newsitem_comments.setVisibility(View.GONE);
                    }


                    SPUtil.setAtt(vhThree.item_type_iv, bean.getAttname());
                    vhThree.tv3.setText(CalendarUtil.friendlyTime(bean.getUpdate_time(), context));

                    String s[] = bean.getImgs();
                    if (s.length == 1) {
                        SPUtil.displayImage(s[0], vhThree.img0, DisplayOptionFactory.Small.options);
                        SPUtil.displayImage("", vhThree.img1, DisplayOptionFactory.Small.options);
                        SPUtil.displayImage("", vhThree.img2, DisplayOptionFactory.Small.options);
                    } else if (s.length == 2) {
                        SPUtil.displayImage(s[0], vhThree.img0, DisplayOptionFactory.Small.options);
                        SPUtil.displayImage(s[1], vhThree.img1, DisplayOptionFactory.Small.options);
                        SPUtil.displayImage("", vhThree.img2, DisplayOptionFactory.Small.options);
                    } else if (s.length > 2) {
                        SPUtil.displayImage(s[0], vhThree.img0, DisplayOptionFactory.Small.options);
                        SPUtil.displayImage(s[1], vhThree.img1, DisplayOptionFactory.Small.options);
                        SPUtil.displayImage(s[2], vhThree.img2, DisplayOptionFactory.Small.options);
                    }

                    vhThree.newsitem_foot.setVisibility(View.GONE);
                    SPUtil.setRtype(bean.getRtype(), vhThree.newsitem_foot);

                }
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        }
        int columnCounts = 1;
        Set<SubjectItemColumnsBean> sets = columnList.keySet();
        for (SubjectItemColumnsBean sicb : sets) {
            if (position == columnCounts) {
                return TYPE_CLOUMN_HEAD;
            } else {
                columnCounts += 1;
                int start = columnCounts;
                List<NewsBean> nbList = columnList.get(sicb);
                if (null != nbList && nbList.size() > 0) {
                    columnCounts += nbList.size();
                    if (position < columnCounts) {
                        NewsBean bean = nbList.get(position - start);
                        if ("4".equals(bean.getType())) {
                            return TYPE_THREEPIC;
                        } else if ("10".equals(bean.getType())) {
                            return TYPE_BIGPIC;
                        } else if ("99".equals(bean.getType())) {
                            return TYPE_LARGE;
                        } else {
                            return TYPE_LEFTPIC;
                        }
                    }
                }
            }
        }
        return TYPE_HEAD;
    }

    @Override
    public int getItemCount() {
        int columnCounts = 1;
        if (!columnList.isEmpty()) {
            Set<SubjectItemColumnsBean> sets = columnList.keySet();
            for (SubjectItemColumnsBean sicb : sets) {
                columnCounts += 1;
                List<NewsBean> nbList = columnList.get(sicb);
                if (null != nbList) {
                    columnCounts += nbList.size();
                }
            }
        }
        return columnCounts;
    }


}

package com.hzpd.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hzpd.hflt.R;
import com.hzpd.modle.NewsBean;
import com.hzpd.modle.db.NewsBeanDB;
import com.hzpd.modle.db.NewsBeanDBDao;
import com.hzpd.network.Const;
import com.hzpd.ui.App;
import com.hzpd.ui.interfaces.I_Result;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.CalendarUtil;
import com.hzpd.utils.DBHelper;
import com.hzpd.utils.DisplayOptionFactory;
import com.hzpd.utils.Log;
import com.hzpd.utils.RequestParamsUtils;
import com.hzpd.utils.SPUtil;
import com.hzpd.utils.db.NewsListDbTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NewsItemListViewAdapter extends RecyclerView.Adapter {

    public static String AD_KEY = "863014693810100_876703775774525";
    public static final int FPS = 60;
    public static final int STANDARD_TIME = 16;
    public static final int STEP = 12;
    public static final int MAX_POSITION = 100;
    LinearLayout.LayoutParams params;

    boolean isJokeGood = false;
    boolean isJokeBad = false;

    public void checkList(List<NewsBean> sList) {
        try {
            if (list != null && sList != null && sList.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    String up = list.get(i).getUp();
                    if ("1".equals(up)) {
                        continue;
                    }
                    String nid = list.get(i).getNid();
                    for (int j = 0; j < sList.size(); j++) {
                        if (sList.get(j).getNid().equals(nid)) {
                            sList.remove(j);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface CallBack {
        void loadMore();
    }

    final Random random = new Random();
    public CallBack callBack;
    Context context;
    LayoutInflater inflater;
    public List<NewsBean> list = null;
    DBHelper dbHelper;
    List<NewsBean> appendoldlist = null;

    final static int TYPE_THREEPIC = 0;
    final static int TYPE_LEFTPIC = 1;
    final static int TYPE_BIGPIC = 2;
    /**
     * 大图
     */
    final static int TYPE_LARGE = 3;
    final static int TYPE_TEXT = 4; // 纯文本
    final static int TYPE_AD = 0xad;
    final static int TYPE_LOADING = 0xDD;
//    final static int TYPE_HOT_SEARCH = 6;
    final static int TYPE_MY_AD = 7;
    final static int TYPE_LARGE_DIAlOG = 8;
    final static int TYPE_VEDIO = 9;
    public boolean showLoading = false;
    private SPUtil spu;
    private int fontSize = 0;//字体大小

    private HashSet<String> readedNewsSet;
    private NewsListDbTask newsListDbTask;

    private View.OnClickListener onClickListener;
    int nextAdPosition = 6;

    public NewsItemListViewAdapter() {
    }

    private Object tag;

    private boolean isNewsitemUnlike;

    public NewsItemListViewAdapter(Context context, View.OnClickListener onClickListener, boolean isNewsitemUnlike) {
        this.isNewsitemUnlike = isNewsitemUnlike;
        this.context = context;
        this.onClickListener = onClickListener;
        this.inflater = LayoutInflater.from(context);
        tag = OkHttpClientManager.getTag();
        list = new ArrayList<>();
        dbHelper = DBHelper.getInstance();
        spu = SPUtil.getInstance();
        newsListDbTask = new NewsListDbTask(context);
        readedNewsSet = new HashSet<>();
        fontSize = spu.getTextSize();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int height = (int) (dm.widthPixels * 0.55);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, height);
        initPopWindow();
    }



    public void appendData(List<NewsBean> data, boolean isClearOld, boolean isDb) {
        if (data == null && isClearOld) {
            list.clear();
            return;
        }
        int index = getItemCount();
        if (showLoading) {
            index = index - 1;
        }
        if (isClearOld) {
            list.clear();
        }
        list.addAll(data);
        getReaded(data);
        if (isDb) {
            appendoldlist = data;
        }
        if (list == null || list.size() == data.size() || isClearOld) {
            notifyDataSetChanged();
            nextAdPosition = 6;
        } else {
            notifyItemRangeInserted(index, data.size());
        }
    }

    public void removeOld() {
        if (null != appendoldlist && appendoldlist.size() > 0) {
            list.removeAll(appendoldlist);
            appendoldlist = null;
        }
    }


    public void clear() {
        list.clear();
        if (appendoldlist != null) {
            appendoldlist.clear();
        }
    }


    //  删除指定的Item
    public void removeData(NewsBean bean, int position) {
        NewsBeanDB nbdb = dbHelper.getNewsList().queryBuilder().where(NewsBeanDBDao.Properties.Nid.eq(bean.getNid())).build().unique();
        if (nbdb != null) {
            dbHelper.getNewsList().delete(nbdb);
        }
        position = list.indexOf(bean);
        list.remove(position);
        notifyItemRemoved(position);
        getServerTagUnlike(bean.getNid());
    }

    private void getServerTagUnlike(String nid) {
        final Map<String, String> params = RequestParamsUtils.getMaps();
        params.put("nid", nid + "");
        SPUtil.addParams(params);
        OkHttpClientManager.postAsyn(tag, InterfaceJsonfile.TAG_UNLIKE, null, params);
    }


    @Override
    public int getItemViewType(int position) {
        if (showLoading && position == getItemCount() - 1) {
            return TYPE_LOADING;
        }
        NewsBean bean = list.get(position);
        if ("ad".equals(bean.getType())) {
            return TYPE_AD;
        }  else if (bean.getImgs() == null || bean.getImgs().length == 0) {
            return TYPE_TEXT;
        } else if ("4".equals(bean.getType())) {
            return TYPE_THREEPIC;
        } else if ("10".equals(bean.getType())) {
            return TYPE_BIGPIC;
        } else if ("99".equals(bean.getType())) {
            if (Const.isSaveMode()) {
                return TYPE_TEXT;
            } else {
                return TYPE_LARGE;
            }
        }  else if ("21".equals(bean.getType())) {
            return TYPE_MY_AD;
        } else if ("22".equals(bean.getType())) {
            return TYPE_LARGE_DIAlOG;
        } else if ("23".equals(bean.getType())) {
            return TYPE_VEDIO;
        } else {
            return TYPE_LEFTPIC;
        }
    }

    @Override
    public int getItemCount() {
        int count = list.size();
        if (showLoading) {
            ++count;
        }
        return count;
    }

    public void setFontSize(int mfontSize) {
        this.fontSize = mfontSize;
        notifyDataSetChanged();
    }

    /**
     * 重新加载数据
     */
    public void setData(List<NewsBean> data) {
        if (data != null) {
            list.clear();
            list.addAll(data);
            notifyDataSetChanged();
        }
    }

    /**
     * 底部追加数据
     */
    public void addBottom(List<NewsBean> data) {
        if (data != null && !data.isEmpty()) {
            checkList(data);
            list.addAll(data);
            notifyDataSetChanged();
        }
    }

    /**
     * 顶部追加数据
     */
    public void addTop(List<NewsBean> data) {
        if (data != null && !data.isEmpty()) {
            checkList(data);
            list.addAll(0, data);
            notifyDataSetChanged();
        }
    }

    public void hidLoading() {
        if (showLoading) {
            int size = getItemCount();
            showLoading = false;
            notifyItemRemoved(size - 1);
        }
    }


    public void setReadedId(NewsBean newsBean) {
        readedNewsSet.add(newsBean.getNid());
        try {
            NewsBeanDB nbdb = dbHelper.getNewsList().queryBuilder().where(NewsBeanDBDao.Properties.Nid.eq(newsBean.getNid())).build().unique();
            if (nbdb != null) {
                nbdb.setNid(newsBean.getNid());
                nbdb.setIsreaded("1");
                dbHelper.getNewsList().update(nbdb);
            } else {
                nbdb = new NewsBeanDB(newsBean);
                nbdb.setIsreaded("1");
                dbHelper.getNewsList().insert(nbdb);
            }
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


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View convertView;
        switch (viewType) {
            case TYPE_TEXT://纯文本
                convertView = inflater.inflate(
                        R.layout.news_list_text_layout, parent, false);
                viewHolder = new TextViewHolder(convertView);
                break;
            case TYPE_THREEPIC://三张连图
                convertView = inflater.inflate(
                        R.layout.news_3_item_layout, parent, false);
                viewHolder = new VHThree(convertView);
                break;
            case TYPE_LEFTPIC://普通
                convertView = inflater.inflate(
                        R.layout.news_list_item_layout, parent, false);
                viewHolder = new VHLeftPic(convertView);
                break;
            case TYPE_BIGPIC:
                convertView = inflater.inflate(
                        R.layout.news_large_item_layout, parent, false);
                viewHolder = new VHBigPic(convertView);
                break;
            case TYPE_LARGE://大图
                convertView = inflater.inflate(
                        R.layout.news_large_item_layout, parent, false);
                viewHolder = new VHLargePic(convertView);
                break;
//            case TYPE_HOT_SEARCH://热搜
//                convertView = inflater.inflate(R.layout.card_hot_search, parent, false);
//                viewHolder = new HotSearch(convertView);
//                break;
            case TYPE_MY_AD://自己的广告
//                convertView = inflater.inflate(R.layout.card_hot_search, parent, false);
//                viewHolder = new MyAD(convertView);
                break;
            case TYPE_LARGE_DIAlOG://对话框
//                convertView = inflater.inflate(R.layout.card_hot_search, parent, false);
//                viewHolder = new MyAD(convertView);
                break;
            case TYPE_VEDIO://视频
                break;
            case TYPE_AD://广告
                convertView = inflater.inflate(
                        R.layout.news_list_ad_layout, parent, false);
                viewHolder = new AdHolder(convertView);
                break;
            case TYPE_LOADING://加载
                convertView = inflater.inflate(
                        R.layout.list_load_more_layout, parent, false);
                viewHolder = new LoadingHolder(convertView);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            NewsBean bean;
            int type = getItemViewType(position);
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.item_title, typedValue, true);
            int color = typedValue.data;
            switch (type) {
                case TYPE_LOADING:
                    Log.e("test", " TYPE_LOADING " + showLoading);
                    if (showLoading && callBack != null) {
                        callBack.loadMore();
                    }
                    break;
                case TYPE_TEXT:
                    bindText(holder, position, color);
                    break;
                case TYPE_THREEPIC:
                    bindThree(holder, position, color);
                    break;
                case TYPE_LEFTPIC:
                    bindLeft(holder, position, color);
                    break;
                case TYPE_AD:
                    bindAd(holder, position);
                    break;
                case TYPE_BIGPIC:
                    bean = list.get(position);
                    holder.itemView.setTag(bean);
                    break;
                case TYPE_LARGE:
                    bindLarge(holder, position, color);
                    break;
//                case TYPE_HOT_SEARCH:
//                    HotSearch hotSearch = (HotSearch) holder;
//                    bean = list.get(position);
//                    final String[] key_wrod = bean.getKey_wrod();
//                    for (int i = 0; i < hotSearch.title.length; i++) {
//                        final String word = key_wrod[i];
//                        hotSearch.title[i].setText(word);
//                        hotSearch.item[i].setOnClickListener(new OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (BuildConfig.DEBUG) {
//                                    TUtil.toast(context, word);
//                                }
//                                Intent intent = new Intent(context, SearchActivity.class);
//                                intent.putExtra("TAGCONNENT", word);
//                                context.startActivity(intent);
//                            }
//                        });
//                    }
//                    break;
                case TYPE_MY_AD:
//                    MyAD myAD = (MyAD) holder;
//                    bean = list.get(position);
                    break;
                case TYPE_VEDIO:

                case TYPE_LARGE_DIAlOG:
//                    MyAD myAD = (MyAD) holder;
//                    bean = list.get(position);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindLarge(RecyclerView.ViewHolder holder, final int position, int color) {
        final NewsBean bean;
        String from;
        String fav;
        String comcount;
        bean = list.get(position);
        holder.itemView.setTag(bean);
        VHLargePic vhLargePic = (VHLargePic) holder;
        if (isNewsitemUnlike) {
            vhLargePic.newsitem_unlike.setVisibility(View.VISIBLE);
        }

        vhLargePic.newsitem_unlike.setOnClickListener(new popAction(bean, position));
        vhLargePic.newsitem_title.setTextSize(fontSize);
        vhLargePic.newsitem_title.setText(bean.getTitle());
        if (readedNewsSet.contains(bean.getNid())) {
            vhLargePic.newsitem_title.setTextColor(App.getInstance()
                    .getResources().getColor(R.color.grey_font));
        } else {
            vhLargePic.newsitem_title.setTextColor(color);
        }

        SPUtil.setAtt(vhLargePic.item_type_iv, bean.getAttname());
        from = bean.getCopyfrom();
        if (!TextUtils.isEmpty(from)) {
            vhLargePic.newsitem_source.setText(from);
            vhLargePic.newsitem_source.setVisibility(View.VISIBLE);
        } else {
            vhLargePic.newsitem_source.setVisibility(View.GONE);
        }
        fav = bean.getFav();
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
        comcount = bean.getComcount();
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

        vhLargePic.nli_foot.setVisibility(View.GONE);

        if (!"0".equals(bean.getSid())) {
            vhLargePic.nli_foot.setImageResource(R.drawable.zq_subscript_issue);
            vhLargePic.nli_foot.setVisibility(View.VISIBLE);
        }
        SPUtil.setRtype(bean.getRtype(), vhLargePic.nli_foot);

        if (null != bean.getImgs() && bean.getImgs().length > 0) {
            SPUtil.displayImage(bean.getImgs()[0], vhLargePic.newsitem_img,
                    DisplayOptionFactory.Big.options);
        } else {
            SPUtil.displayImage("", vhLargePic.newsitem_img,
                    DisplayOptionFactory.Big.options);
            vhLargePic.newsitem_img.setVisibility(View.GONE);
        }
    }

    private void bindAd(RecyclerView.ViewHolder holder, int position) {
        NewsBean bean;
        try {
            bean = list.get(position);
            holder.itemView.setTag(bean);
            AdHolder adHolder = (AdHolder) holder;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindLeft(RecyclerView.ViewHolder holder, final int position, int color) {
        final NewsBean bean;
        String fav;
        String from;
        String comcount;
        bean = list.get(position);
        holder.itemView.setTag(bean);
        VHLeftPic vhLeftPic = (VHLeftPic) holder;
        if (isNewsitemUnlike) {
            vhLeftPic.newsitem_unlike.setVisibility(View.VISIBLE);
        }
        vhLeftPic.newsitem_unlike.setOnClickListener(new popAction(bean, position));
        vhLeftPic.newsitem_title.setTextSize(fontSize);
        vhLeftPic.newsitem_title.setText(bean.getTitle());

        vhLeftPic.newsitem_title.setTextColor(color);

        if (readedNewsSet.contains(bean.getNid())) {
            vhLeftPic.newsitem_title.setTextColor(App.getInstance()
                    .getResources().getColor(R.color.grey_font));
        } else {
            vhLeftPic.newsitem_title.setTextColor(color);
        }
        SPUtil.setAtt(vhLeftPic.item_type_iv, bean.getAttname());

        fav = bean.getFav();
        vhLeftPic.newsitem_collectcount.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(fav)) {
            int fav_counts = Integer.parseInt(fav);
            if (fav_counts > 0) {
                vhLeftPic.newsitem_collectcount.setText(String.valueOf(fav_counts));
                vhLeftPic.newsitem_collectcount.setVisibility(View.VISIBLE);
            }
        }

        from = bean.getCopyfrom();
        if (!TextUtils.isEmpty(from)) {
            vhLeftPic.newsitem_source.setText(from);
            vhLeftPic.newsitem_source.setVisibility(View.VISIBLE);
        } else {
            vhLeftPic.newsitem_source.setVisibility(View.GONE);
        }

        vhLeftPic.newsitem_commentcount.setVisibility(View.GONE);
        comcount = bean.getComcount();
        if (!TextUtils.isEmpty(comcount)) {
            int counts = Integer.parseInt(comcount);
            if (counts > 0) {
                bean.setComcount(String.valueOf(counts));
                vhLeftPic.newsitem_commentcount.setText(String.valueOf(counts));
                vhLeftPic.newsitem_commentcount.setVisibility(View.VISIBLE);
            }
        }

        if (CalendarUtil.friendlyTime(bean.getUpdate_time(), context) == null) {
            vhLeftPic.newsitem_time.setText("");
        } else {
            vhLeftPic.newsitem_time.setText(CalendarUtil.friendlyTime(bean.getUpdate_time(), context));
        }
        vhLeftPic.newsitem_img.setVisibility(View.VISIBLE);
        vhLeftPic.nli_foot.setVisibility(View.GONE);
        if (bean.getSid() != null && !"0".equals(bean.getSid())) {
            vhLeftPic.nli_foot.setImageResource(R.drawable.zq_subscript_issue);
            vhLeftPic.nli_foot.setVisibility(View.VISIBLE);
        }
        SPUtil.setRtype(bean.getRtype(), vhLeftPic.nli_foot);
        vhLeftPic.newsitem_img.setImageResource(R.drawable.default_bg);
        SPUtil.displayImage(bean.getImgs()[0], vhLeftPic.newsitem_img,
                DisplayOptionFactory.Small.options);
    }

    private void bindThree(RecyclerView.ViewHolder holder, final int position, int color) {
        final NewsBean bean;
        bean = list.get(position);
        VHThree vhThree = (VHThree) holder;
        if (isNewsitemUnlike) {
            vhThree.newsitem_unlike.setVisibility(View.VISIBLE);
        }
        holder.itemView.setTag(bean);
        vhThree.newsitem_unlike.setOnClickListener(new popAction(bean, position));
        vhThree.newsitem_title.setTextSize(fontSize);
        vhThree.newsitem_title.setText(bean.getTitle());
        if (readedNewsSet.contains(bean.getNid())) {
            vhThree.newsitem_title.setTextColor(App.getInstance()
                    .getResources().getColor(R.color.grey_font));
        } else {
            vhThree.newsitem_title.setTextColor(color);
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

    private void bindText(RecyclerView.ViewHolder holder, final int position, int color) {
        final NewsBean bean;
        bean = list.get(position);
        holder.itemView.setTag(bean);
        TextViewHolder textViewHolder = (TextViewHolder) holder;
        if (isNewsitemUnlike) {
            textViewHolder.newsitem_unlike.setVisibility(View.VISIBLE);
        }
        textViewHolder.newsitem_unlike.setOnClickListener(new popAction(bean, position));
        textViewHolder.newsitem_title.setTextSize(fontSize);
        textViewHolder.newsitem_title.setText(bean.getTitle());
        if (readedNewsSet.contains(bean.getNid())) {
            textViewHolder.newsitem_title.setTextColor(App.getInstance()
                    .getResources().getColor(R.color.grey_font));
        } else {
            textViewHolder.newsitem_title.setTextColor(color);
        }
        SPUtil.setAtt(textViewHolder.item_type_iv, bean.getAttname());

        String fav = bean.getFav();
        if (!TextUtils.isEmpty(fav)) {

            int fav_counts = Integer.parseInt(fav);
            if (fav_counts > 0) {
                textViewHolder.newsitem_collectcount.setVisibility(View.VISIBLE);
                textViewHolder.newsitem_collectcount.setText(fav_counts + "");
            } else {
                textViewHolder.newsitem_collectcount.setVisibility(View.GONE);
            }
        } else {
            textViewHolder.newsitem_collectcount.setVisibility(View.GONE);
        }

        String from = bean.getCopyfrom();
        if (!TextUtils.isEmpty(from)) {
            textViewHolder.newsitem_source.setVisibility(View.VISIBLE);
            textViewHolder.newsitem_source.setText(from);
        } else {
            textViewHolder.newsitem_source.setVisibility(View.GONE);
        }

        String comcount = bean.getComcount();
        if (!TextUtils.isEmpty(comcount)) {
            int counts = Integer.parseInt(comcount);
            if (counts > 0) {
                textViewHolder.newsitem_commentcount.setVisibility(View.VISIBLE);
                bean.setComcount(counts + "");
                textViewHolder.newsitem_commentcount.setText(counts + "");
            } else {
                textViewHolder.newsitem_commentcount.setVisibility(View.GONE);
            }
        } else {
            textViewHolder.newsitem_commentcount.setVisibility(View.GONE);
        }


        if (CalendarUtil.friendlyTime(bean.getUpdate_time(), context) == null) {
            textViewHolder.newsitem_time.setText("");
        } else {
            textViewHolder.newsitem_time.setText(CalendarUtil.friendlyTime(bean.getUpdate_time(), context));
        }
        textViewHolder.nli_foot.setVisibility(View.GONE);


        if (bean.getSid() != null && !"0".equals(bean.getSid())) {
            textViewHolder.nli_foot.setImageResource(R.drawable.zq_subscript_issue);
            textViewHolder.nli_foot.setVisibility(View.VISIBLE);
        }
        SPUtil.setRtype(bean.getRtype(), textViewHolder.nli_foot);
    }


    private class AdHolder extends RecyclerView.ViewHolder {
        ViewGroup viewGroup;

        public AdHolder(View itemView) {
            super(itemView);
            viewGroup = (ViewGroup) itemView.findViewById(R.id.ad_layout);
        }

    }

    public class LoadingHolder extends RecyclerView.ViewHolder {
        public LoadingHolder(View itemView) {
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
        private LinearLayout ll_tag;
        private View newsitem_unlike;

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
            ll_tag = (LinearLayout) v.findViewById(R.id.ll_tag);
            newsitem_unlike = v.findViewById(R.id.newsitem_unlike);
            v.setOnClickListener(onClickListener);
        }
    }

    //左边图片，右title，评论，时间，脚标
    private class VHLeftPic extends RecyclerView.ViewHolder {
        private TextView newsitem_title;
        private ImageView nli_foot;
        private TextView newsitem_source;//来源
        private TextView newsitem_collectcount;//收藏数
        private TextView newsitem_commentcount;//评论数
        private TextView newsitem_time;
        private ImageView newsitem_img;
        private View newsitem_unlike;
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
            newsitem_unlike = v.findViewById(R.id.newsitem_unlike);
            item_type_iv = (ImageView) v.findViewById(R.id.item_type_iv);
            v.setOnClickListener(onClickListener);
        }
    }

    //纯文本，评论，时间，脚标
    private class TextViewHolder extends RecyclerView.ViewHolder {
        private TextView newsitem_title;
        private ImageView nli_foot;
        private TextView newsitem_source;//来源
        private TextView newsitem_collectcount;//收藏数
        private TextView newsitem_commentcount;//评论数
        private TextView newsitem_time;
        private View newsitem_unlike;
        private ImageView item_type_iv;
        private LinearLayout ll_tag;

        public TextViewHolder(View v) {
            super(v);
            newsitem_title = (TextView) v.findViewById(R.id.newsitem_title);
            nli_foot = (ImageView) v.findViewById(R.id.nli_foot);
            newsitem_source = (TextView) v.findViewById(R.id.newsitem_source);
            newsitem_collectcount = (TextView) v.findViewById(R.id.newsitem_collectcount);
            newsitem_commentcount = (TextView) v.findViewById(R.id.newsitem_commentcount);
            newsitem_time = (TextView) v.findViewById(R.id.newsitem_time);
            newsitem_unlike = v.findViewById(R.id.newsitem_unlike);
            item_type_iv = (ImageView) v.findViewById(R.id.item_type_iv);
            ll_tag = (LinearLayout) v.findViewById(R.id.ll_tag);
            v.setOnClickListener(onClickListener);
        }
    }

    //大图
    private class VHLargePic extends RecyclerView.ViewHolder {
        private TextView newsitem_title;
        private ImageView nli_foot;
        private TextView newsitem_source; //来源
        private TextView newsitem_collectcount;//收藏数
        private TextView newsitem_commentcount;//评论数
        private TextView newsitem_time;
        private ImageView newsitem_img;
        private View newsitem_unlike;
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
            newsitem_unlike = v.findViewById(R.id.newsitem_unlike);
            item_type_iv = (ImageView) v.findViewById(R.id.item_type_iv);
            v.setOnClickListener(onClickListener);
        }
    }

    private class VHBigPic extends RecyclerView.ViewHolder {

        public VHBigPic(View v) {
            super(v);
        }
    }

    private PopupWindow popupWindow;
    private View dislike_tv;
    private View dislike_layout;

    /**
     * 初始化popWindow
     */
    private void initPopWindow() {
        View popView = inflater.inflate(R.layout.dislike_dialog_layout_no_items, null);
        popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        //设置popwindow出现和消失动画
        popupWindow.setAnimationStyle(R.style.PopMenuAnimation);
        dislike_layout = popView.findViewById(R.id.dislike_layout);
        dislike_tv = popView.findViewById(R.id.dislike_tv);
    }


    /**
     * 每个ITEM中more按钮对应的点击动作
     */
    public class popAction implements OnClickListener {
        int position;
        NewsBean bean;

        public popAction(NewsBean bean, int position) {
            this.position = position;
            this.bean = bean;
        }

        @Override
        public void onClick(View v) {
            if (bean.getType().equals("2") && (bean.getImgs() != null || bean.getImgs().length > 0)) {
                dislike_layout.setPadding(0, 0, App.px_170dp, 0);
            } else {
                dislike_layout.setPadding(0, 0, App.px_35dp, 0);
            }
            int[] arrayOfInt = new int[2];
            //获取点击按钮的坐标
            v.getLocationOnScreen(arrayOfInt);
            int x = arrayOfInt[0];
            int y = arrayOfInt[1];
            //设置popwindow显示位置
            popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, x, y);
            //获取popwindow焦点
            popupWindow.setFocusable(true);
            //设置popwindow如果点击外面区域，便关闭。
            popupWindow.setOutsideTouchable(true);
            popupWindow.update();
            if (popupWindow.isShowing()) {
                dislike_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeData(bean, position);
                        popupWindow.dismiss();
                    }
                });
            }
        }
    }

}

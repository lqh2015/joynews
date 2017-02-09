package com.hzpd.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.hzpd.custorm.CircleImageView;
import com.hzpd.hflt.R;
import com.hzpd.modle.NewsBean;
import com.hzpd.modle.VideoItemBean;
import com.hzpd.modle.XF_CommentBean;
import com.hzpd.modle.XF_UserCommNewsBean;
import com.hzpd.modle.XF_UserCommentsBean;
import com.hzpd.modle.XF_UserInfoBean;
import com.hzpd.ui.activity.NewsAlbumActivity;
import com.hzpd.ui.activity.NewsDetailActivity;
import com.hzpd.ui.activity.VideoPlayerActivity;
import com.hzpd.utils.AvoidOnClickFastUtils;
import com.hzpd.utils.CalendarUtil;
import com.hzpd.utils.DisplayOptionFactory;
import com.hzpd.utils.Log;
import com.hzpd.utils.SPUtil;

import java.util.ArrayList;
import java.util.List;

public class XF_UserCommentsAdapter extends RecyclerView.Adapter {

    private boolean flag = false;
    public boolean showLoading = false;
    private Context context;
    public static final int TYPE_HEAD = 0;
    private static final int TYPE_NORMAL = 1;
    final static int TYPE_LOADING = 2;
    private List<XF_UserCommentsBean> list;
    private LayoutInflater mInflater;
    XF_UserInfoBean userInfoBean;

    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }

    public XF_UserCommentsAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        list = new ArrayList<>();
    }

    public XF_UserCommentsAdapter(Context context, XF_UserInfoBean userInfoBean) {
        this.userInfoBean = userInfoBean;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        list = new ArrayList<>();
    }

    public void appendData(List<XF_UserCommentsBean> data, boolean isClearOld) {
        if (data == null) {
            return;
        }
        if (isClearOld) {
            list.clear();
        }
        list.addAll(data);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        } else if (showLoading && position + 1 == getItemCount()) {
            return TYPE_LOADING;
        } else {
            return TYPE_NORMAL;
        }

    }

    @Override
    public int getItemCount() {
        int count = 1;
        if (list != null) {
            count = count + list.size();
            if (showLoading) {
                count = count + 1;
            }
        }
        return count;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEAD) {
            View headView = mInflater.inflate(R.layout.xf_personal_headview,
                    parent, false);
            HeadViewHolder headingHolder = new HeadViewHolder(headView);
            headingHolder.xf_pinfo_iv_avatar = (CircleImageView) headView.findViewById(R.id.xf_pinfo_iv_avatar);
            headingHolder.xf_pinfo_tv_nickname = (TextView) headView.findViewById(R.id.xf_pinfo_tv_nickname);
            headingHolder.xf_pinfo_tv_level_alias = (TextView) headView.findViewById(R.id.xf_pinfo_tv_level_alias);
            headingHolder.xf_pinfo_tv_level = (TextView) headView.findViewById(R.id.xf_pinfo_tv_level);
            headingHolder.xf_pinfo_tv_score = (TextView) headView.findViewById(R.id.xf_pinfo_tv_score);
            headingHolder.xf_pinfo_npb = (NumberProgressBar) headView.findViewById(R.id.xf_pinfo_npb);
            headingHolder.xf_pinfo_tv_regtime = (TextView) headView.findViewById(R.id.xf_pinfo_tv_regtime);
            headingHolder.xf_pinfo_tv_levelup = (TextView) headView.findViewById(R.id.xf_pinfo_tv_levelup);
            return headingHolder;
        } else if (viewType == TYPE_NORMAL) {
            View view = mInflater.inflate(R.layout.mycomments_item_layout,
                    parent, false);

            return new NorViewHolder(view);
        } else if (viewType == TYPE_LOADING) {
            View view = mInflater.inflate(R.layout.list_load_more_layout,
                    parent, false);
            LoadingHolder loadingHolder = new LoadingHolder(view);
            return loadingHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int type = getItemViewType(position);
        switch (type) {
            case TYPE_HEAD: {
                HeadViewHolder headingHolder = (HeadViewHolder) holder;
                SPUtil.displayImage(userInfoBean.getAvatar_path()
                        , headingHolder.xf_pinfo_iv_avatar
                        , DisplayOptionFactory.Avatar.options);

                headingHolder.xf_pinfo_tv_nickname.setText(userInfoBean.getNickname());
                headingHolder.xf_pinfo_tv_level_alias.setText("" + userInfoBean.getAlias());
                headingHolder.xf_pinfo_tv_regtime.setText(context.getString(R.string.sgin_time) + userInfoBean.getRegtime());
            }
            break;
            case TYPE_NORMAL: {
                NorViewHolder norViewHolder = (NorViewHolder) holder;
                XF_UserCommentsBean bean = list.get(position - 1);
                final XF_UserCommNewsBean myCommentBean = bean.getContent();
                List<XF_CommentBean> commentBeanList = bean.getComs();
                Log.i("myCommentBean", "myCommentBean" + myCommentBean.getTitle());
                norViewHolder.mycoms_content_txt.setText(myCommentBean.getTitle());

                JumpActivity(norViewHolder, myCommentBean);

                if (myCommentBean.getImgs() != null && myCommentBean.getImgs().length > 0) {
                    SPUtil.displayImage(myCommentBean.getImgs()[0]
                            , norViewHolder.mycoms_img_id
                            , DisplayOptionFactory.Personal_center_News.options);
                } else {
                    norViewHolder.mycoms_img_id.setImageResource(R.drawable.urlicon_loadingpicture_dynamic);
                }

                norViewHolder.mycoms_ll.removeAllViews();

                for (XF_CommentBean itembean : bean.getComs()) {
                    View vi = mInflater.inflate(R.layout.mycomments_itemc_layout, null);
                    norViewHolder.mycoms_ll.addView(vi);
                    CircleImageView my_avatar = (CircleImageView) vi.findViewById(R.id.my_avatar);
                    TextView mycomment_news = (TextView) vi.findViewById(R.id.mycomment_news);
                    TextView mycomments_itemc_tv_content = (TextView) vi.findViewById(R.id.mycomments_itemc_tv_content);
                    TextView mycomments_itemc_tv_prise = (TextView) vi.findViewById(R.id.mycomments_itemc_tv_prise);
                    TextView mycomments_itemc_tv_sj_txt = (TextView) vi.findViewById(R.id.mycomments_itemc_tv_sj_txt);
                    TextView cm_item_tv_comstate = (TextView) vi.findViewById(R.id.cm_item_tv_comstate);
                    mycomment_news.setText(itembean.getNickname());
                    SPUtil.displayImage(itembean.getAvatar_path()
                            , my_avatar
                            , DisplayOptionFactory.Avatar.options);
                    mycomments_itemc_tv_content.setText(itembean.getContent());
                    mycomments_itemc_tv_sj_txt.setText(CalendarUtil.friendlyTime1(itembean.getDateline(), context));
                }

            }
            break;
            case TYPE_LOADING:
                break;
        }

    }

    public class LoadingHolder extends RecyclerView.ViewHolder {
        public LoadingHolder(View itemView) {
            super(itemView);
        }

    }

    private static class HeadViewHolder extends RecyclerView.ViewHolder {
        CircleImageView xf_pinfo_iv_avatar;//头像
        TextView xf_pinfo_tv_nickname;//昵称
        TextView xf_pinfo_tv_level_alias;//级别
        TextView xf_pinfo_tv_level;//级别
        TextView xf_pinfo_tv_score;//分数
        NumberProgressBar xf_pinfo_npb;//进度条
        TextView xf_pinfo_tv_regtime;//注册时间
        TextView xf_pinfo_tv_levelup;//升级提示

        HeadViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class NorViewHolder extends RecyclerView.ViewHolder {
        ImageView mycoms_img_id;
        LinearLayout my_newdetails;
        TextView mycoms_content_txt;
        LinearLayout mycoms_ll;

        public NorViewHolder(View v) {
            super(v);
            mycoms_img_id = (ImageView) v.findViewById(R.id.mycoms_img_id);
            my_newdetails = (LinearLayout) v.findViewById(R.id.my_newdetails);
            mycoms_content_txt = (TextView) v.findViewById(R.id.mycoms_content_txt);
            mycoms_ll = (LinearLayout) v.findViewById(R.id.mycoms_ll);
        }
    }

    private void JumpActivity(NorViewHolder norViewHolder, final XF_UserCommNewsBean myCommentBean) {
        norViewHolder.my_newdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AvoidOnClickFastUtils.isFastDoubleClick(v)) {
                    return;
                }
                Intent intent = new Intent();
                if ("1".equals(myCommentBean.getType())) {
                    intent.setClass(context, NewsDetailActivity.class);
                    NewsBean nb = new NewsBean();
                    nb.setNid(myCommentBean.getNid());
                    nb.setSid("0");
                    nb.setTitle(myCommentBean.getTitle());
                    nb.setJson_url(myCommentBean.getUrl());
                    nb.setType(myCommentBean.getType());
                    nb.setRtype(myCommentBean.getType());
                    nb.setTid(myCommentBean.getTid());
                    String[] imgs = {myCommentBean.getSmallimgurl()};
                    nb.setImgs(imgs);
                    nb.setUpdate_time(myCommentBean.getUpdate_time());
                    nb.setCopyfrom(myCommentBean.getCopyfrom());
                    nb.setFav(myCommentBean.getFav());
                    nb.setComcount(myCommentBean.getComcount());
                    intent.putExtra("newbean", nb);
                    intent.putExtra("from", "mycomments");
                    flag = true;

                } else if ("2".equals(myCommentBean.getType())) {
                    intent.setClass(context, NewsAlbumActivity.class);
                    intent.putExtra("from", "collection");
                    intent.putExtra("pid", myCommentBean.getNid());
                    intent.putExtra("json_url", myCommentBean.getUrl());
                    flag = true;
                } else if ("3".equals(myCommentBean.getType())
                        || "7".equals(myCommentBean.getType())) {
                    intent.setClass(context, NewsDetailActivity.class);

                    NewsBean nb = new NewsBean();
                    nb.setNid(myCommentBean.getNid());
                    nb.setSid("0");
                    nb.setTitle(myCommentBean.getTitle());
                    nb.setJson_url(myCommentBean.getUrl());
                    nb.setRtype(myCommentBean.getType());
                    nb.setType("1");
                    nb.setTid(myCommentBean.getTid());
                    String[] imgs = {myCommentBean.getSmallimgurl()};
                    nb.setImgs(imgs);
                    nb.setUpdate_time(myCommentBean.getUpdate_time());
                    nb.setCopyfrom(myCommentBean.getCopyfrom());
                    intent.putExtra("newbean", nb);
                    intent.putExtra("from", "mycomments");
                    flag = true;
                } else if ("4".equals(myCommentBean.getType())) {
                    intent.setClass(context, VideoPlayerActivity.class);
                    VideoItemBean vib = new VideoItemBean();
                    vib.setVid(myCommentBean.getNid());
                    vib.setTitle(myCommentBean.getTitle());
                    vib.setTime(myCommentBean.getUpdate_time());
                    vib.setMainpic(myCommentBean.getSmallimgurl());
                    vib.setJson_url(myCommentBean.getUrl());
                    intent.putExtra("from", "collection");
                    intent.putExtra("vib", vib);
                    flag = true;
                }
                if (!flag) {
                    return;
                }
                context.startActivity(intent);
            }
        });
    }
}
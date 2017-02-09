package com.hzpd.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.color.tools.mytools.LogUtils;
import com.hzpd.custorm.CircleImageView;
import com.hzpd.hflt.R;
import com.hzpd.modle.MyCommentBean;
import com.hzpd.modle.MycommentsBean;
import com.hzpd.modle.MycommentsitemBean;
import com.hzpd.modle.NewsBean;
import com.hzpd.modle.VideoItemBean;
import com.hzpd.ui.activity.NewsAlbumActivity;
import com.hzpd.ui.activity.NewsDetailActivity;
import com.hzpd.ui.activity.VideoPlayerActivity;
import com.hzpd.utils.AAnim;
import com.hzpd.utils.AvoidOnClickFastUtils;
import com.hzpd.utils.CalendarUtil;
import com.hzpd.utils.DisplayOptionFactory;
import com.hzpd.utils.Log;
import com.hzpd.utils.SPUtil;

public class MycommentsAdapter extends ListBaseAdapter<MycommentsBean> {

    private int size;

    private boolean flag = false;

    public MycommentsAdapter(Activity c) {
        super(c);
    }

    public MycommentsAdapter(Activity c, int size) {
        super(c);
        this.size = size;
    }

    private static class ViewHolder {
        ImageView mycoms_img_id;
        LinearLayout my_newdetails;
        TextView mycoms_content_txt;
        LinearLayout mycoms_ll;

        public ViewHolder(View v) {
            mycoms_img_id = (ImageView) v.findViewById(R.id.mycoms_img_id);
            my_newdetails = (LinearLayout) v.findViewById(R.id.my_newdetails);
            mycoms_content_txt = (TextView) v.findViewById(R.id.mycoms_content_txt);
            mycoms_ll = (LinearLayout) v.findViewById(R.id.mycoms_ll);
        }
    }


    @Override
    public View getMyView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.mycomments_item_layout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final MyCommentBean myCommentBean = list.get(position).getContent();
        holder.mycoms_content_txt.setText(myCommentBean.getTitle());
        String[] imgs = myCommentBean.getImgs();
        if (imgs.length > 0) {
            SPUtil.displayImage(myCommentBean.getImgs()[0]
                    , holder.mycoms_img_id
                    , DisplayOptionFactory.Personal_center_News.options);
        } else {
            holder.mycoms_img_id.setVisibility(View.GONE);
            holder.mycoms_img_id.setImageResource(R.drawable.urlicon_loadingpicture_dynamic);
        }

        holder.my_newdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AvoidOnClickFastUtils.isFastDoubleClick(v))
                    return;

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
//                    nb.setComcount("1");
                    Log.e("test", "test--->" + nb.toString());
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
                AAnim.ActivityStartAnimation(context);
            }
        });


        MycommentsBean bean = list.get(position);

        holder.mycoms_ll.removeAllViews();

        for (MycommentsitemBean itembean : bean.getComs()) {
            View vi = inflater.inflate(R.layout.mycomments_itemc_layout, null);
            holder.mycoms_ll.addView(vi);
            CircleImageView my_avatar = (CircleImageView) vi.findViewById(R.id.my_avatar);
            TextView mycomment_news = (TextView) vi.findViewById(R.id.mycomment_news);
            TextView mycomments_itemc_tv_content = (TextView) vi.findViewById(R.id.mycomments_itemc_tv_content);
            TextView mycomments_itemc_tv_prise = (TextView) vi.findViewById(R.id.mycomments_itemc_tv_prise);
            TextView mycomments_itemc_tv_sj_txt = (TextView) vi.findViewById(R.id.mycomments_itemc_tv_sj_txt);
            TextView cm_item_tv_comstate = (TextView) vi.findViewById(R.id.cm_item_tv_comstate);

            SPUtil.displayImage(itembean.getAvatar_path()
                    , my_avatar
                    , DisplayOptionFactory.Avatar.options);
            mycomment_news.setText("" + itembean.getNickname());
            mycomments_itemc_tv_content.setText(itembean.getContent());
            mycomments_itemc_tv_sj_txt.setText(CalendarUtil.friendlyTime1(itembean.getDateline(), context));
//
            if ("-2".equals(itembean.getStatus())) {
                cm_item_tv_comstate.setVisibility(View.VISIBLE);
            } else {
                cm_item_tv_comstate.setVisibility(View.GONE);
            }
        }


        return convertView;
    }


}
package com.hzpd.adapter;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hzpd.custorm.CircleImageView;
import com.hzpd.custorm.PopUpwindowLayout;
import com.hzpd.hflt.R;
import com.hzpd.modle.CommentzqzxBean;
import com.hzpd.ui.activity.XF_PInfoActivity;
import com.hzpd.ui.activity.ZQ_ReplyCommentActivity;
import com.hzpd.utils.AvoidOnClickFastUtils;
import com.hzpd.utils.CalendarUtil;
import com.hzpd.utils.DisplayOptionFactory;
import com.hzpd.utils.Log;
import com.hzpd.utils.SPUtil;
import com.hzpd.utils.SharePreferecesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuqing on 2015/8/13.
 */
public class CommentListAdapter extends BaseAdapter {

    private List<CommentzqzxBean> mDataList = new ArrayList<>();
    private SPUtil spu;
    private String nid;


    public CommentListAdapter(String nid) {
        this.nid = nid;
        spu = SPUtil.getInstance();
    }

    @Override
    public int getCount() {
        return mDataList.isEmpty() ? 0 : (mDataList.size() + 1);
    }


    @Override
    public CommentzqzxBean getItem(int position) {
        if (position == 0) {
            return null;
        } else {
            return mDataList.get(position - 1);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if (position == 0) {
            if (convertView == null || convertView.getId() != R.id.details_related_comments) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.details_related_comments, parent, false);
            }
        } else {
            final ViewHolder holder;
            if (convertView == null || convertView.getId() != R.id.root_layout) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.details_comment_list_item, parent, false);
                holder = new ViewHolder();
                holder.comment_user_icon = (CircleImageView) convertView.findViewById(R.id.comment_user_icon);
                holder.comment_user_name = (TextView) convertView.findViewById(R.id.comment_user_name);
                holder.comment_text = (TextView) convertView.findViewById(R.id.comment_text);
                holder.comment_time = (TextView) convertView.findViewById(R.id.comment_time);
                holder.up_icon = (ImageView) convertView.findViewById(R.id.up_icon);
                holder.comment_up_num = (TextView) convertView.findViewById(R.id.comment_up_num);
                holder.rl_comment_text = convertView.findViewById(R.id.rl_comment_text);
                holder.line = convertView.findViewById(R.id.line);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final CommentzqzxBean item = getItem(position);
            if (position == 1) {
                holder.line.setVisibility(View.GONE);
            }
            holder.userId = item.getUid();
            // 显示头像
            SPUtil.displayImage(item.getAvatar_path()
                    , holder.comment_user_icon
                    , DisplayOptionFactory.XF_Avatar.options);
            holder.comment_user_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent();
                    mIntent.putExtra("uid", item.getUid()); //TODO
                    mIntent.setClass(parent.getContext(), XF_PInfoActivity.class);
                    parent.getContext().startActivity(mIntent);
                }
            });

            // 用户名
            holder.comment_user_name.setText(item.getNickname());
            holder.comment_user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent();
                    mIntent.putExtra("uid", item.getUid()); //TODO
                    mIntent.setClass(parent.getContext(), XF_PInfoActivity.class);
                    parent.getContext().startActivity(mIntent);
                }
            });

            // 评论内容
            holder.comment_text.setText(item.getContent());
            holder.comment_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AvoidOnClickFastUtils.isFastDoubleClick(v)) {
                        return;
                    }
                    ArrayList<String> titles = new ArrayList<String>();
                    titles.add(parent.getContext().getResources().getString(R.string.reply_comment));
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_popupwindow, null);
                    PopUpwindowLayout popUpwindowLayout = (PopUpwindowLayout) view.findViewById(R.id.llayout_popupwindow);
                    popUpwindowLayout.initViews(parent.getContext(), titles, false);
                    final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int popupWidth = view.getMeasuredWidth();
                    int popupHeight = view.getMeasuredHeight();
                    int[] location = new int[2];
                    // 允许点击外部消失
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setFocusable(true);
                    // 获得位置
                    v.getLocationOnScreen(location);
                    popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
                    popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
                    popUpwindowLayout.setClickListener(new PopUpwindowLayout.OnClickCallback() {

                        @Override
                        public void onItemClick(LinearLayout parentView, int size, int index) {
                            switch (index) {
                                case 0:
                                    Intent intent = new Intent(parent.getContext(), ZQ_ReplyCommentActivity.class);
                                    intent.putExtra("USER_UID", item.getCid());
                                    parent.getContext().startActivity(intent);
                                    popupWindow.dismiss();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });

                }
            });
            // 评论时间
            holder.comment_time.setText(CalendarUtil.friendlyTime1(item.getDateline(), parent.getContext()));
            if (SharePreferecesUtils.getParam(parent.getContext(), "" + item.getCid(), "0").toString().equals("1")) {
                holder.up_icon.setImageResource(R.drawable.details_icon_likeit);
                holder.up_icon.setEnabled(false);
            }

            // 点赞数
            holder.comment_up_num.setText(item.getPraise());
            holder.up_icon.setTag(item);
            holder.up_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        holder.up_icon.setEnabled(false);
                        Log.e("holder.digNum", "holder.digNum");
                        if (null == spu.getUser()) {
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        return convertView;
    }

    public void appendData(List<CommentzqzxBean> data) {
        if (data != null && !data.isEmpty()) {
            mDataList.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void appendData(List<CommentzqzxBean> data, boolean isClearOld) {
        if (isClearOld) {
            mDataList.clear();
        }
        if (data != null && !data.isEmpty()) {
            mDataList.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void insertData(List<CommentzqzxBean> data, int positon) {
        if (data != null && !data.isEmpty()) {
            mDataList.addAll(positon, data);
            notifyDataSetChanged();
        }
    }

    public String getLogTag() {
        return getClass().getSimpleName();
    }

    public static class ViewHolder {
        public String userId = "";
        public CircleImageView comment_user_icon;
        public TextView comment_user_name;
        public TextView comment_text;
        public TextView comment_time;
        public ImageView up_icon;
        private TextView comment_up_num;
        private View rl_comment_text;
        private View line;

    }
}
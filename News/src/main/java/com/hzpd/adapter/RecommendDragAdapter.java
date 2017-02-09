package com.hzpd.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzpd.hflt.R;
import com.hzpd.modle.NewsChannelBean;
import com.hzpd.utils.Log;

import java.util.List;

public class RecommendDragAdapter extends BaseAdapter {
    /**
     * TAG
     */
    private final static String TAG = "DragAdapter";
    /**
     * 是否显示底部的ITEM
     */
    private boolean isItemShow = false;
    private Context context;
    /**
     * 控制的postion
     */
    private int holdPosition;
    /**
     * 是否改变
     */
    private boolean isChanged = false;
    /**
     * 列表数据是否改变
     */
    private boolean isListChanged = false;
    /**
     * 是否可见
     */
    boolean isVisible = true;
    /**
     * 可以拖动的列表（即用户选择的频道列表）
     */
    public List<NewsChannelBean> channelList;
    /**
     * TextView 频道内容
     */
    private TextView item_text;

    private ImageView iv_delete;
    /**
     * 要删除的position
     */
    public int remove_position = -1;
    private List<NewsChannelBean> titleData;

    public RecommendDragAdapter(Context context, List<NewsChannelBean> channelList) {
        this.context = context;
        this.channelList = channelList;
    }

    boolean animFlag = false;

    public void setList(List<NewsChannelBean> titleData) {
        this.titleData = titleData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return channelList == null ? 0 : channelList.size();
    }

    public void setAnim(boolean animFlag) {
        this.animFlag = animFlag;
    }

    @Override
    public NewsChannelBean getItem(int position) {
        // TODO Auto-generated method stub
        if (channelList != null && channelList.size() != 0) {
            return channelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.channel_item, null);
        try {
            item_text = (TextView) view.findViewById(R.id.text_item);
            iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
            if (isEdit) {
                iv_delete.setVisibility(View.VISIBLE);
            } else {
                iv_delete.setVisibility(View.GONE);
            }

            NewsChannelBean channel = getItem(position);
            item_text.setText(channel.getCnname().toUpperCase());
            //控制不可点击栏目颜色
            if ((position == 0) || (position == 1)) {
                //			item_text.setTextColor(context.getResources().getColor(R.color.black));
                item_text.setEnabled(false);
                iv_delete.setVisibility(View.GONE);
            }
            if (isChanged && (position == holdPosition) && !isItemShow) {
                Log.e("isChanged", "isChanged");
                item_text.setText("");
                item_text.setSelected(true);
                item_text.setEnabled(true);
                isChanged = false;
            }
            if (!isVisible && (position == -1 + channelList.size())) {
                Log.e("isVisible", "isVisible");
                item_text.setText("");
                item_text.setSelected(true);
                item_text.setEnabled(true);
            }
            if (remove_position == position) {
                Log.e("remove_position", "remove_position");
                item_text.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private boolean isEdit;

    public void isEditItem(boolean isEdit) {
        this.isEdit = isEdit;
        Log.e("adapter", "isEditItem" + isEdit);

        notifyDataSetChanged();
    }

    /**
     * 添加频道列表
     */
    public void addItem(NewsChannelBean channel) {
        channelList.add(channel);
        isListChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 拖动变更频道排序
     */
    public void exchange(int dragPostion, int dropPostion) {
        holdPosition = dropPostion;
        NewsChannelBean dragItem = getItem(dragPostion);
        Log.d(TAG, "startPostion=" + dragPostion + ";endPosition=" + dropPostion);
        if (dragPostion < dropPostion) {
            channelList.add(dropPostion + 1, dragItem);
            channelList.remove(dragPostion);
        } else {
            channelList.add(dropPostion, dragItem);
            channelList.remove(dragPostion + 1);
        }
        isChanged = true;
        isListChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 获取频道列表
     */
    public List<NewsChannelBean> getChannnelLst() {
        return channelList;
    }

    /**
     * 设置删除的position
     */
    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
    }

    /**
     * 删除频道列表
     */
    public void remove() {
        channelList.remove(remove_position);
        remove_position = -1;
        isListChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 设置频道列表
     */
    public void setListDate(List<NewsChannelBean> list) {
        channelList = list;
    }

    /**
     * 获取是否可见
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * 排序是否发生改变
     */
    public boolean isListChanged() {
        return isListChanged;
    }

    /**
     * 设置是否可见
     */
    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    /**
     * 显示放下的ITEM
     */
    public void setShowDropItem(boolean show) {
        isItemShow = show;
    }
}
package com.hzpd.adapter.editcolumn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzpd.hflt.R;
import com.hzpd.modle.NewsChannelBean;

import java.util.ArrayList;
import java.util.List;

public class LastEditColumnAdapter extends BaseAdapter {
    private List<NewsChannelBean> list;
    private LayoutInflater inflater;

    private Context context;
    private boolean animFlag = false;
    ;//是否隐藏最后一个item

    public LastEditColumnAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        list = new ArrayList<NewsChannelBean>();

    }

    public void setList(List<NewsChannelBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void addData(NewsChannelBean nb) {
        list.add(nb);
//		setAnim(true);
        notifyDataSetChanged();
    }

    public void setAnim(boolean animFlag) {
        this.animFlag = animFlag;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private boolean isEdit;

    public void isEditItem(boolean isEdit) {
        this.isEdit = isEdit;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.channel_item, parent, false);
        ImageView iv_add = (ImageView) convertView.findViewById(R.id.iv_add);
        if (isEdit){
            iv_add.setVisibility(View.VISIBLE);
        }else {
            iv_add.setVisibility(View.GONE);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.text_item);
        NewsChannelBean ncb = list.get(position);
        String cname = ncb.getCnname();
        if (cname.length() > 3) {
            tv.setTextSize(14);
        } else {
            tv.setTextSize(18);
        }
        tv.setText(cname);

        if (position == list.size() - 1 && animFlag) {
            convertView.setVisibility(View.INVISIBLE);
        } else {
            convertView.setVisibility(View.VISIBLE);
        }

//		if(ncb.isClickable()){
//			tv.setTextColor(context.getResources().getColor(R.color.black));
//		}else{
//			tv.setTextColor(context.getResources().getColor(R.color.editColumn_fontgrey));
//		}
        return convertView;
    }

    /**
     * 是否可见
     */
    boolean isVisible = true;

    /**
     * 设置是否可见
     */
    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
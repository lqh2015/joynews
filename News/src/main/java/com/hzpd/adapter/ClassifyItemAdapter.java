package com.hzpd.adapter;import android.content.Context;import android.support.v7.widget.RecyclerView;import android.util.DisplayMetrics;import android.util.TypedValue;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.FrameLayout;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.TextView;import com.hzpd.hflt.R;import com.hzpd.modle.TagBean;import com.hzpd.ui.App;import com.hzpd.utils.DisplayOptionFactory;import com.hzpd.utils.SPUtil;import java.util.ArrayList;import java.util.List;public class ClassifyItemAdapter extends RecyclerView.Adapter {    private LayoutInflater mInflater;    private View.OnClickListener onClickListener;    public List<TagBean> list = null;    private Context context;    public ClassifyItemAdapter(Context context, View.OnClickListener onClickListener) {        this.context = context;        this.mInflater = LayoutInflater.from(context);        this.onClickListener = onClickListener;        list = new ArrayList<>();    }    public class HeadViewHolder extends RecyclerView.ViewHolder {        public HeadViewHolder(View v) {            super(v);            DisplayMetrics displayMetrics = App.getInstance().getResources().getDisplayMetrics();            int itemWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 95, displayMetrics);            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(displayMetrics.widthPixels / 2 - itemWidth / 2, ViewGroup.LayoutParams.MATCH_PARENT);            v.setLayoutParams(params);        }    }    public class ItemViewHolder extends RecyclerView.ViewHolder {        public ItemViewHolder(View v) {            super(v);            v.setTag(this);        }        public int postion;        public TagBean tagBean;        public View classify_item;        public ImageView mImg;        public TextView mTxt;    }    public void appendData(List<TagBean> data) {        if (data == null) {            return;        }        list.clear();        list.addAll(data);        notifyDataSetChanged();    }    @Override    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {        RecyclerView.ViewHolder viewHolder = null;        switch (viewType) {            case TYPE_HEAD: {                View view = new FrameLayout(parent.getContext());                HeadViewHolder headViewHolder = new HeadViewHolder(view);                viewHolder = headViewHolder;            }            break;            default: {                View view = mInflater.inflate(R.layout.classify_item_layout, parent, false);                ItemViewHolder itemViewHolder = new ItemViewHolder(view);                itemViewHolder.classify_item = view.findViewById(R.id.classify_item);                itemViewHolder.mImg = (ImageView) view.findViewById(R.id.id_index_gallery_item_image);                itemViewHolder.mTxt = (TextView) view.findViewById(R.id.id_index_gallery_item_text);                viewHolder = itemViewHolder;            }            break;        }        return viewHolder;    }    @Override    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {        if (position == 0 || position == getItemCount() - 1) {            return;        }        --position;        final ItemViewHolder viewHolder = (ItemViewHolder) holder;        final TagBean tagBean = list.get(position);        if (last != null && tagBean.getName().equals(last.tagBean.getName())) {            viewHolder.mImg.setLayoutParams(SPUtil.LARGE);            last = viewHolder;        } else {            viewHolder.mImg.setLayoutParams(SPUtil.NORMAL);            viewHolder.classify_item.setSelected(false);        }        if (tagBean.getName() != null) {            viewHolder.mTxt.setText(tagBean.getName());        }        if (tagBean.getIcon() != null) {            SPUtil.displayImage(tagBean.getIcon(), viewHolder.mImg                    , DisplayOptionFactory.Classify_Item_Tag.options);        }        viewHolder.postion = position;        viewHolder.tagBean = tagBean;        viewHolder.classify_item.setTag(viewHolder);        viewHolder.classify_item.setOnClickListener(onClickListener);        if (last == null && position == 0) {            viewHolder.classify_item.setSelected(true);            viewHolder.mImg.setLayoutParams(SPUtil.LARGE);            last = viewHolder;        }    }    public ItemViewHolder last;    public static final int TYPE_HEAD = 1;    public static final int TYPE_CATEGORY = 2;    @Override    public int getItemViewType(int position) {        return (position == 0 || position == getItemCount() - 1) ? TYPE_HEAD : TYPE_CATEGORY;    }    @Override    public int getItemCount() {        if (list.size() > 0) {            return list.size() + 2;        } else {            return 0;        }    }}
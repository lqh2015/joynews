package com.hzpd.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.color.tools.mytools.LogUtils;
import com.hzpd.hflt.R;
import com.hzpd.modle.event.HistoryClearEvent;
import com.hzpd.modle.event.SearchKeyEvent;
import com.hzpd.ui.fragments.SearchKeyFragment;
import com.hzpd.utils.AvoidOnClickFastUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


public class SearchKeysAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    int[] colors;
    public static final String TAG = SearchKeysAdapter.class.getSimpleName();

    private List<String> keysList = new ArrayList<>();
    private List<String> historyList = new ArrayList<>();

    public SearchKeysAdapter() {
        colors = new int[]{0xff4aa8d8, 0xff02d1b1, 0xffffffff, 0xff56bc8a, 0xff3bbae7};
    }

    final static int TYPE_KEY_HEAD = 0x10;
    final static int TYPE_KEY = 0x20;
    final static int TYPE_HISTORY_HEAD = 0x30;
    final static int TYPE_HISTORY = 0x40;
    final static int TYPE_HISTORY_END = 0x50;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.history_head_clear:
                // 清除历史记录
                EventBus.getDefault().post(new HistoryClearEvent());
                break;
        }
    }

    protected class KeyHeadHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public KeyHeadHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.key_head_title);
        }

    }

    protected class HistoryHeadHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView clearView;

        public HistoryHeadHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.history_head_title);
            clearView = (TextView) itemView.findViewById(R.id.history_head_clear);
            clearView.setOnClickListener(SearchKeysAdapter.this);
        }
    }


    protected class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(View itemView) {
            super(itemView);
            search_key_llayout = itemView.findViewById(R.id.search_key_llayout);
            search_position = (TextView) itemView.findViewById(R.id.search_position);
            textView = (TextView) itemView.findViewById(R.id.search_key);
            search_key_llayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AvoidOnClickFastUtils.isFastDoubleClick(view)) {
                        return;
                    }
                    EventBus.getDefault().post(new SearchKeyEvent("" + textView.getText()));
                }
            });
        }

        public TextView textView;
        public TextView search_position;
        private View search_key_llayout;
    }

    protected class HistoryHolder extends RecyclerView.ViewHolder {
        public HistoryHolder(View itemView) {
            super(itemView);
            llayout = itemView.findViewById(R.id.llayout);
            textView = (TextView) itemView.findViewById(R.id.history_text);
            llayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new SearchKeyEvent("" + textView.getText()));
                }
            });
        }

        public TextView textView;
        private View llayout;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder value = null;
        switch (viewType) {
            case TYPE_KEY_HEAD: {
                KeyHeadHolder holder = null;
                Context context = parent.getContext();
                View view = LayoutInflater.from(context).inflate(R.layout.key_head_layout, parent, false);
                holder = new KeyHeadHolder(view);
                value = holder;
            }
            break;
            case TYPE_KEY: {
                ItemViewHolder holder = null;
                Context context = parent.getContext();
                View view = LayoutInflater.from(context).inflate(R.layout.search_key_layout, parent, false);
                holder = new ItemViewHolder(view);
                value = holder;
            }
            break;
            case TYPE_HISTORY_HEAD: {
                HistoryHeadHolder holder = null;
                Context context = parent.getContext();
                View view = LayoutInflater.from(context).inflate(R.layout.history_head_layout, parent, false);
                holder = new HistoryHeadHolder(view);
                value = holder;
            }
            break;
            case TYPE_HISTORY: {
                HistoryHolder holder = null;
                Context context = parent.getContext();
                View view = LayoutInflater.from(context).inflate(R.layout.history_item_layout, parent, false);
                holder = new HistoryHolder(view);
                value = holder;
            }
            break;
            case TYPE_HISTORY_END: {
                HistoryHeadHolder holder = null;
                Context context = parent.getContext();
                View view = LayoutInflater.from(context).inflate(R.layout.history_head_layout, parent, false);
                holder = new HistoryHeadHolder(view);
                value = holder;
            }
            break;
        }
        return value;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder sHolder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_KEY_HEAD:
                break;
            case TYPE_KEY: {
                ItemViewHolder holder = (ItemViewHolder) sHolder;
                holder.textView.setText(keysList.get(position - 1));
                holder.search_position.setText("" + position);
                if (position == 1) {
                    holder.search_position.setBackgroundResource(R.color.search_position_1);
                } else if (position == 2) {
                    holder.search_position.setBackgroundResource(R.color.search_position_2);
                } else if (position == 3) {
                    holder.search_position.setBackgroundResource(R.color.search_position_3);
                }
//				holder.textView.setBackgroundColor(colors[(position - 1) % colors.length]);
            }
            break;
            case TYPE_HISTORY_HEAD:
                break;
            case TYPE_HISTORY: {
                HistoryHolder holder = (HistoryHolder) sHolder;
                int pos = position - 1 - keysList.size() - 1;
                holder.textView.setText(historyList.get(pos));
            }
            break;
            case TYPE_HISTORY_END:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int count = 1;
        if (position < count) {
            return TYPE_KEY_HEAD;
        }
        count += keysList.size();
        if (position < count) {
            return TYPE_KEY;
        }
        count += 1;
        if (position < count) {
            return TYPE_HISTORY_HEAD;
        }
        count += historyList.size();
        if (position < count) {
            return TYPE_HISTORY;
        }
        count += 1;
        if (position < count) {
            return TYPE_HISTORY_END;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return 1 + keysList.size() + 1 + historyList.size();
    }

    private GridLayoutManager.SpanSizeLookup spanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            int span = SearchKeyFragment.COUNT_COLUMS;
            int type = getItemViewType(position);
            switch (type) {
                case TYPE_KEY:
                    span = 1;
                    break;
            }
            return span;
        }
    };

    public GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        return spanSizeLookup;
    }

    /**
     * 重新加载数据
     */
    public void setData(List<String> data) {
        if (data != null) {
            keysList.clear();
            keysList.addAll(data);
            notifyDataSetChanged();
        }
    }

    /**
     * 加载历史数据
     */
    public void setHistory(List<String> data) {
        if (data != null) {
            historyList.clear();
            historyList.addAll(data);
            LogUtils.e("历史数据" + data.toString());
            notifyDataSetChanged();
        }
    }

    /**
     * 追加搜索历史数据
     */
    public void addHistory(List<String> data) {
        if (data != null && !data.isEmpty()) {
            int start = getItemCount() - historyList.size();
            historyList.addAll(data);
            notifyItemRangeInserted(start, data.size());
        }
    }

    public String getLogTag() {
        return this.getClass().getSimpleName();
    }

}

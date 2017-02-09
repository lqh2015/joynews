package com.hzpd.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.hzpd.hflt.R;
import com.hzpd.ui.activity.NewsDetailActivity;

/**
 */
public class NewsDetailAdapter extends RecyclerView.Adapter {

    final int TYPE_HEAD = 0x11;
    final int TYPE_WEBVIEW = 0x56;
    final int TYPE_FOOTER = 0x72;
    final int TYPE_ITEM = 0x121;
    final int TYPE_COMMRNT = 0x242;//评论

    NewsDetailActivity context;

    public NewsDetailAdapter(Context context) {
        this.context = (NewsDetailActivity) context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        } else if (position == 1) {
            return TYPE_WEBVIEW;
        } else if (position==2){
            return TYPE_FOOTER;
        }else {
            return TYPE_COMMRNT;
        }
//        else {
//            return TYPE_ITEM;
//        }
//        return position == 1 ? TYPE_WEBVIEW : TYPE_ITEM;

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_HEAD:
                View viewHead = LayoutInflater.from(context).inflate(R.layout.details_news_head, parent, false);
                context.setDetailsHead(viewHead);
                viewHolder = new HeadViewHolder(viewHead);
                break;
            case TYPE_WEBVIEW: {
                ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.webview_layout, parent, false);
                WebView webView = (WebView) viewGroup.findViewById(R.id.webview);
                (context).setWebview(webView);
                viewHolder = new WebViewHolder(viewGroup);
            }
            break;
            case TYPE_FOOTER: {
                View viewFooter = LayoutInflater.from(context).inflate(R.layout.details_related_news, parent, false);
                context.setFooterview(viewFooter);
                viewHolder = new FooterViewHolder(viewFooter);
            }
            break;
            case TYPE_COMMRNT:
                View commentView = LayoutInflater.from(context).inflate(R.layout.news_detail_comment, parent, false);
//                context.setFooterview(viewFooter);
                context.setCommentView(commentView);
                viewHolder = new FooterViewHolder(commentView);
                break;
//            case TYPE_ITEM: {
//                ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.fixed_listview_layout, parent, false);
//                ListView view = (ListView) viewGroup.findViewById(R.id.fixed_listview);
//                view.setDividerHeight(0); //  dividerHeight
//                context.setListView(view);
//                viewHolder = new ItemViewHolder(viewGroup);
//            }
//            break;

        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }


    class HeadViewHolder extends RecyclerView.ViewHolder {

        public HeadViewHolder(View itemView) {
            super(itemView);
        }
    }

    class WebViewHolder extends RecyclerView.ViewHolder {

        public WebViewHolder(View itemView) {
            super(itemView);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}

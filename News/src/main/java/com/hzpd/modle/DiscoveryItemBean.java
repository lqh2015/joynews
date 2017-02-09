package com.hzpd.modle;

import java.util.List;

public class DiscoveryItemBean {
    private List<NewsBean> news;
    private TagBean tag;

    @Override
    public String toString() {
        return "DiscoveryItemBean{" +
                "news=" + news +
                ", tag=" + tag +
                '}';
    }

    public List<NewsBean> getNews() {
        return news;
    }

    public void setNews(List<NewsBean> news) {
        this.news = news;
    }

    public TagBean getTag() {
        return tag;
    }

    public void setTag(TagBean tag) {
        this.tag = tag;
    }
}

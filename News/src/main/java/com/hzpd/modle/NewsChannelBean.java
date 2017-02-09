package com.hzpd.modle;

import android.text.TextUtils;

import com.hzpd.modle.db.NewsChannelBeanDB;
import com.hzpd.url.InterfaceJsonfile;

import java.io.Serializable;

public class NewsChannelBean implements Comparable<NewsChannelBean>, Serializable {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_RECOMMEND = 0x9999;

    private static final long serialVersionUID = 1L;

    private String tid;//": "16",
    private String cnname;//": "898195fb",
    private String sort_order;//": "11",
    private String fid;//": "14",
    private String path;//": "0-14",
    private String source;//": "http://www.tianshannet.com/wap/rss/toutiao.xml",
    private String style;//": "2",
    private String status;//": "1",
    private String siteid = InterfaceJsonfile.SITEID;//": "1"
    private String default_show;


    private int type = TYPE_NORMAL;
    private String icon;
    private String id = "";
    private String name;
    private String num;

    public NewsChannelBean() {

    }

    public NewsChannelBean(NewsChannelBeanDB bean) {
        setTid(bean.getTid());
        setCnname(bean.getCnname());
        setSort_order(bean.getSort_order());
        setFid(bean.getFid());
        setPath(bean.getPath());
        setSource(bean.getSource());
        setStyle(bean.getStyle());
        setStatus(bean.getStatus());
        setSiteid(bean.getSiteid());
        setDefault_show(bean.getDefault_show());
        setIcon(bean.getIcon());
        setId(bean.getTagid());
        setName(bean.getName());
        setNum(bean.getNum());
        setType(bean.getType());
    }

    @Override
    public String toString() {
        return "NewsChannelBean{" +
                "tid='" + tid + '\'' +
                ", cnname='" + cnname + '\'' +
                ", sort_order='" + sort_order + '\'' +
                ", siteid='" + siteid + '\'' +
                ", default_show='" + default_show + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", num='" + num + '\'' +
                ", type=" + type +
                '}';
    }

    public String gerKey() {
        // 保存数据的Key 请勿修改
        return "_" + siteid + ',' + tid + ',' + id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null) {
            id = "";
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getDefault_show() {
        return default_show;
    }

    public void setDefault_show(String default_show) {
        this.default_show = default_show;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTid() {
        return tid;
    }

    public String getCnname() {
        return cnname;
    }

    public String getSort_order() {
        return sort_order;
    }

    public String getFid() {
        return fid;
    }

    public String getPath() {
        return path;
    }

    public String getSource() {
        return source;
    }

    public String getStyle() {
        return style;
    }

    public String getStatus() {
        return status;
    }

    public String getSiteid() {
        return siteid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public void setCnname(String cnname) {
        this.cnname = cnname;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSiteid(String siteid) {
        this.siteid = siteid;
    }

    @Override
    public int compareTo(NewsChannelBean another) {
        int my = Integer.parseInt(sort_order);
        int an = Integer.parseInt(another.getSort_order());
        if (my > an) {
            return 1;
        } else if (my < an) {
            return -1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NewsChannelBean) {
            NewsChannelBean another = (NewsChannelBean) o;
            return !TextUtils.isEmpty(getTid()) && getTid().equals(another.getTid());
        }
        return super.equals(o);
    }

}

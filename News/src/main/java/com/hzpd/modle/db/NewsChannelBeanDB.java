package com.hzpd.modle.db;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import com.hzpd.modle.NewsChannelBean;
import com.hzpd.modle.TagBean;

/**
 * Entity mapped to table "NEWS_CHANNEL_BEAN_DB".
 */
public class NewsChannelBeanDB {

    private Long id;
    private String tid;
    private String cnname;
    private String sort_order;
    private String fid;
    private String path;
    private String source;
    private String style;
    private String status;
    private String siteid;
    private String default_show;
    private String icon;
    private String tagid;
    private String name;
    private String num;
    private Integer type;

    public NewsChannelBeanDB() {
    }

    public NewsChannelBeanDB(Long id) {
        this.id = id;
    }

    public NewsChannelBeanDB(Long id, String tid, String cnname, String sort_order, String fid, String path, String source, String style, String status, String siteid, String default_show, String icon, String tagid, String name, String num, Integer type) {
        this.id = id;
        this.tid = tid;
        this.cnname = cnname;
        this.sort_order = sort_order;
        this.fid = fid;
        this.path = path;
        this.source = source;
        this.style = style;
        this.status = status;
        this.siteid = siteid;
        this.default_show = default_show;
        this.icon = icon;
        this.tagid = tagid;
        this.name = name;
        this.num = num;
        this.type = type;
    }

	
    public NewsChannelBeanDB(NewsChannelBean bean) {
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
        setTagid(bean.getId());
        setName(bean.getName());
        setNum(bean.getNum());
        setType(bean.getType());
    }

    public NewsChannelBeanDB(TagBean bean) {
        setCnname(bean.getName());
        setDefault_show("1");
        setIcon(bean.getIcon());
        setTagid(bean.getId());
        setName(bean.getName());
        setNum(bean.getNum());
        setType(NewsChannelBean.TYPE_NORMAL);
    }


	
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getCnname() {
        return cnname;
    }

    public void setCnname(String cnname) {
        this.cnname = cnname;
    }

    public String getSort_order() {
        return sort_order;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSiteid() {
        return siteid;
    }

    public void setSiteid(String siteid) {
        this.siteid = siteid;
    }

    public String getDefault_show() {
        return default_show;
    }

    public void setDefault_show(String default_show) {
        this.default_show = default_show;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTagid() {
        return tagid;
    }

    public void setTagid(String tagid) {
        this.tagid = tagid;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}

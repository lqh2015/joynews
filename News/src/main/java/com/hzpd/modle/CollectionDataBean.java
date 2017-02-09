package com.hzpd.modle;

import android.text.Html;

import java.util.Arrays;

public class CollectionDataBean {
    private String comcount;//": "0",
    private String fav;//收藏数目
    private String[] imgs;
    private String json_url;//":"",
    //	private String imgs;//":"",
    private String update_time;
    private String copyfrom;//新闻来源

    private String rtype;
    private String nid;


    private String id;
    private String tid;
    private String time;//":"",
    private String title;//":"111111111111111",


    public CollectionDataBean() {
    }


    public CollectionDataBean(String id, String tid, String title, String time,
                              String[] imgs, String json_url, String rtype) {
        super();
        this.id = id;
        this.tid = tid;
        this.title = title;
        this.time = time;
        this.imgs = imgs;
        this.json_url = json_url;
        this.rtype = rtype;
    }


    public CollectionDataBean(String id, String tid, String title, String time,
                              String[] imgs, String json_url, String rtype, String copyfrom, String fav, String comcount) {
        super();
        this.id = id;
        this.tid = tid;
        this.title = title;
        this.time = time;
        this.imgs = imgs;
        this.json_url = json_url;
        this.rtype = rtype;
        this.copyfrom = copyfrom;
        this.fav = fav;
        this.comcount = comcount;
    }

    @Override
    public String toString() {
        return "CollectionDataBean{" +
                "comcount='" + comcount + '\'' +
                ", fav='" + fav + '\'' +
                ", imgs=" + Arrays.toString(imgs) +
                ", json_url='" + json_url + '\'' +
                ", update_time='" + update_time + '\'' +
                ", copyfrom='" + copyfrom + '\'' +
                ", rtype='" + rtype + '\'' +
                ", nid='" + nid + '\'' +
                ", id='" + id + '\'' +
                ", tid='" + tid + '\'' +
                ", time='" + time + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getRtype() {
        return rtype;
    }

    public void setRtype(String rtype) {
        this.rtype = rtype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Html.fromHtml(title).toString();;
    }

    public String getCopyfrom() {
        return copyfrom;
    }

    public void setCopyfrom(String copyfrom) {
        this.copyfrom = copyfrom;
    }

    public String getFav() {
        return fav;
    }

    public void setFav(String fav) {
        this.fav = fav;
    }

    public String getComcount() {
        return comcount;
    }

    public void setComcount(String comcount) {
        this.comcount = comcount;
    }

    public String[] getImgs() {
        return imgs;
    }

    public void setImgs(String[] imgs) {
        this.imgs = imgs;
    }

    public String getJson_url() {
        return json_url;
    }

    public void setJson_url(String json_url) {
        this.json_url = json_url;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }


}

package com.hzpd.modle;

public class MycommentsitemBean {


    private String status;//1审核通过 -2审核中


    private String alias;
    private String avatar_path;//": "http://58.68.134.165:8081/cms_json/zqzx/News/Content/197001/01/133",
    private String cid;
    private String content;//新闻来源
    private String dateline;

    private String floor;//": "555555555555555",
    private String nickname;//": "2014-12-17 18:25:15"
    private String praise;//":"1",

    private String uid;

    @Override
    public String toString() {
        return "MycommentsitemBean{" +
                "status='" + status + '\'' +
                ", alias='" + alias + '\'' +
                ", avatar_path='" + avatar_path + '\'' +
                ", cid='" + cid + '\'' +
                ", content='" + content + '\'' +
                ", dateline='" + dateline + '\'' +
                ", floor='" + floor + '\'' +
                ", nickname='" + nickname + '\'' +
                ", praise='" + praise + '\'' +
                ", uid='" + uid + '\'' +
                ", ulevel='" + ulevel + '\'' +
                '}';
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAvatar_path() {
        return avatar_path;
    }

    public void setAvatar_path(String avatar_path) {
        this.avatar_path = avatar_path;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUlevel() {
        return ulevel;
    }

    public void setUlevel(String ulevel) {
        this.ulevel = ulevel;
    }

    private String ulevel;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPraise() {
        return praise;
    }

    public void setPraise(String praise) {
        this.praise = praise;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

}

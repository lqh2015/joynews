package com.hzpd.modle;

public class SubjectItemColumnsBean {

    private String cid;//: "20",
    private String cname;//: "快快快",
    private String sid;//: "2",
    private String create_time;//: "2015-06-04 10:22:11"


    public String getCid() {
        return cid;
    }

    public String getCname() {
        return cname;
    }

    public String getSid() {
        return sid;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    @Override
    public String toString() {
        return "SubjectItemColumnsBean{" +
                "cid='" + cid + '\'' +
                ", cname='" + cname + '\'' +
                ", sid='" + sid + '\'' +
                ", create_time='" + create_time + '\'' +
                '}';
    }
}

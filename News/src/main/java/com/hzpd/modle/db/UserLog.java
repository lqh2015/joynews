package com.hzpd.modle.db;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "USER_LOG".
 */
public class UserLog {

    private Long id;
    private String newsId;
    private String time;
    private String active_time;

    public UserLog() {
    }

    public UserLog(Long id) {
        this.id = id;
    }

    public UserLog(Long id, String newsId, String time, String active_time) {
        this.id = id;
        this.newsId = newsId;
        this.time = time;
        this.active_time = active_time;
    }
    public UserLog(String newsId, String time, String active_time) {
        this.newsId = newsId;
        this.time = time;
        this.active_time = active_time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getActive_time() {
        return active_time;
    }

    public void setActive_time(String active_time) {
        this.active_time = active_time;
    }

}
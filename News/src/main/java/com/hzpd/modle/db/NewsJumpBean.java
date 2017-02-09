package com.hzpd.modle.db;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "NEWS_JUMP_BEAN".
 */
public class NewsJumpBean {

    private Long id;
    private String url;
    private String content;

    public NewsJumpBean() {
    }

    public NewsJumpBean(Long id) {
        this.id = id;
    }

    public NewsJumpBean(Long id, String url, String content) {
        this.id = id;
        this.url = url;
        this.content = content;
    }
	
	    public NewsJumpBean(String url, String content) {
        super();
        this.url = url;
        this.content = content;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}

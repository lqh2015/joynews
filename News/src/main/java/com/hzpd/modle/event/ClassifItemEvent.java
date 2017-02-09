package com.hzpd.modle.event;

/**
 * 清除搜索历史
 */
public class ClassifItemEvent {

    private String id;

    public ClassifItemEvent(String id) {
        // TODO Auto-generated constructor stub
        this.id = id;
    }

    public String getId(){
        return id;
    }

}

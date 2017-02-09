package com.hzpd.modle;

import java.io.Serializable;

/**
 * Created by taoshuang on 2015/12/9.
 */
public class TagBean implements Serializable{
    private static final long serialVersionUID = 1L;
    private String icon;
    private String id;
    private String name;
    private String num;

    @Override
    public String toString() {
        return "TagBean{" +
                "icon='" + icon + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", num='" + num + '\'' +
                '}';
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
}

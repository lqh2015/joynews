package com.hzpd.modle;


public class CollectionJsonBean {

    private CollectionDataBean data;//
    private String datetime;//":"2014-12-31 12:12:12"
    private String id;
    private String type;//":"1",

    @Override
    public String toString() {
        return "CollectionJsonBean{" +
                "data=" + data +
                ", datetime='" + datetime + '\'' +
                ", id='" + id + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public CollectionDataBean getData() {
        return data;
    }

    public void setData(CollectionDataBean data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

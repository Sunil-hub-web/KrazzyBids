package com.example.krazzybids.modelclass;

public class BannerModelClass {

    String banner_id,banner_title,type,orderby,image;

    public BannerModelClass(String banner_id, String banner_title, String type, String orderby, String image) {
        this.banner_id = banner_id;
        this.banner_title = banner_title;
        this.type = type;
        this.orderby = orderby;
        this.image = image;
    }

    public String getBanner_id() {
        return banner_id;
    }

    public void setBanner_id(String banner_id) {
        this.banner_id = banner_id;
    }

    public String getBanner_title() {
        return banner_title;
    }

    public void setBanner_title(String banner_title) {
        this.banner_title = banner_title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderby() {
        return orderby;
    }

    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

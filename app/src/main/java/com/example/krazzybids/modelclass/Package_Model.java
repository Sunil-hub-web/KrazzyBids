package com.example.krazzybids.modelclass;

public class Package_Model {

    String id,name,total_price,total_bids,discount_price,status,create_at;

    public Package_Model(String id, String name, String total_price, String total_bids, String discount_price,
                         String status, String create_at) {
        this.id = id;
        this.name = name;
        this.total_price = total_price;
        this.total_bids = total_bids;
        this.discount_price = discount_price;
        this.status = status;
        this.create_at = create_at;
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

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getTotal_bids() {
        return total_bids;
    }

    public void setTotal_bids(String total_bids) {
        this.total_bids = total_bids;
    }

    public String getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(String discount_price) {
        this.discount_price = discount_price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }
}

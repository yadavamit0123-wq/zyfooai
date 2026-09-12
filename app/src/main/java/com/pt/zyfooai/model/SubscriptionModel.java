package com.pt.zyfooai.model;

import java.io.Serializable;

public class SubscriptionModel implements Serializable {

    public String id, name, type, value, price, discount_price, image,banner, details, posts_limit, business_limit, political_limit, status, updated_at, created_at;
    public int festival,business,political,video;

    public String plan_id, plan_name, plan_start_date, plan_end_date;
    public boolean is_subscribed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(String discount_price) {
        this.discount_price = discount_price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPosts_limit() {
        return posts_limit;
    }

    public void setPosts_limit(String posts_limit) {
        this.posts_limit = posts_limit;
    }

    public String getBusiness_limit() {
        return business_limit;
    }

    public void setBusiness_limit(String business_limit) {
        this.business_limit = business_limit;
    }

    public String getPolitical_limit() {
        return political_limit;
    }

    public void setPolitical_limit(String political_limit) {
        this.political_limit = political_limit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getFestival() {
        return festival;
    }

    public void setFestival(int festival) {
        this.festival = festival;
    }

    public int getBusiness() {
        return business;
    }

    public void setBusiness(int business) {
        this.business = business;
    }

    public int getPolitical() {
        return political;
    }

    public void setPolitical(int political) {
        this.political = political;
    }

    public int getVideo() {
        return video;
    }

    public void setVideo(int video) {
        this.video = video;
    }
}

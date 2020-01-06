package com.projectreachout.Article;

import com.google.gson.Gson;

public class Article {
    private String article_id;
    private String user_id;
    private String display_name;
    private String time_stamp;
    private String avatar;
    private String image_url;
    private String description;

    public Article() {
    }

    public static Article fromJson(String jsonString) {
        return new Gson().fromJson(jsonString, Article.class);
    }

    public String getId() {
        return article_id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setId(String article_id) {
        this.article_id = article_id;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArticle_id() {
        return article_id;
    }

    public void setArticle_id(String article_id) {
        this.article_id = article_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}

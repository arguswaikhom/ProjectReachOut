package com.projectreachout.Article;

public class ArticleItem {

    private int id;
    private String team_name;
    private String username;
    private String time_stamp;
    private String profile_picture_url;
    private String image_url;
    private String description;

    public ArticleItem() {
    }

    public ArticleItem(int id, String team_name, String username, String time_stamp,
                       String profile_picture_url, String image_url, String description) {
        this.id = id;
        this.team_name = team_name;
        this.username = username;
        this.time_stamp = time_stamp;
        this.profile_picture_url = profile_picture_url;
        this.image_url = image_url;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getTeam_name() {
        return team_name;
    }

    public String getUsername() {
        return username;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public String getProfile_picture_url() {
        return profile_picture_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public void setProfile_picture_url(String profile_picture_url) {
        this.profile_picture_url = profile_picture_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package com.projectreachout.Article.GetArticle;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.projectreachout.User.User;

public class Article {
    private String article_id;
    private String user_id;
    private String display_name;
    private String time_stamp;
    private String avatar;
    private String image_url;
    private String description;
    private String my_reaction;
    private String reaction_count;

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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArticle_id() {
        return article_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMy_reaction() {
        return my_reaction;
    }

    public String getReaction_count() {
        return reaction_count;
    }

    @NonNull
    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static class Reaction {
        private User user;
        private String reaction;

        public static Reaction fromJSON(String jsonString) {
            return new Gson().fromJson(jsonString, Reaction.class);
        }

        public User getUser() {
            return user;
        }

        public String getReaction() {
            return reaction;
        }
    }
}

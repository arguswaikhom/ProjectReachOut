package com.projectreachout.Event;

import androidx.annotation.NonNull;

public class ContributePeople {
    private int user_id;
    private String user_name;
    private String profile_picture_url;

    public ContributePeople() {
    }

    public ContributePeople(String user_name, String profile_picture_url) {
        this.user_name = user_name;
        this.profile_picture_url = profile_picture_url;
    }

    public ContributePeople(int id, String user_name, String profile_picture_url) {
        this.user_id = id;
        this.user_name = user_name;
        this.profile_picture_url = profile_picture_url;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setProfile_picture_url(String profile_picture_url) {
        this.profile_picture_url = profile_picture_url;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getProfile_picture_url() {
        return profile_picture_url;
    }

    @NonNull
    @Override
    public String toString() {
        return this.user_name + "\n" + this.profile_picture_url + "\n\n";
    }
}

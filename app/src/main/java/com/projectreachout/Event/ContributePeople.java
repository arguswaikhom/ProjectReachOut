package com.projectreachout.Event;

import android.support.annotation.NonNull;

public class ContributePeople {
    private String user_name;
    private String profile_picture_url;

    public ContributePeople() {
    }

    public ContributePeople(String user_name, String profile_picture_url) {
        this.user_name = user_name;
        this.profile_picture_url = profile_picture_url;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setProfile_picture_url(String profile_picture_url) {
        this.profile_picture_url = profile_picture_url;
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

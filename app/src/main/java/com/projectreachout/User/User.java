package com.projectreachout.User;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

public class User implements Parcelable {

    private String user_id;
    private String username;
    private String team_name;
    private String profile_image_url;
    private String display_name;
    private String email;
    private String phone_number;
    private String user_type;
    private String bio;

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public User() {
    }

    public User(String user_id, String username, String team_name, String profile_image_url) {
        this.user_id = user_id;
        this.username = username;
        this.team_name = team_name;
        this.profile_image_url = profile_image_url;
    }

    public User(String user_id, String username, String profile_image_url, String display_name, String email, String phone_number, String user_type, String bio) {
        this.user_id = user_id;
        this.username = username;
        this.profile_image_url = profile_image_url;
        this.display_name = display_name;
        this.email = email;
        this.phone_number = phone_number;
        this.user_type = user_type;
        this.bio = bio;
    }

    public static User fromJson(String jsonString) {
        return new Gson().fromJson(jsonString, User.class);
    }

    protected User(Parcel in) {
        user_id = in.readString();
        username = in.readString();
        team_name = in.readString();
        profile_image_url = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(username);
        dest.writeString(team_name);
        dest.writeString(profile_image_url);
    }

    @NonNull
    @Override
    public String toString() {
        return user_id + " " +
                this.username + " " +
                this.team_name + " " +
                this.profile_image_url + " " +
                this.display_name + " " +
                this.email + " " +
                this.phone_number + " " +
                this.user_type + " " +
                this.bio + " ";
    }
}

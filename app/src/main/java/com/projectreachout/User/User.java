package com.projectreachout.User;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

public class User implements Parcelable {
    public static final String AC_SUPERUSER = "superuser";
    public static final String AC_STAFF = "staff";
    public static final String AC_GUEST = "guest";

    private String user_id;
    private String username;
    private String team_name;
    private String profile_image_url;
    private String display_name;
    private String email;
    private String phone_number;
    private String user_type;
    private String bio;
    private boolean isActive;

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getUser_type() {
        return user_type;
    }

    public String getBio() {
        return bio;
    }

    public boolean isActive() {
        return isActive;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.user_id);
        dest.writeString(this.username);
        dest.writeString(this.team_name);
        dest.writeString(this.profile_image_url);
        dest.writeString(this.display_name);
        dest.writeString(this.email);
        dest.writeString(this.phone_number);
        dest.writeString(this.user_type);
        dest.writeString(this.bio);
        dest.writeByte(this.isActive ? (byte) 1 : (byte) 0);
    }

    protected User(Parcel in) {
        this.user_id = in.readString();
        this.username = in.readString();
        this.team_name = in.readString();
        this.profile_image_url = in.readString();
        this.display_name = in.readString();
        this.email = in.readString();
        this.phone_number = in.readString();
        this.user_type = in.readString();
        this.bio = in.readString();
        this.isActive = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}

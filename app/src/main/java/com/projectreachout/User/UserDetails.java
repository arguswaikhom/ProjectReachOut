package com.projectreachout.User;

import android.os.Parcel;
import android.os.Parcelable;

public class UserDetails implements Parcelable {

    private String id;
    private String user_name;
    private String team_name;
    private String profile_picture_url;

    public UserDetails() {
    }

    public UserDetails(String  id, String user_name, String team_name, String profile_picture_url) {
        this.id = id;
        this.user_name = user_name;
        this.team_name = team_name;
        this.profile_picture_url = profile_picture_url;
    }

    protected UserDetails(Parcel in) {
        id = in.readString();
        user_name = in.readString();
        team_name = in.readString();
        profile_picture_url = in.readString();
    }

    public static final Creator<UserDetails> CREATOR = new Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel in) {
            return new UserDetails(in);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String  id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getProfile_picture_url() {
        return profile_picture_url;
    }

    public void setProfile_picture_url(String profile_picture_url) {
        this.profile_picture_url = profile_picture_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user_name);
        dest.writeString(team_name);
        dest.writeString(profile_picture_url);
    }
}

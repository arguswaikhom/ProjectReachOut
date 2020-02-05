package com.projectreachout.ManageUser;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.projectreachout.User.User;
import com.projectreachout.Utilities.NetworkUtils.HttpVolleyRequest;
import com.projectreachout.Utilities.NetworkUtils.OnHttpResponse;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.projectreachout.GeneralStatic.JSONParsingArrayFromString;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromArray;
import static com.projectreachout.GeneralStatic.getDomainUrl;

public class OnRequestManageUser implements OnHttpResponse {
    private final String TAG = OnRequestManageUser.class.getName();

    public static final int RC_GET_ALL_USER = 1;
    private int request;
    private OnUpdateUser onUpdateUser;

    public OnRequestManageUser(int request, OnUpdateUser onUpdateUser) {
        this.request = request;
        this.onUpdateUser = onUpdateUser;
    }

    public interface OnUpdateUser {
        void onUpdateUser(List<User> users, int request);
    }

    public void fetch() {
        String url = getDomainUrl() + "/get_users/";

        HttpVolleyRequest httpVolleyRequest = new HttpVolleyRequest(Request.Method.GET, url, null, this.request, this);
        httpVolleyRequest.execute();
    }

    @Override
    public void onHttpResponse(String response, int request) {
        Log.v(TAG, response);
        parseFromJSON(response, request);
    }

    @Override
    public void onHttpErrorResponse(VolleyError error, int request) {
        Log.e(TAG, error.getMessage());
    }

    private void parseFromJSON(String response, int request) {
        List<User> users = new ArrayList<>();
        JSONArray jsonArray = JSONParsingArrayFromString(response);
        for (int i=0; i<jsonArray.length(); i++) {
            users.add(User.fromJson(JSONParsingObjectFromArray(jsonArray, i).toString()));
        }
        onUpdateUser.onUpdateUser(users, request);
    }
}

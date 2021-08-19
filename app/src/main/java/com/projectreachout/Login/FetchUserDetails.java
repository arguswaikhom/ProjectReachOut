package com.projectreachout.Login;

import android.util.Log;

import com.android.volley.Request;
import com.projectreachout.AppController;
import com.projectreachout.Utilities.NetworkUtils.HttpVolleyRequest;
import com.projectreachout.Utilities.NetworkUtils.OnHttpResponse;

import java.util.HashMap;
import java.util.Map;

import static com.projectreachout.GeneralStatic.getDomainUrl;

public class FetchUserDetails {
    private final static String TAG = FetchUserDetails.class.getSimpleName();

    public static void fetch(OnHttpResponse onHttpResponse, int request) {
        getUserDetails(onHttpResponse, request);
    }

    private static void getUserDetails(OnHttpResponse onHttpResponse, int request) {
        String url = getDomainUrl() + "/get_user_details/";
        String user_id = AppController.getInstance().getFirebaseAuth().getUid();
        Map<String, String> param = new HashMap<>();
        param.put("user_id", user_id);

        Log.v(TAG, "User id: " + user_id);
        Log.v(TAG, "Param: " + param.toString());

        HttpVolleyRequest httpVolleyRequest = new HttpVolleyRequest(Request.Method.POST, url, null, request, null, param, onHttpResponse);
        httpVolleyRequest.execute();
    }
}

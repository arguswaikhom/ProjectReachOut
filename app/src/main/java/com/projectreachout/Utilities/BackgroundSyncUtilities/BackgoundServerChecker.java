package com.projectreachout.Utilities.BackgroundSyncUtilities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.projectreachout.AppController;
import com.projectreachout.Login.LoginActivity;
import com.projectreachout.Utilities.PreferenceUtilities.NotificationPreferenceUtilities;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.projectreachout.GeneralStatic.JSONParsingObjectFromString;
import static com.projectreachout.GeneralStatic.JSONParsingStringFromObject;
import static com.projectreachout.GeneralStatic.getDomainUrl;

public class BackgoundServerChecker {
    static class IndexHolder {
        int article;
        int my_event;
        int event;

        @NonNull
        @Override
        public String toString() {
            return "article: " + this.article + ", my_event: " + this.my_event + ", event: " + this.event;
        }
    }
    public static final int ACTION_ARTICLE_ONLY = 1;
    public static final int ACTION_MY_EVENT_ONLY = 2;
    public static final int ACTION_DEFAULT = 3;

    public static final int INTERVAL_MINUTES = 30;

    public static final String url = getDomainUrl() + "/get_latest_item_ids/";

    synchronized public static void backgroundCheck(final int action) {
        Log.v("ooooo", NotificationPreferenceUtilities.getLastArticle() + " : " + NotificationPreferenceUtilities.getLastMyEvent());

        if (AppController.getInstance().isInternetAvailable()) {
            performChecking(action);
        }
    }

    private static void performChecking(final int action) {
        Log.v("ooooo", url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            IndexHolder indexHolder = getIndexHolder(response);
            if (action == ACTION_DEFAULT) {
                NotificationPreferenceUtilities.setLastArticle(indexHolder.article);
                NotificationPreferenceUtilities.setLastMyEvent(indexHolder.my_event);
            } else if (action == ACTION_ARTICLE_ONLY) {
                NotificationPreferenceUtilities.setLastArticle(indexHolder.article);
            } else if (action == ACTION_MY_EVENT_ONLY) {
                NotificationPreferenceUtilities.setLastMyEvent(indexHolder.my_event);
            }
            Log.v("ooooo", indexHolder.toString());

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("ooooo", error.toString());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return getBackgroundSyncPOSTParam();
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public static IndexHolder getIndexHolder(String response) {
        IndexHolder indexHolder = new IndexHolder();
        JSONObject object = JSONParsingObjectFromString(response);
        indexHolder.article = Integer.valueOf(JSONParsingStringFromObject(object, "last_article_id"));
        indexHolder.my_event = Integer.valueOf(JSONParsingStringFromObject(object, "last_my_event_id"));
        indexHolder.event = Integer.valueOf(JSONParsingStringFromObject(object, "last_event_id"));
        return indexHolder;
    }

    public static Map<String, String> getBackgroundSyncPOSTParam() {
        Map<String, String> param = new HashMap<>();
        if (AppController.getInstance().getUserType() == LoginActivity.AUTHORISED_USER) {
            param.put("user_name", AppController.getInstance().getLoginUserUsername());
        } else if (AppController.getInstance().getUserType() == LoginActivity.GUEST_USER) {
            param.put("user_name", "0_guest");
        }
        Log.v("ooooo", param.toString());

        return param;
    }
}

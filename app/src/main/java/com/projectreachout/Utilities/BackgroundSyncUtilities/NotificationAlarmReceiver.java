package com.projectreachout.Utilities.BackgroundSyncUtilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.projectreachout.AppController;
import com.projectreachout.Utilities.NotificationUtilities.HandleNotifications;
import com.projectreachout.Utilities.PreferenceUtilities.NotificationPreferenceUtilities;

import java.util.Map;


public class NotificationAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (AppController.getInstance().isInternetAvailable()) {
            performChecking(context);
        } else {
            Log.v("ooooo", "no internet");
        }
    }

    private void performChecking(Context context) {
        StringRequest request = new StringRequest(Request.Method.POST, BackgoundServerChecker.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                BackgoundServerChecker.IndexHolder indexHolder = BackgoundServerChecker.getIndexHolder(response);
                Log.v("ooooo", BackgoundServerChecker.url);

                Log.v("ooooo", indexHolder.toString());
                Log.v("ooooo", NotificationPreferenceUtilities.getLastArticle() + " : " + NotificationPreferenceUtilities.getLastMyEvent());

                if (indexHolder.article > NotificationPreferenceUtilities.getLastArticle() && indexHolder.my_event > NotificationPreferenceUtilities.getLastMyEvent()) {
                    HandleNotifications.executeTask(context, HandleNotifications.ACTION_NOTIFICATION_NEW_MY_EVENT);
                    HandleNotifications.executeTask(context, HandleNotifications.ACTION_NOTIFICATION_NEW_ARTICLE);
                } else if (indexHolder.article > NotificationPreferenceUtilities.getLastArticle()) {
                    HandleNotifications.executeTask(context, HandleNotifications.ACTION_NOTIFICATION_NEW_ARTICLE);
                } else if (indexHolder.my_event > NotificationPreferenceUtilities.getLastMyEvent()) {
                    HandleNotifications.executeTask(context, HandleNotifications.ACTION_NOTIFICATION_NEW_MY_EVENT);
                }
                NotificationPreferenceUtilities.setLastArticle(indexHolder.article);
                NotificationPreferenceUtilities.setLastMyEvent(indexHolder.my_event);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("ooooo", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return BackgoundServerChecker.getBackgroundSyncPOSTParam();
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }
}

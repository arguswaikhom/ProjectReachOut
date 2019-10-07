package com.projectreachout.Utilities.PreferenceUtilities;

import android.content.SharedPreferences;

import com.projectreachout.AppController;

public class NotificationPreferenceUtilities {
    private static final String KEY_LAST_ARTICLE = "last_article";
    private static final String KEY_MY_EVENT = "last_my_event";
    private static int DEFAULT_ID = 0;

    synchronized public static void setLastArticle(int lastArticleId) {
        SharedPreferences prefs = AppController.getInstance().getSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_LAST_ARTICLE, lastArticleId);
        editor.apply();
    }

    synchronized public static void setLastMyEvent(int lastMyEventId) {
        SharedPreferences prefs = AppController.getInstance().getSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_MY_EVENT, lastMyEventId);
        editor.apply();
    }

    public static int getLastArticle() {
        SharedPreferences prefs = AppController.getInstance().getSharedPreferences();
        return prefs.getInt(KEY_LAST_ARTICLE, DEFAULT_ID);
    }

    public static int getLastMyEvent() {
        SharedPreferences prefs = AppController.getInstance().getSharedPreferences();
        return prefs.getInt(KEY_MY_EVENT, DEFAULT_ID);
    }
}

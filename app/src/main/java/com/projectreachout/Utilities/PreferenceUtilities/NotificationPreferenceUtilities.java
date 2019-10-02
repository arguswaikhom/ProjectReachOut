package com.projectreachout.Utilities.PreferenceUtilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NotificationPreferenceUtilities {
    private static final String KEY_LAST_ARTICLE = "last_article";
    private static final String KEY_MY_EVENT = "last_my_event";
    private static int DEFAULT_ID = 0;

    synchronized public static void setLastArticle(Context context, int lastArticleId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_LAST_ARTICLE, lastArticleId);
        editor.apply();
    }

    synchronized public static void setLastMyEvent(Context context, int lastMyEventId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_MY_EVENT, lastMyEventId);
        editor.apply();
    }

    public static int getLastArticle(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(KEY_LAST_ARTICLE, DEFAULT_ID);
    }

    public static int getLastMyEvent(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(KEY_MY_EVENT, DEFAULT_ID);
    }
}

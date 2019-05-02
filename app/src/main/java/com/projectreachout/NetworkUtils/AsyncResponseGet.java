package com.projectreachout.NetworkUtils;

import com.android.volley.VolleyError;

import org.json.JSONArray;

public interface AsyncResponseGet {
    void onResponse(JSONArray output);
    void onErrorResponse(VolleyError error);
    void onProgressUpdate(int value);
    void onPreExecute();
}

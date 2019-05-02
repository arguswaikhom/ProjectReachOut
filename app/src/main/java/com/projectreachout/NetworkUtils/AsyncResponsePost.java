package com.projectreachout.NetworkUtils;

import com.android.volley.VolleyError;

public interface AsyncResponsePost {
    void onResponse(String output);
    void onErrorResponse(VolleyError error);
    void onProgressUpdate(int value);
    void onPreExecute();
}

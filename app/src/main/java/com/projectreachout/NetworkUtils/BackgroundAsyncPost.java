package com.projectreachout.NetworkUtils;

import android.os.AsyncTask;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.projectreachout.AppController;

import java.util.Map;

public class BackgroundAsyncPost extends AsyncTask<String, Integer, String> {

    private AsyncResponsePost mAsyncResponsePost;
    private String mResponse;
    private Map<String, String> mParam;

    public BackgroundAsyncPost(Map<String, String> param, AsyncResponsePost asyncResponsePost) {
        this.mParam = param;
        this.mAsyncResponsePost = asyncResponsePost;
    }

    @Override
    protected String doInBackground(String... strings) {

        String url = strings[0];

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                (String response) -> {
                    mResponse = response;
                },
                (VolleyError error) -> mAsyncResponsePost.onErrorResponse(error)) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return mParam;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);

        return mResponse;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mAsyncResponsePost.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mAsyncResponsePost.onResponse(s);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mAsyncResponsePost.onProgressUpdate(values[0]);
    }
}

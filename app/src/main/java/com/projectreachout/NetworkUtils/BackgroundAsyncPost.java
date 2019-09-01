package com.projectreachout.NetworkUtils;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.projectreachout.AppController;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class BackgroundAsyncPost extends AsyncTask<String, Integer, String> {

    private static final String TAG = "";

    private AsyncResponsePost mAsyncResponsePost;
    private String mResponse;

    private Map<String, String> mParam;
    private Map<String, String> mHeader = null;

    public BackgroundAsyncPost(Map<String, String> param, AsyncResponsePost asyncResponsePost) {
        this.mParam = param;
        this.mAsyncResponsePost = asyncResponsePost;
    }

    public BackgroundAsyncPost(Map<String, String> header, Map<String, String> param, AsyncResponsePost asyncResponsePost) {
        this.mHeader = header;
        this.mParam = param;
        this.mAsyncResponsePost = asyncResponsePost;
    }

    @Override
    protected String doInBackground(String... strings) {

        String url = strings[0];

        Log.v(TAG, url);

        CountDownLatch latch = new CountDownLatch(1);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                (String response) -> {
                    mResponse = response;
                    latch.countDown();
                },
                (VolleyError error) -> mAsyncResponsePost.onErrorResponse(error)) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (mHeader != null) {
                    return mHeader;
                } else {
                    return AppController.getInstance().getLoginCredentialHeader();
                }
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return mParam;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

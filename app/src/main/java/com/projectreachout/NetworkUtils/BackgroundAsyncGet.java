package com.projectreachout.NetworkUtils;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.projectreachout.AppController;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class BackgroundAsyncGet extends AsyncTask<String, Integer, JSONArray> {

    private static final String TAG = BackgroundAsyncGet.class.getSimpleName();

    private JSONArray mJsonArrayResponse;
    private AsyncResponseGet mAsyncResponseGet;

    private Map<String, String> mHeader = null;
    private Map<String, String> mParam = null;

    public BackgroundAsyncGet(AsyncResponseGet mAsyncResponseGet) {
        this.mAsyncResponseGet = mAsyncResponseGet;
    }

    public BackgroundAsyncGet(Map<String, String> header, AsyncResponseGet mAsyncResponseGet) {
        this.mHeader = header;
        this.mAsyncResponseGet = mAsyncResponseGet;
    }

    public BackgroundAsyncGet(Map<String, String> header, Map<String, String> param, AsyncResponseGet mAsyncResponseGet) {
        this.mHeader = header;
        this.mParam = param;
        this.mAsyncResponseGet = mAsyncResponseGet;
    }

    @Override
    protected JSONArray doInBackground(String... strings) {

        String url = strings[0];

        Log.v(TAG, url);

        CountDownLatch latch = new CountDownLatch(1);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                (JSONArray response) -> {
                    mJsonArrayResponse = response;
                    latch.countDown();
                }, (VolleyError error) -> mAsyncResponseGet.onErrorResponse(error)) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (mHeader != null) {
                    return mHeader;
                } else {
                    Map<String, String> headers = new HashMap<>();
                    String credentials = "argus:argus";
                    String auth = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    //headers.put("Content-Type", "application/json");
                    headers.put("Authorization", auth);
                    return headers;
                }
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (mParam != null) {
                    Log.d(TAG, mParam.toString());
                    return mParam;
                }
                return super.getParams();
            }
        };

        AppController.getInstance().addToRequestQueue(jsonArrayRequest);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return mJsonArrayResponse;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mAsyncResponseGet.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);
        mAsyncResponseGet.onResponse(jsonArray);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mAsyncResponseGet.onProgressUpdate(values[0]);

    }

    @Override
    protected void onCancelled(JSONArray jsonArray) {
        super.onCancelled(jsonArray);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}

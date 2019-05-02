package com.projectreachout.NetworkUtils;

import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.projectreachout.AppController;

import org.json.JSONArray;

public class BackgroundAsyncGet extends AsyncTask<String, Integer, JSONArray> {

    private JSONArray mJsonArrayResponse;
    private AsyncResponseGet mAsyncResponseGet;

    public BackgroundAsyncGet(AsyncResponseGet mAsyncResponseGet) {
        this.mAsyncResponseGet = mAsyncResponseGet;
    }

    @Override
    protected JSONArray doInBackground(String... strings) {

        String url = strings[0];

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                (JSONArray response) -> mJsonArrayResponse = response,
                (VolleyError error) -> mAsyncResponseGet.onErrorResponse(error));

        AppController.getInstance().addToRequestQueue(jsonArrayRequest);

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

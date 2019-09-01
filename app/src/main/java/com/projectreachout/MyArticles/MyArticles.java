package com.projectreachout.MyArticles;

import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.projectreachout.AppController;
import com.projectreachout.PostFeed.FeedItem;
import com.projectreachout.PostFeed.FeedListAdapter;
import com.projectreachout.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.projectreachout.GeneralStatic.JSONParsingArrayFromString;
import static com.projectreachout.GeneralStatic.JSONParsingIntFromObject;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromArray;
import static com.projectreachout.GeneralStatic.JSONParsingStringFromObject;
import static com.projectreachout.GeneralStatic.LOAD_MORE;
import static com.projectreachout.GeneralStatic.REFRESH;
import static com.projectreachout.GeneralStatic.getDomainUrl;

public class MyArticles extends AppCompatActivity {

    public static FeedListAdapter mFeedListAdapterMyArticles;

    private View mParentLayout;

    public static List<FeedItem> mFeedItemListMyArticles;
    private LinearLayout mErrorMessageLayout;
    private Button mRetryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_layout);

        mParentLayout = findViewById(android.R.id.content);

        ListView listView = findViewById(R.id.lv_lvl_list_view);
        mErrorMessageLayout = findViewById(R.id.ll_lvl_error_message_layout);
        mRetryBtn = findViewById(R.id.btn_nel_retry);

        try {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mFeedItemListMyArticles = new ArrayList<>();

        mFeedListAdapterMyArticles = new FeedListAdapter(this, mFeedItemListMyArticles);
        listView.setAdapter(mFeedListAdapterMyArticles);

        loadData(REFRESH);

        mRetryBtn.setOnClickListener(v -> loadData(REFRESH));
    }

    private void loadData(int action) {
        /*Uri.Builder builder = new Uri.Builder();
        // TODO: use .authority(getString(R.string.localhost)) after having a domain name
        builder.scheme(getString(R.string.http))
                .encodedAuthority(getString(R.string.localhost) + ":" + getString(R.string.port_no))
                .appendPath("get_my_articles")
                .appendPath("");*/

        String url = getDomainUrl() + "/get_my_articles/";

        switch (action) {
            case REFRESH: {
                loadBackgroundAsyncTask(url);
            }
            case LOAD_MORE: {
                // TODO: get timeStamp of the last post in the feed list
                /*String lastPostTimeStamp = "1556604826";

                builder.appendQueryParameter("before", lastPostTimeStamp);
                loadBackgroundAsyncTask(builder.build().toString());*/
            }
        }
    }

    private void loadBackgroundAsyncTask(String url) {
        Map<String, String> param = new HashMap<>();
        param.put("user_name", AppController.getInstance().getLoginUserUsername());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String output) {
                if (output != null) {
                    if (mErrorMessageLayout.getVisibility() == View.VISIBLE) {
                        mErrorMessageLayout.setVisibility(View.GONE);
                    }
                    parseJsonFeed(JSONParsingArrayFromString(output));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                displayErrorMessage();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppController.getInstance().getLoginCredentialHeader();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return param;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void displayErrorMessage() {
        if (mFeedListAdapterMyArticles.isEmpty()) {
            mErrorMessageLayout.setVisibility(View.VISIBLE);
        } else {
            String errorMessage = "Couldn't update information from server...";
            Snackbar.make(mParentLayout, errorMessage, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadData(REFRESH);
                }
            }).show();
        }
    }

    private void parseJsonFeed(JSONArray response) {
        mFeedItemListMyArticles.clear();
        for (int i = response.length()-1; i >= 0; i--) {
            JSONObject feedObj = JSONParsingObjectFromArray(response, i);

            /*Uri.Builder builder = new Uri.Builder();
            builder.scheme(getString(R.string.http))
                    .encodedAuthority(getString(R.string.localhost) + ":" + getString(R.string.port_no));

            String url = builder.build().toString();*/

            String url = getDomainUrl();

            int id = JSONParsingIntFromObject(feedObj, "article_id");
            String teamName = JSONParsingStringFromObject(feedObj, "team_name");
            String userName = JSONParsingStringFromObject(feedObj, "username");
            String timeStamp = JSONParsingStringFromObject(feedObj, "time_stamp");
            String profilePictureUrl = JSONParsingStringFromObject(feedObj, "profile_picture_url");
            String imageUrl = JSONParsingStringFromObject(feedObj, "image");
            String description = JSONParsingStringFromObject(feedObj, "desc");

            FeedItem item = new FeedItem();

            item.setId(id);
            item.setTeam_name(teamName);
            item.setUsername(userName);
            item.setTime_stamp(timeStamp);

            item.setProfile_picture_url(url + profilePictureUrl);
            item.setImage_url(url + imageUrl);

            item.setDescription(description);

            mFeedItemListMyArticles.add(item);
        }
        mFeedListAdapterMyArticles.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}

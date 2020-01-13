package com.projectreachout.Article.MyArticles;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.projectreachout.AppController;
import com.projectreachout.Article.GetArticle.Article;
import com.projectreachout.Article.GetArticle.ArticleListAdapter;
import com.projectreachout.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.projectreachout.GeneralStatic.JSONParsingArrayFromString;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromArray;
import static com.projectreachout.GeneralStatic.LOAD_MORE;
import static com.projectreachout.GeneralStatic.REFRESH;
import static com.projectreachout.GeneralStatic.getDummyUrl;

public class MyArticles extends AppCompatActivity {

    public static ArticleListAdapter mArticleListAdapterMyArticles;

    private View mParentLayout;

    public static List<Article> mArticleItemListMyArticles;
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

        mArticleItemListMyArticles = new ArrayList<>();

        mArticleListAdapterMyArticles = new ArticleListAdapter(this, mArticleItemListMyArticles);
        listView.setAdapter(mArticleListAdapterMyArticles);

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

        String url = getDummyUrl() + "/get_my_articles/";

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
        if (mArticleListAdapterMyArticles.isEmpty()) {
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
        mArticleItemListMyArticles.clear();
        for (int i = response.length()-1; i >= 0; i--) {
            JSONObject feedObj = JSONParsingObjectFromArray(response, i);
            Article item = Article.fromJson(feedObj.toString());
            mArticleItemListMyArticles.add(item);
        }
        mArticleListAdapterMyArticles.notifyDataSetChanged();
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

package com.projectreachout.Article.GetArticle;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.projectreachout.AppController;
import com.projectreachout.Utilities.CallbackUtilities.OnServerRequestResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.projectreachout.GeneralStatic.JSONParsingArrayFromString;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromArray;
import static com.projectreachout.GeneralStatic.getDomainUrl;

public class GetArticleHandler {
    public static final String TAG = GetArticleHandler.class.getSimpleName();

    public static final int REFRESH = 1;
    public static final int LOAD_MORE = 2;
    public static final String REFRESH_VALUE = "-1";

    // private final static String url = getDomainUrl() + "/retrieve_articles/";
    private final static String url = getDomainUrl() + "/get_all_articles/";

    public static void loadArticles(OnServerRequestResponse responseInterface, int action, String lastVisibleArticleID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, output -> {
            Log.v(TAG, output);
            if (output != null) {
                parseJsonFeed(responseInterface, action, JSONParsingArrayFromString(output));
            }
        }, (VolleyError error) -> Log.v(TAG, error.toString())) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("last_viewed_id", String.valueOf(lastVisibleArticleID));
                param.put("user_id", AppController.getInstance().getFirebaseAuth().getUid());
                return param;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private static void parseJsonFeed(OnServerRequestResponse responseInterface, int action, JSONArray response) {
        List<Article> articles = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            JSONObject feedObj = JSONParsingObjectFromArray(response, i);
            Article item = Article.fromJson(feedObj.toString());
            articles.add(item);
        }
        responseInterface.onSuccess(action, articles, null );
    }
}

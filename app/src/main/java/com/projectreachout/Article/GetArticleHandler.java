package com.projectreachout.Article;

import android.util.Log;

import com.android.volley.AuthFailureError;
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
import static com.projectreachout.GeneralStatic.JSONParsingIntFromObject;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromArray;
import static com.projectreachout.GeneralStatic.JSONParsingStringFromObject;
import static com.projectreachout.GeneralStatic.getDomainUrl;
import static com.projectreachout.GeneralStatic.getDummyUrl;

public class GetArticleHandler {
    public static final String TAG = GetArticleHandler.class.getSimpleName();

    public static final int REFRESH = 1;
    public static final int LOAD_MORE = 2;
    public static final String REFRESH_VALUE = "-1";

    // private final static String url = getDomainUrl() + "/retrieve_articles/";
    private final static String url = getDummyUrl() + "/get_all_articles/";

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
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private static void parseJsonFeed(OnServerRequestResponse responseInterface, int action, JSONArray response) {
        List<ArticleItem> articleItems = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            JSONObject feedObj = JSONParsingObjectFromArray(response, i);
            String url = getDomainUrl();

            String id = JSONParsingStringFromObject(feedObj, "id");
            String teamName = JSONParsingStringFromObject(feedObj, "team_name");
            String userName = JSONParsingStringFromObject(feedObj, "username");
            String timeStamp = JSONParsingStringFromObject(feedObj, "time_stamp");
            String profilePictureUrl = JSONParsingStringFromObject(feedObj, "profile_picture_url");
            String imageUrl = JSONParsingStringFromObject(feedObj, "image_url");
            String description = JSONParsingStringFromObject(feedObj, "description");

            ArticleItem item = new ArticleItem();

            item.setId(id);
            item.setTeam_name(teamName);
            item.setUsername(userName);
            item.setTime_stamp(timeStamp);

            item.setProfile_picture_url(/*url +*/ profilePictureUrl);
            item.setImage_url(/*url +*/ imageUrl);

            item.setDescription(description);

            Log.v(TAG, id + "\n");

            articleItems.add(item);
        }
        responseInterface.onSuccess(action, articleItems, null );
    }
}

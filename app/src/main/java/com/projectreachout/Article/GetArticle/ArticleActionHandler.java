package com.projectreachout.Article.GetArticle;

import static com.projectreachout.GeneralStatic.JSONParsingArrayFromString;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromArray;
import static com.projectreachout.GeneralStatic.OPTION;
import static com.projectreachout.GeneralStatic.OPTION_ORGANIZERS;
import static com.projectreachout.GeneralStatic.ORGANIZER_LIST;
import static com.projectreachout.GeneralStatic.getDomainUrl;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.projectreachout.AppController;
import com.projectreachout.Event.AddEvent.BottomSheets.BottomSheetFragment;
import com.projectreachout.R;
import com.projectreachout.User.User;
import com.projectreachout.Utilities.MessageUtilities.MessageUtils;
import com.projectreachout.Utilities.NetworkUtils.HttpVolleyRequest;
import com.projectreachout.Utilities.NetworkUtils.OnHttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleActionHandler implements OnHttpResponse, View.OnClickListener {
    private final String TAG = ArticleActionHandler.class.getName();
    public static final String ACTION_LIKE = "like";
    public static final String ACTION_LOVE = "love";

    private final int RC_LIKE = 1;
    private final int RC_LOVE = 2;
    private final int RC_REFRESH_ARTICLE = 3;
    private final int RC_REACTED_ORGANIZERS = 4;

    private View mConvertView;
    private ProgressBar mLoadingPb;
    private List<Article> mArticles;
    private ArticleListAdapter mAdapter;
    private int mPosition;

    public ArticleActionHandler(View convertView, List<Article> articles, ArticleListAdapter adapter, int position) {
        this.mConvertView = convertView;
        this.mArticles = articles;
        this.mAdapter = adapter;
        this.mPosition = position;
        this.mLoadingPb = convertView.findViewById(R.id.pb_ail_loading);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_ail_like:
            case R.id.ib_ail_love: {
                onClickedReactionButton(view);
                break;
            }
            case R.id.tv_ail_reaction: {
                mLoadingPb.setVisibility(View.VISIBLE);
                getReactedOrganizers();
                break;
            }
        }
    }


    @Override
    public void onHttpResponse(String response, int request) {
        Log.v(TAG, response);
        switch (request) {
            case RC_LIKE:
            case RC_LOVE:
                mLoadingPb.setVisibility(View.INVISIBLE);
                if (response.equals("200")) {
                    refreshArticle();
                } else {
                    MessageUtils.showShortToast(mConvertView.getContext(), "Something went wrong!!");
                }
                break;
            case RC_REFRESH_ARTICLE:
                mLoadingPb.setVisibility(View.INVISIBLE);
                if (!response.equals("400")) {
                    updateArticle(response);
                }
                break;
            case RC_REACTED_ORGANIZERS: {
                mLoadingPb.setVisibility(View.INVISIBLE);
                parseJson(JSONParsingArrayFromString(response));
                break;
            }
        }
    }

    @Override
    public void onHttpErrorResponse(VolleyError error, int request) {
        Log.v(TAG, error.toString());
        mLoadingPb.setVisibility(View.INVISIBLE);
    }

    private void onClickedReactionButton(View view) {
        mLoadingPb.setVisibility(View.VISIBLE);
        String url = getDomainUrl() + "/on_article_reaction/";
        HttpVolleyRequest httpVolleyRequest;
        Map<String, String> param = new HashMap<>();
        param.put("article_id", mArticles.get(mPosition).getArticle_id());
        param.put("user_id", AppController.getInstance().getFirebaseAuth().getUid());

        if (view.getId() == R.id.ib_ail_like) {
            param.put("action", ACTION_LIKE);
            httpVolleyRequest = new HttpVolleyRequest(Request.Method.POST, url, null, RC_LIKE, null, param, this);
        } else {
            param.put("action", ACTION_LOVE);
            httpVolleyRequest = new HttpVolleyRequest(Request.Method.POST, url, null, RC_LOVE, null, param, this);
        }
        httpVolleyRequest.execute();
    }

    private void refreshArticle() {
        mLoadingPb.setVisibility(View.VISIBLE);
        String url = getDomainUrl() + "/get_article_details/";
        Map<String, String> param = new HashMap<>();
        param.put("article_id", mArticles.get(mPosition).getArticle_id());
        param.put("user_id", AppController.getInstance().getFirebaseAuth().getUid());

        HttpVolleyRequest httpVolleyRequest = new HttpVolleyRequest(Request.Method.POST, url, null, RC_REFRESH_ARTICLE, null, param, this);
        httpVolleyRequest.execute();
    }

    private void updateArticle(String response) {
        Article updatedArticle = Article.fromJson(response);
        mArticles.set(mPosition, updatedArticle);
        mAdapter.notifyDataSetChanged();
    }

    private void getReactedOrganizers() {
        String url = getDomainUrl() + "/get_reacted_users_on_article/";
        Map<String, String> param = new HashMap<>();
        param.put("article_id", mArticles.get(mPosition).getArticle_id());

        HttpVolleyRequest httpVolleyRequest = new HttpVolleyRequest(Request.Method.POST, url, null, RC_REACTED_ORGANIZERS, null, param, this);
        httpVolleyRequest.execute();
    }

    private void parseJson(JSONArray responseArray) {
        ArrayList<User> userList = new ArrayList<>();

        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject userJSON = JSONParsingObjectFromArray(responseArray, i);
            Article.Reaction reaction = Article.Reaction.fromJSON(userJSON.toString());
            userList.add(reaction.getUser());
        }
        showSelectedOrganizers(userList);
    }

    private void showSelectedOrganizers(ArrayList<User> users) {
        Bundle bundle = new Bundle();
        bundle.putInt(OPTION, OPTION_ORGANIZERS);
        bundle.putParcelableArrayList(ORGANIZER_LIST, users);
        bundle.putString(BottomSheetFragment.TITLE, BottomSheetFragment.TITLE_REACTED_PEOPLE);

        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.setArguments(bundle);

        bottomSheetFragment.show(((FragmentActivity)mConvertView.getContext()).getSupportFragmentManager(), bottomSheetFragment.getTag());
    }
}
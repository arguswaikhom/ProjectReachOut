package com.projectreachout.Article.GetArticle;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.projectreachout.AppController;
import com.projectreachout.R;
import com.projectreachout.Utilities.MessageUtilities.MessageUtils;
import com.projectreachout.Utilities.NetworkUtils.HttpVolleyRequest;
import com.projectreachout.Utilities.NetworkUtils.OnHttpResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.projectreachout.GeneralStatic.getDomainUrl;

public class ArticleActionHandler implements OnHttpResponse, View.OnClickListener {
    private final String TAG = ArticleActionHandler.class.getName();
    public static final String ACTION_LIKE = "like";
    public static final String ACTION_LOVE = "love";

    private final int RC_LIKE = 1;
    private final int RC_LOVE = 2;
    private final int RC_REFRESH_ARTICLE = 3;

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
}
package com.projectreachout.Article.GetArticle;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.projectreachout.AppController;
import com.projectreachout.R;
import com.projectreachout.Utilities.CallbackUtilities.OnServerRequestResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ArticleMainFragment extends Fragment implements OnServerRequestResponse, AbsListView.OnScrollListener {
    private static final String TAG = ArticleMainFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    public static ArticleListAdapter mArticleListAdapter;

    public static List<Article> mArticleList;
    private LinearLayout mErrorMessageLayout;
    private Button mRetryBtn;

    private boolean hasLockedLoadMore;
    private View mListViewProgressBarFooterView;
    private ListView mListView;

    public ArticleMainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AppController.getInstance().performIfAuthenticated(getActivity())) {
            GetArticleHandler.loadArticles(this, GetArticleHandler.REFRESH, GetArticleHandler.REFRESH_VALUE);
            mListener.onUpdateProgressVisibility(View.VISIBLE);
        }
        //BackgoundServerChecker.backgroundCheck(BackgoundServerChecker.ACTION_ARTICLE_ONLY);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_view_layout, container, false);
        if (mListener != null) {
            mListener.onFragmentInteraction(Uri.parse(getString(R.string.title_home)));
        }

        mListView = rootView.findViewById(R.id.lv_lvl_list_view);
        mErrorMessageLayout = rootView.findViewById(R.id.ll_lvl_error_message_layout);
        mRetryBtn = rootView.findViewById(R.id.btn_nel_retry);
        mListViewProgressBarFooterView = LayoutInflater.from(getContext()).inflate(R.layout.footer_progress_bar_layout, mListView, false);

        mArticleList = new ArrayList<>();

        mArticleListAdapter = new ArticleListAdapter(getActivity(), mArticleList);
        mListView.setAdapter(mArticleListAdapter);

        mListView.setOnScrollListener(this);
        return rootView;
    }

    private String getLastVisibleArticleID() {
        return mArticleList.get(mArticleList.size() - 1).getId();
    }

    private void displayErrorMessage() {
        if (mArticleListAdapter.isEmpty()) {
            mErrorMessageLayout.setVisibility(View.VISIBLE);
        } else {
            String errorMessage = "Couldn't update information from server...";
            Snackbar.make(Objects.requireNonNull(getView()), errorMessage, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // loadData(REFRESH);
                }
            }).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSuccess(int responseCode, List<?> response, String msg) {
        mListener.onUpdateProgressVisibility(View.INVISIBLE);
        if (response.isEmpty()) {
            mListView.removeFooterView(mListViewProgressBarFooterView);
            return;
        }
        if (responseCode == GetArticleHandler.REFRESH) {
            mArticleList.clear();
            mArticleList.addAll((Collection<? extends Article>) response);
        } else if (responseCode == GetArticleHandler.LOAD_MORE) {
            mArticleList.addAll((Collection<? extends Article>) response);
        }
        mArticleListAdapter.notifyDataSetChanged();
        mListView.removeFooterView(mListViewProgressBarFooterView);
        hasLockedLoadMore = false;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        /*if (!hasLockedLoadMore && firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
            mListView.addFooterView(mListViewProgressBarFooterView);
            hasLockedLoadMore = true;
            GetArticleHandler.loadArticles(this, GetArticleHandler.LOAD_MORE, getLastVisibleArticleID());
        }*/
    }

    public interface OnFragmentInteractionListener {
        void onUpdateProgressVisibility(int visibility);
        void onFragmentInteraction(Uri uri);
    }
}
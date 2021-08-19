package com.projectreachout.Article.GetArticle;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.projectreachout.AppController;
import com.projectreachout.R;
import com.projectreachout.Utilities.MessageUtilities.MessageUtils;
import com.projectreachout.Utilities.TimeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.projectreachout.Article.GetArticle.ArticleMainFragment.mArticleList;
import static com.projectreachout.Article.GetArticle.ArticleMainFragment.mArticleListAdapter;
import static com.projectreachout.Article.MyArticles.MyArticles.mArticleItemListMyArticles;
import static com.projectreachout.Article.MyArticles.MyArticles.mArticleListAdapterMyArticles;
import static com.projectreachout.GeneralStatic.getDomainUrl;

public class ArticleListAdapter extends BaseAdapter implements ArticleImageView.ResponseObserver {

    public static final String TAG = ArticleListAdapter.class.getSimpleName();

    private Activity activity;
    private LayoutInflater inflater;
    private List<Article> articles;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ArticleListAdapter(Activity activity, List<Article> articles) {
        this.activity = activity;
        this.articles = articles;
    }

    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public Object getItem(int location) {
        return articles.get(location);
    }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.pf_feed_item, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        ArticleActionHandler articleActionHandler = new ArticleActionHandler(convertView, articles, this, position);

        TextView userName = convertView.findViewById(R.id.tv_pfi_username);
        TextView timeStamp = convertView.findViewById(R.id.tv_pfi_time_stamp);
        TextView description = convertView.findViewById(R.id.tv_ail_description);
        ArticleImageView articleImageView = convertView.findViewById(R.id.iv_pfi_post_image);
        ImageButton optionsImageButton = convertView.findViewById(R.id.ibtn_pfi_overflow_button);
        CircleImageView profilePicture = convertView.findViewById(R.id.iv_pfi_profile_picture);
        ImageButton likeIBtn = convertView.findViewById(R.id.ib_ail_like);
        ImageButton loveBtn = convertView.findViewById(R.id.ib_ail_love);
        TextView reactionTV = convertView.findViewById(R.id.tv_ail_reaction);

        final Article item = articles.get(position);

        likeIBtn.setOnClickListener(articleActionHandler);
        loveBtn.setOnClickListener(articleActionHandler);
        reactionTV.setOnClickListener(articleActionHandler);

        String myReaction = item.getMy_reaction();
        if (myReaction != null && !myReaction.isEmpty()) {
            if (myReaction.equals(ArticleActionHandler.ACTION_LIKE)) {
                likeIBtn.setImageResource(R.drawable.ic_thumb_up_28dp);
                loveBtn.setImageResource(R.drawable.ic_heart_24dp);
            } else if (myReaction.equals(ArticleActionHandler.ACTION_LOVE)) {
                likeIBtn.setImageResource(R.drawable.ic_like_28dp);
                loveBtn.setImageResource(R.drawable.ic_heart_black_24dp);
            } else {
                likeIBtn.setImageResource(R.drawable.ic_like_28dp);
                loveBtn.setImageResource(R.drawable.ic_heart_24dp);
            }
        }

        userName.setText(item.getDisplay_name());
        timeStamp.setText(TimeUtil.getTimeAgaFromSecond(Long.parseLong(item.getTime_stamp())));
        reactionTV.setText(String.format("%s People", item.getReaction_count()));
        if (!TextUtils.isEmpty(item.getDescription())) {
            description.setVisibility(View.VISIBLE);
            description.setText(item.getDescription());
        } else {
            description.setVisibility(View.GONE);
        }

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_person_black_124dp).error(R.drawable.ic_person_black_124dp).centerCrop().circleCrop();
        Glide.with(profilePicture.getContext())
                .load(item.getAvatar())
                .apply(requestOptions)
                .into(profilePicture);

        if (item.getImage_url() != null) {
            articleImageView.setImageUrl(item.getImage_url(), imageLoader);
            articleImageView.setVisibility(View.VISIBLE);
            articleImageView.setResponseObserver(this);
        } else {
            articleImageView.setVisibility(View.GONE);
        }

        // TODO: remove this after share article implementation
        if (AppController.getInstance().getFirebaseAuth().getUid().equals(item.getUser_id())){
            optionsImageButton.setVisibility(View.VISIBLE);
        } else {
            optionsImageButton.setVisibility(View.GONE);
        }
        optionsImageButton.setOnClickListener(v -> showPopupMenu(v, item, position));

        return convertView;
    }

    private void showPopupMenu(View view, Article item, int position) {

        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.inflate(R.menu.evn_eei_popup_menu);

        Menu overFlowMenu = popup.getMenu();
        if (AppController.getInstance().getFirebaseAuth().getUid().equals(item.getUser_id())) {
            overFlowMenu.findItem(R.id.menu_eepm_delete).setVisible(true);
        } else {
            overFlowMenu.findItem(R.id.menu_eepm_delete).setVisible(false);
        }

        popup.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.menu_eepm_delete: {
                    deleteArticle(view, item, position);
                }
            }
            return true;
        });

        popup.show();
    }

    private void deleteArticle(View view, Article article, int position) {
        String url = getDomainUrl() + "/delete_article/";

        Map<String, String> param = new HashMap<>();
        param.put("article_id", article.getId());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            Log.v(TAG, response);
            if (response.trim().equals("200")) {
                if (mArticleList != null) mArticleList.remove(position);
                if (mArticleListAdapter != null) mArticleListAdapter.notifyDataSetChanged();
                if (mArticleItemListMyArticles != null) mArticleItemListMyArticles.remove(position);
                if (mArticleListAdapterMyArticles != null) mArticleListAdapterMyArticles.notifyDataSetChanged();
                StorageReference imageStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(article.getImage_url());
                imageStorageReference.delete();
            } else {
                MessageUtils.showNoActionShortSnackBar(view, "Something went wrong");
            }
        }, error -> {
            Log.e(TAG, error.toString());
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

    @Override
    public void onError() {

    }

    @Override
    public void onSuccess() {

    }
}

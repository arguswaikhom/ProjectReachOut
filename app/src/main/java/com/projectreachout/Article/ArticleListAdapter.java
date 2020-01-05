package com.projectreachout.Article;

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
import com.projectreachout.Login.LoginActivity;
import com.projectreachout.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.projectreachout.Article.ArticleMainFragment.mArticleItemList;
import static com.projectreachout.Article.ArticleMainFragment.mArticleListAdapter;
import static com.projectreachout.GeneralStatic.getDateTime;
import static com.projectreachout.GeneralStatic.getDummyUrl;
import static com.projectreachout.MyArticles.MyArticles.mArticleItemListMyArticles;
import static com.projectreachout.MyArticles.MyArticles.mArticleListAdapterMyArticles;

public class ArticleListAdapter extends BaseAdapter {

    public static final String LOG_TAG_FLA = ArticleListAdapter.class.getSimpleName();

    private Activity activity;
    private LayoutInflater inflater;
    private List<ArticleItem> articleItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ArticleListAdapter(Activity activity, List<ArticleItem> articleItems) {
        this.activity = activity;
        this.articleItems = articleItems;
    }



    @Override
    public int getCount() {
        return articleItems.size();
    }

    @Override
    public Object getItem(int location) {
        return articleItems.get(location);
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

        TextView teamName = convertView.findViewById(R.id.tv_pfi_team_name);
        TextView userName = convertView.findViewById(R.id.tv_pfi_username);
        TextView timeStamp = convertView.findViewById(R.id.tv_pfi_time_stamp);
        TextView description = convertView.findViewById(R.id.tv_pfi_description);
        ArticleImageView articleImageView = convertView.findViewById(R.id.iv_pfi_post_image);
        ImageButton optionsImageButton = convertView.findViewById(R.id.ibtn_pfi_overflow_button);

        final ArticleItem item = articleItems.get(position);

        teamName.setText(item.getTeam_name());

        userName.setText(item.getUsername());

        // Converting timestamp into x ago format
        /*CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTime_stamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

        timeStamp.setText(timeAgo);*/

        timeStamp.setText(getDateTime(item.getTime_stamp()));

        // Check for empty status message
        if (!TextUtils.isEmpty(item.getDescription())) {
            description.setText(item.getDescription());
            description.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            description.setVisibility(View.GONE);
        }

        // user profile pic
        //profilePicture.setImageUrl(item.getProfile_image_url(), imageLoader);

        CircleImageView profilePicture = convertView.findViewById(R.id.iv_pfi_profile_picture);

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_person_black_124dp).error(R.drawable.ic_person_black_124dp).centerCrop().circleCrop();

        Glide.with(profilePicture.getContext())
                .load(item.getProfile_picture_url())
                .apply(requestOptions)
                .into(profilePicture);

        // Feed image
        if (item.getImage_url() != null) {
            articleImageView.setImageUrl(item.getImage_url(), imageLoader);
            articleImageView.setVisibility(View.VISIBLE);
            articleImageView.setResponseObserver(new ArticleImageView.ResponseObserver() {
                @Override
                public void onError() {
                }

                @Override
                public void onSuccess() {
                    Log.v(LOG_TAG_FLA,"Image loaded : " + item.getId());
                }
            });
        } else {
            articleImageView.setVisibility(View.GONE);
        }

        if (AppController.getInstance().getUserType() != LoginActivity.AUTHORISED_USER) {
            optionsImageButton.setVisibility(View.GONE);
        }else if (item.getUsername().trim().equals(AppController.getInstance().getLoginUserUsername().trim())){
            optionsImageButton.setVisibility(View.VISIBLE);
        } else {
            optionsImageButton.setVisibility(View.GONE);
        }

        optionsImageButton.setOnClickListener(v -> showPopupMenu(v, item, position));

        return convertView;
    }

    private void showPopupMenu(View view, ArticleItem item, int position) {

        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.inflate(R.menu.evn_eei_popup_menu);

        Menu overFlowMenu = popup.getMenu();
        if (AppController.getInstance().getLoginUserUsername().equals(item.getUsername().trim())) {
            overFlowMenu.findItem(R.id.menu_eepm_delete).setVisible(true);
        } else {
            overFlowMenu.findItem(R.id.menu_eepm_delete).setVisible(false);
        }

        popup.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.menu_eepm_delete: {
                    deleteArticle(item, position);
                }
            }
            return true;
        });

        popup.show();
    }

    private void deleteArticle(ArticleItem article, int position) {
        String url = getDummyUrl() + "/delete_article/";

        Map<String, String> param = new HashMap<>();
        param.put("article_id", article.getId());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response != null) {
                if (mArticleItemList != null) mArticleItemList.remove(position);
                if (mArticleListAdapter != null) mArticleListAdapter.notifyDataSetChanged();
                if (mArticleItemListMyArticles != null) mArticleItemListMyArticles.remove(position);
                if (mArticleListAdapterMyArticles != null) mArticleListAdapterMyArticles.notifyDataSetChanged();
                StorageReference imageStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(article.getImage_url());
                imageStorageReference.delete();
                Log.d(LOG_TAG_FLA, response);
            }
        }, error -> {

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
}

package com.projectreachout.PostFeed;

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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.projectreachout.AppController;
import com.projectreachout.Login.LoginActivity;
import com.projectreachout.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.projectreachout.GeneralStatic.getDateTime;
import static com.projectreachout.GeneralStatic.getDomainUrl;
import static com.projectreachout.MyArticles.MyArticles.mFeedItemListMyArticles;
import static com.projectreachout.MyArticles.MyArticles.mFeedListAdapterMyArticles;
import static com.projectreachout.PostFeed.FeedMainFragment.mFeedItemList;
import static com.projectreachout.PostFeed.FeedMainFragment.mFeedListAdapter;

public class FeedListAdapter  extends BaseAdapter {

    public static final String LOG_TAG_FLA = FeedListAdapter.class.getSimpleName();

    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }



    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
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
        FeedImageView feedImageView = convertView.findViewById(R.id.iv_pfi_post_image);
        ImageButton optionsImageButton = convertView.findViewById(R.id.ibtn_pfi_overflow_button);

        final FeedItem item = feedItems.get(position);

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
        //profilePicture.setImageUrl(item.getProfile_picture_url(), imageLoader);

        CircleImageView profilePicture = convertView.findViewById(R.id.iv_pfi_profile_picture);

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_person_black_124dp).error(R.drawable.ic_person_black_124dp).centerCrop().circleCrop();

        Glide.with(profilePicture.getContext())
                .load(item.getProfile_picture_url())
                .apply(requestOptions)
                .into(profilePicture);

        // Feed image
        if (item.getImage_url() != null) {
            feedImageView.setImageUrl(item.getImage_url(), imageLoader);
            feedImageView.setVisibility(View.VISIBLE);
            feedImageView.setResponseObserver(new FeedImageView.ResponseObserver() {
                @Override
                public void onError() {
                }

                @Override
                public void onSuccess() {
                    Log.v(LOG_TAG_FLA,"Image loaded : " + item.getId());
                }
            });
        } else {
            feedImageView.setVisibility(View.GONE);
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

    private void showPopupMenu(View view, FeedItem item, int position) {

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
                    deleteArticle(item.getId(), position);
                }
            }
            return true;
        });

        popup.show();
    }

    private void deleteArticle(int id, int position) {
        String url = getDomainUrl() + "/delete_article/";

        Map<String, String> param = new HashMap<>();
        param.put("article_id", String.valueOf(id));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    if (mFeedItemList != null) mFeedItemList.remove(position);
                    if (mFeedListAdapter != null) mFeedListAdapter.notifyDataSetChanged();
                    if (mFeedItemListMyArticles != null) mFeedItemListMyArticles.remove(position);
                    if (mFeedListAdapterMyArticles != null) mFeedListAdapterMyArticles.notifyDataSetChanged();
                    Log.d(LOG_TAG_FLA, response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
}

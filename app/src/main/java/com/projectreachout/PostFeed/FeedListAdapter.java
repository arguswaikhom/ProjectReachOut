package com.projectreachout.PostFeed;

import android.util.Log;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.projectreachout.*;
import com.projectreachout.R;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

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
        //NetworkImageView profilePicture = convertView.findViewById(R.id.iv_pfi_profile_picture);
        FeedImageView feedImageView = convertView.findViewById(R.id.iv_pfi_post_image);

        final FeedItem item = feedItems.get(position);

        teamName.setText(item.getTeam_name());

        userName.setText(item.getUsername());

        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTime_stamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

        timeStamp.setText(timeAgo);

        // Chcek for empty status message
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
        Glide.with(profilePicture.getContext())
                .load(item.getProfile_picture_url())
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

        return convertView;
    }
}

package com.projectreachout.PostFeed;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.projectreachout.AppController;
import com.projectreachout.R;

import java.util.List;

import static com.projectreachout.GeneralStatic.getDomainUrl;

public class FeedAdapter extends ArrayAdapter<FeedItem> {

    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public FeedAdapter(Context context, int resource, List<FeedItem> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater()
                    .inflate(R.layout.pf_feed_item, parent, false);
        }

        /*if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
*/
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView teamName = convertView.findViewById(R.id.tv_pfi_team_name);
        TextView userName = convertView.findViewById(R.id.tv_pfi_username);
        TextView timeStamp = convertView.findViewById(R.id.tv_pfi_time_stamp);
        TextView description = convertView.findViewById(R.id.tv_pfi_description);
        NetworkImageView profilePicture = convertView.findViewById(R.id.iv_pfi_profile_picture);
        FeedImageView feedImageView = convertView.findViewById(R.id.iv_pfi_post_image);
        ImageButton optionImageButton = convertView.findViewById(R.id.ibtn_pfi_overflow_button);

        FeedItem item = feedItems.get(position);

        teamName.setText(item.getTeam_name());

        userName.setText(item.getUsername());

        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTime_stamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        timeStamp.setText(timeAgo);

        // Check for empty status message
        if (!TextUtils.isEmpty(item.getDescription())) {
            description.setText(item.getDescription());
            description.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            description.setVisibility(View.GONE);
        }

        // user profile pic
        profilePicture.setImageUrl(item.getProfile_picture_url(), imageLoader);

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
                }
            });
        } else {
            feedImageView.setVisibility(View.GONE);
        }

        // onClicked Option overflow Button
        optionImageButton.setOnClickListener(v -> onClickedOptions(item));

        return convertView;
    }

    private void onClickedOptions(FeedItem item) {
        int id = item.getId();
        String url = getDomainUrl();

        //TODO: implement onClicked
    }
}

package com.projectreachout.Event.AddEvent.BottomSheets;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.projectreachout.R;
import com.projectreachout.User.User;

import java.util.List;

public class OrganizersListAdapter extends ArrayAdapter<User> {
    private static final String TAG = OrganizersListAdapter.class.getSimpleName();

    public OrganizersListAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.u_user_row_item, parent, false);
        }

        User user = getItem(position);

        ImageView profileThumbnailImageView = convertView.findViewById(R.id.iv_uuri_profile_thumbnail);
        TextView usernameTextView = convertView.findViewById(R.id.tv_uuri_username);

        String username = user.getDisplay_name();
        Log.v(TAG, user + "");
        String profileThumbnailUrl = user.getProfile_image_url();

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_person_black_124dp).error(R.drawable.ic_person_black_124dp).centerCrop().circleCrop();

        Glide.with(getContext())
                .load(profileThumbnailUrl)
                .apply(requestOptions)
                .into(profileThumbnailImageView);

        usernameTextView.setText(username);

        return convertView;
    }
}

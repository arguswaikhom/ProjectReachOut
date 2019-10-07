package com.projectreachout.AddNewEvent.BottomSheets;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.projectreachout.R;
import com.projectreachout.User.UserDetails;

import java.util.List;
import java.util.Objects;

public class OrganizersListAdapter extends ArrayAdapter<UserDetails> {

    public OrganizersListAdapter(Context context, int resource, List<UserDetails> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.u_user_row_item, parent, false);
        }

        UserDetails userDetails = getItem(position);

        ImageView profileThumbnailImageView = convertView.findViewById(R.id.iv_uuri_profile_thumbnail);
        TextView usernameTextView = convertView.findViewById(R.id.tv_uuri_username);

        String username = Objects.requireNonNull(userDetails).getUser_name();
        String profileThumbnailUrl = Objects.requireNonNull(userDetails).getProfile_picture_url();

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_person_black_124dp).error(R.drawable.ic_person_black_124dp).centerCrop().circleCrop();

        Glide.with(getContext())
                .load(profileThumbnailUrl)
                .apply(requestOptions)
                .into(profileThumbnailImageView);

        usernameTextView.setText(username);

        return convertView;
    }
}

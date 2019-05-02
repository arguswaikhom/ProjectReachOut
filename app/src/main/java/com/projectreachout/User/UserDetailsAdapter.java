package com.projectreachout.User;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.projectreachout.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserDetailsAdapter extends ArrayAdapter<UserDetails> {

    public SparseBooleanArray mSelectedItems;

    public UserDetailsAdapter(Context context, int resource, List<UserDetails> objects) {
        super(context, resource, objects);
        mSelectedItems = new SparseBooleanArray();
    }

    public ArrayList<Integer> getSelectedItem() {
        ArrayList<Integer> arrayList = new ArrayList<>(mSelectedItems.size());
        for (int i = 0; i < mSelectedItems.size(); i++) {
            arrayList.add(mSelectedItems.keyAt(i));
        }
        return arrayList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.u_user_row_item, parent, false);
        }

        UserDetails userDetails = getItem(position);

        String username = Objects.requireNonNull(userDetails).getUser_name();
        String profileThumbnailUrl = Objects.requireNonNull(userDetails).getProfile_picture_url();

        ImageView profileThumbnailImageView = convertView.findViewById(R.id.iv_uuri_profile_thumbnail);
        TextView usernameTextView = convertView.findViewById(R.id.tv_uuri_username);

        try {
            Glide.with(getContext())
                    .load(profileThumbnailUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileThumbnailImageView);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        usernameTextView.setText(username);

        if(mSelectedItems.get(position)){
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.color_item_selected));
        }else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }
}

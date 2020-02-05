package com.projectreachout.ManageUser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.projectreachout.R;
import com.projectreachout.User.User;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserLayoutViewHolder>{
    private List<User> mUserList;
    private RecyclerViewClickListener mClickListener;

    public UserAdapter(List<User> users, RecyclerViewClickListener clickListener) {
        this.mUserList = users;
        this.mClickListener = clickListener;
    }

    public interface RecyclerViewClickListener {
        void recyclerViewListClicked(View v, int position);
    }

    public class UserLayoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView profileIV;
        public TextView displayNameTV;
        public TextView accountTypeTV;
        public TextView emailTV;
        public UserLayoutViewHolder(ViewGroup viewGroup) {
            super(viewGroup);
            profileIV = viewGroup.findViewById(R.id.iv_ul_profile);
            displayNameTV = viewGroup.findViewById(R.id.tv_ul_username);
            accountTypeTV = viewGroup.findViewById(R.id.tv_ul_account_type);
            emailTV = viewGroup.findViewById(R.id.tv_ul_email);

            viewGroup.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickListener.recyclerViewListClicked(view, this.getLayoutPosition());
        }
    }

    @Override
    public UserLayoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ConstraintLayout rootView = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.user_layout, parent, false);
        return new UserLayoutViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(UserLayoutViewHolder holder, int position) {
        User user = mUserList.get(position);

        Glide.with(holder.profileIV.getContext())
                .load(user.getProfile_image_url())
                .apply(new RequestOptions().circleCrop())
                .into(holder.profileIV);
        holder.displayNameTV.setText(user.getDisplay_name());
        holder.accountTypeTV.setText(user.getUser_type().toUpperCase());
        holder.emailTV.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }
}
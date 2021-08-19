package com.projectreachout.ManageUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.projectreachout.AppController;
import com.projectreachout.R;
import com.projectreachout.User.User;

import java.util.ArrayList;
import java.util.List;

public class ManageUserActivity extends AppCompatActivity implements OnRequestManageUser.OnUpdateUser, UserAdapter.RecyclerViewClickListener {

    private final String TAG = ManageUserActivity.class.getName();
    public static final String EX_USER = "user";

    private RecyclerView recyclerView;
    private UserAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<User> mUserList;
    private ProgressBar mLoadingPbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mu_activity_manage_user);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.rv_mamu_user_list);
        mLoadingPbar = findViewById(R.id.pbar_mamu_loading);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mUserList = new ArrayList<>();
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new UserAdapter(mUserList, this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AppController.getInstance().performIfAuthenticated(this)) {
            if (AppController.getInstance().isSuperUserAccount()) {
                mLoadingPbar.setVisibility(View.VISIBLE);
                OnRequestManageUser onRequestManageUser = new OnRequestManageUser(OnRequestManageUser.RC_GET_ALL_USER, this);
                onRequestManageUser.fetch();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onUpdateUser(List<User> users, int request) {
        Log.v(TAG, users.toString());
        mLoadingPbar.setVisibility(View.GONE);
        if (request == OnRequestManageUser.RC_GET_ALL_USER) {
            mUserList.clear();
            mUserList.addAll(users);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        Intent intent = new Intent(this, UserDetailsActivity.class);
        intent.putExtra(EX_USER, mUserList.get(position));
        startActivity(intent);
    }
}

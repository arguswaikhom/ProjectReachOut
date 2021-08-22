package com.projectreachout.ManageUser;

import static com.projectreachout.GeneralStatic.getDomainUrl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.projectreachout.R;
import com.projectreachout.User.User;
import com.projectreachout.Utilities.MessageUtilities.MessageUtils;
import com.projectreachout.Utilities.NetworkUtils.HttpVolleyRequest;
import com.projectreachout.Utilities.NetworkUtils.OnHttpResponse;

import java.util.HashMap;
import java.util.Map;

public class UserDetailsActivity extends AppCompatActivity implements View.OnClickListener, OnHttpResponse, MessageUtils.OnSnackBarActionListener {
    private final String TAG = UserDetailsActivity.class.getName();
    private final int RC_UPDATE_USER_ACCOUNT_TYPE = 1;

    private ImageView mProfileIV;
    private TextView mDisplayNameTV;
    private TextView mEmailTV;
    private TextView mPhoneNoTV;
    private RadioGroup mAccountTypeRG;
    private Button mUpdateAccountTypeBtn;
    private ProgressBar mLoadingPbar;

    private View mParentView;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mu_activity_user_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mParentView = findViewById(android.R.id.content);
        mProfileIV = findViewById(R.id.iv_maud_profile);
        mDisplayNameTV = findViewById(R.id.tv_maud_display_name);
        mEmailTV = findViewById(R.id.tv_maud_email);
        mPhoneNoTV = findViewById(R.id.tv_maud_phone_no);
        mAccountTypeRG = findViewById(R.id.rg_maud_account_type);
        mUpdateAccountTypeBtn = findViewById(R.id.btn_maud_submit);
        mLoadingPbar = findViewById(R.id.pbar_maud_loading);

        mUpdateAccountTypeBtn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        mUser = intent.getParcelableExtra(ManageUserActivity.EX_USER);
        setUpUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpUI() {
        getSupportActionBar().setTitle(mUser.getDisplay_name());

        Glide.with(this)
                .load(mUser.getProfile_image_url())
                .apply(new RequestOptions().circleCrop())
                .into(mProfileIV);
        mDisplayNameTV.setText(mUser.getDisplay_name());
        mPhoneNoTV.setText(mUser.getPhone_number());
        mEmailTV.setText(mUser.getEmail());

        switch (mUser.getUser_type()) {
            case User.AC_SUPERUSER:
                mAccountTypeRG.check(R.id.rb_maud_superuser);
                break;
            case User.AC_STAFF:
                mAccountTypeRG.check(R.id.rb_maud_staff);
                break;
            case User.AC_GUEST:
                mAccountTypeRG.check(R.id.rb_maud_guest);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_maud_submit) {
            mLoadingPbar.setVisibility(View.VISIBLE);
            updateAccountType();
        }
    }

    private void updateAccountType() {
        RadioButton checkedRBtn = findViewById(mAccountTypeRG.getCheckedRadioButtonId());

        String url = getDomainUrl() + "/update_account_type/";
        Map<String, String> param = new HashMap<>();
        param.put("user_id", mUser.getUser_id());
        param.put("user_type", checkedRBtn.getText().toString().toLowerCase());

        HttpVolleyRequest httpVolleyRequest = new HttpVolleyRequest(Request.Method.POST, url, null, RC_UPDATE_USER_ACCOUNT_TYPE, null, param, this);
        httpVolleyRequest.execute();
    }

    @Override
    public void onHttpResponse(String response, int request) {
        Log.v(TAG, response);
        mLoadingPbar.setVisibility(View.GONE);
        if (request == RC_UPDATE_USER_ACCOUNT_TYPE) {
            if (response.trim().equals("200")) {
                MessageUtils.showNoActionShortSnackBar(mParentView, "Account type updated.");
            } else {
                MessageUtils.showNoActionShortSnackBar(mParentView, "Something went wrong.");
            }
        }
    }

    @Override
    public void onHttpErrorResponse(VolleyError error, int request) {
        mLoadingPbar.setVisibility(View.GONE);
        Log.e(TAG, error.toString());
        if (request == RC_UPDATE_USER_ACCOUNT_TYPE) {
            MessageUtils.showActionIndefiniteSnackBar(mParentView, "Update failed!!", "RETRY", RC_UPDATE_USER_ACCOUNT_TYPE, this);
        }
    }

    @Override
    public void onActionBarClicked(View view, int requestCode) {
        if (requestCode == RC_UPDATE_USER_ACCOUNT_TYPE) {
            updateAccountType();
        }
    }
}

package com.projectreachout.Login.ForgotPassword;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.projectreachout.NetworkUtils.AsyncResponsePost;
import com.projectreachout.NetworkUtils.BackgroundAsyncPost;
import com.projectreachout.R;

import java.util.HashMap;
import java.util.Map;

import static com.projectreachout.GeneralStatic.EMAIL_KEY;
import static com.projectreachout.GeneralStatic.PHONE_NO_KEY;
import static com.projectreachout.GeneralStatic.hideKeyBoard;
import static com.projectreachout.GeneralStatic.isValidEmail;
import static com.projectreachout.GeneralStatic.isValidMobile;
import static com.projectreachout.GeneralStatic.showKeyBoard;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    private EditText mUsernameOrPasswordET;
    private ImageButton mSubmitIBtn;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ln_fp_activity_forgot_password);

        mVisible = true;
        mContentView = findViewById(R.id.rl_ifafp_fullscreen_content);

        mUsernameOrPasswordET = findViewById(R.id.et_ifafp_email_or_phone_no);
        mSubmitIBtn = findViewById(R.id.ib_ifafp_submit);

        mSubmitIBtn.setOnClickListener(this::onClickedSubmit);
    }

    private void onClickedSubmit(View view) {

        hideKeyBoard(this);

        String usernameOrPassword = mUsernameOrPasswordET.getText().toString().trim();

        Map<String, String> param = new HashMap<>();

        if (usernameOrPassword.equals("")) {
            showKeyBoard(mUsernameOrPasswordET);
            return;
        }

        if (isValidEmail(usernameOrPassword)) {
            param.put(EMAIL_KEY, usernameOrPassword);
        } else if (isValidMobile(usernameOrPassword)) {
            param.put(PHONE_NO_KEY, usernameOrPassword);
        } else {
            // TODO: Show error message
            return;
        }

        uploadForgetPasswordInfo(param);
    }

    private void uploadForgetPasswordInfo(Map<String, String> param) {

        //TODO: Remove this action later
        Intent intent = new Intent(this, CreatePasswordActivity.class);
        startActivity(intent);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(getString(R.string.http))
                .encodedAuthority(getString(R.string.localhost) + ":" + getString(R.string.port_no))
                .appendPath("forget_password");

        String url = builder.build().toString();

        BackgroundAsyncPost backgroundAsyncPost = new BackgroundAsyncPost(param, new AsyncResponsePost() {
            @Override
            public void onResponse(String output) {
                // TODO: Extract userId from the output response and startActivity() to the CreatePasswordActivity with the userId
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onProgressUpdate(int value) {

            }

            @Override
            public void onPreExecute() {

            }
        });

        backgroundAsyncPost.execute(url);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.

        //delayedHide(100);
        delayedHide(0);
    }

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}

package com.projectreachout.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.projectreachout.Login.ForgotPassword.ForgotPasswordActivity;
import com.projectreachout.NetworkUtils.AsyncResponsePost;
import com.projectreachout.NetworkUtils.BackgroundAsyncPost;
import com.projectreachout.R;

import java.util.HashMap;
import java.util.Map;

import static com.projectreachout.GeneralStatic.PASSWORD_KEY;
import static com.projectreachout.GeneralStatic.USERNAME_KEY;
import static com.projectreachout.GeneralStatic.showKeyBoard;

public class LoginActivity extends AppCompatActivity {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    private EditText mUserNameET;
    private EditText mPassWordET;

    private ImageButton mLoginIBtn;

    private TextView mForgotPasswordTV;

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
        setContentView(R.layout.ln_activity_login);

        mVisible = true;
        mContentView = findViewById(R.id.rl_al_fullscreen_content);

        mUserNameET = findViewById(R.id.et_al_username);
        mPassWordET = findViewById(R.id.et_al_password);
        mLoginIBtn = findViewById(R.id.ib_al_login);
        mForgotPasswordTV = findViewById(R.id.tv_al_forgot_password);

        // Underlining text for clickable
        mForgotPasswordTV.setPaintFlags(mForgotPasswordTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mLoginIBtn.setOnClickListener(this::onClickedLogin);

        mForgotPasswordTV.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void onClickedLogin(View view) {
        String username = mUserNameET.getText().toString().trim();
        String password = mPassWordET.getText().toString().trim();

        if(username.equals("")){
            showKeyBoard(mUserNameET);
            return;
        }

        if (password.equals("")){
            showKeyBoard(mPassWordET);
            return;
        }

        Map<String, String> param = new HashMap<>();
        param.put(USERNAME_KEY, username);
        param.put(PASSWORD_KEY, password);

        uploadLoginContent(param);
    }

    private void uploadLoginContent(Map<String, String> param) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(getString(R.string.http))
                .encodedAuthority(getString(R.string.localhost) + ":" + getString(R.string.port_no))
                .appendPath("login");

        String url = builder.build().toString();

        BackgroundAsyncPost backgroundAsyncPost = new BackgroundAsyncPost(param, new AsyncResponsePost() {
            @Override
            public void onResponse(String output) {

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

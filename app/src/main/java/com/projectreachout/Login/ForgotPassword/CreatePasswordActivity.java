package com.projectreachout.Login.ForgotPassword;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.projectreachout.R;

import java.util.HashMap;
import java.util.Map;

import static com.projectreachout.GeneralStatic.PASSWORD_KEY;
import static com.projectreachout.GeneralStatic.USER_ID_KEY;
import static com.projectreachout.GeneralStatic.getDomainUrl;
import static com.projectreachout.GeneralStatic.getRandomInt;
import static com.projectreachout.GeneralStatic.showKeyBoard;

public class CreatePasswordActivity extends AppCompatActivity {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    private EditText mNewPasswordET;
    private EditText mReEnterPasswordET;

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
        setContentView(R.layout.ln_fp_activity_create_password);

        mVisible = true;
        mContentView = findViewById(R.id.rl_ifacp_fullscreen_content);

        mNewPasswordET = findViewById(R.id.et_ifacp_new_password);
        mReEnterPasswordET = findViewById(R.id.et_ifacp_reenter_password);

        mSubmitIBtn = findViewById(R.id.ib_ifacp_submit);

        mSubmitIBtn.setOnClickListener(this::onClickedSubmit);
    }

    private void onClickedSubmit(View view) {
        String newPassword = mNewPasswordET.getText().toString().trim();
        String reEnterPassword = mReEnterPasswordET.getText().toString().trim();

        if (newPassword.equals("")){
            showKeyBoard(mNewPasswordET);
            return;
        }

        if (reEnterPassword.equals("")){
            showKeyBoard(mReEnterPasswordET);
            return;
        }

        if (!newPassword.equals(reEnterPassword)){
            // TODO: Show error message of not matching password
            return;
        }

        // TODO: Extract userId passed by ForgetPasswordActivity
        String userId = String.valueOf(getRandomInt(1000, 10000));

        Map<String, String> param = new HashMap<>();
        param.put(USER_ID_KEY, userId);
        param.put(PASSWORD_KEY, newPassword);

        uploadChangePasswordInfo(param);
    }

    private void uploadChangePasswordInfo(Map<String, String> param) {
        /*Uri.Builder builder = new Uri.Builder();
        builder.scheme(getString(R.string.http))
                .encodedAuthority(getString(R.string.localhost) + ":" + getString(R.string.port_no))
                .appendPath("new_password");*/

        String url = getDomainUrl() + "/new_password/";


        // TODO: Implement create password request
        // TODO: On good response redirect to the login activity

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

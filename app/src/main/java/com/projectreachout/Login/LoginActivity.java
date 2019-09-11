package com.projectreachout.Login;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.projectreachout.AppController;
import com.projectreachout.Login.ForgotPassword.ForgotPasswordActivity;
import com.projectreachout.MainActivity;
import com.projectreachout.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.projectreachout.GeneralStatic.JSONParsingObjectFromString;
import static com.projectreachout.GeneralStatic.JSONParsingStringFromObject;
import static com.projectreachout.GeneralStatic.getDomainUrl;
import static com.projectreachout.GeneralStatic.showKeyBoard;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    private EditText mUserNameET;
    private EditText mPassWordET;

    private Button mLoginIBtn;

    private TextView mForgotPasswordTV;

    /*private final Runnable mHidePart2Runnable = new Runnable() {
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
    };*/

    /*private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ln_activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        /*mVisible = true;
        mContentView = findViewById(R.id.rl_al_fullscreen_content);*/

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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        login();
    }

    private void onClickedLogin(View view) {
        String username = mUserNameET.getText().toString().trim();
        String password = mPassWordET.getText().toString().trim();

        if (username.equals("")) {
            showKeyBoard(mUserNameET);
            return;
        }

        if (password.equals("")) {
            showKeyBoard(mPassWordET);
            return;
        }

        uploadLoginContent(username, password);
    }

    private void uploadLoginContent(String username, String password) {

        /*Uri.Builder builder = new Uri.Builder();
        builder.scheme(getString(R.string.http))
                .encodedAuthority(getString(R.string.localhost) + ":" + getString(R.string.port_no))
                .appendPath("login")
                .appendPath("");

        String url = builder.build().toString();*/

        String url = getDomainUrl() + "/login/";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                (String response) -> {
                    if (response != null) {
                        Log.d(TAG, response);
                        if (response.trim().equals("500")) {
                            String errorMessage = "Incorrect username or password";
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            newLogin(response, password);
                        }
                    }
                },
                (VolleyError error) -> {
                    Log.d(TAG, error.toString());
                    String errorMessage = "Something went wrong!!";
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = username + ":" + password;
                Log.d(TAG, credentials);
                String auth = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void newLogin(String response, String password) {
        JSONObject jsonObject = JSONParsingObjectFromString(response);

        String username = JSONParsingStringFromObject(jsonObject, "username");
        String email = JSONParsingStringFromObject(jsonObject, "email");
        String profile_picture_url = getDomainUrl() + JSONParsingStringFromObject(jsonObject, "avatar");
        String accountType = JSONParsingStringFromObject(jsonObject, "account_type");

        AppController.getInstance().saveLoginUserCredentials(username, password, email, profile_picture_url, accountType);

        login();
    }

    private void login() {
        if (AppController.getInstance().isUserLogin()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.

        //delayedHide(100);
        //delayedHide(0);
    }

    /*private void hide() {
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
    }*/
}

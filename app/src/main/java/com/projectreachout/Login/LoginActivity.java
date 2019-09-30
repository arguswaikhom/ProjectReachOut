package com.projectreachout.Login;

import android.content.Intent;
import android.os.Bundle;
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
import com.projectreachout.MainActivity;
import com.projectreachout.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.projectreachout.GeneralStatic.JSONParsingObjectFromString;
import static com.projectreachout.GeneralStatic.JSONParsingStringFromObject;
import static com.projectreachout.GeneralStatic.getDomainUrl;
import static com.projectreachout.GeneralStatic.showKeyBoard;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    public static final String USER_TYPE = "user_type";
    public static final String GUEST_USER = "guest_user";
    public static final String AUTHORISED_USER = "a_user";

    private EditText mUserNameET;
    private EditText mPassWordET;

    private Button mLoginIBtn;

    private TextView mVisitAsGuestTV;

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
        mVisitAsGuestTV = findViewById(R.id.tv_lal_visit_as_guest);

        mLoginIBtn.setOnClickListener(this);
        mVisitAsGuestTV.setOnClickListener(this);
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

    private void onClickedLogin() {
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
            intent.putExtra(USER_TYPE, AUTHORISED_USER);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_lal_visit_as_guest: {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(USER_TYPE, GUEST_USER);
                startActivity(intent);
                finish();
            }
            case R.id.ib_al_login: {
                onClickedLogin();
                break;
            }
        }
    }
}

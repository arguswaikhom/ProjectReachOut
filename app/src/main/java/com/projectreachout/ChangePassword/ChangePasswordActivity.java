package com.projectreachout.ChangePassword;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.projectreachout.AppController;
import com.projectreachout.R;

import java.util.HashMap;
import java.util.Map;

import static com.projectreachout.GeneralStatic.getDomainUrl;
import static com.projectreachout.GeneralStatic.getVolleyErrorMessage;

public class ChangePasswordActivity extends AppCompatActivity {

    private LinearLayout mConfirmUserLL;
    private LinearLayout mCreateNewPasswordLL;

    private View mWarningBoard;

    private EditText mUsernameET;
    private EditText mPasswordET;
    private EditText mNewPasswordET;
    private EditText mConfirmPasswordET;

    private Button mConfirmBtn;
    private Button mUpdatePasswordBtn;

    private TextView mWarningTV;
    private TextView mHeaderTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cp_activity_confirm_user);

        mConfirmUserLL = findViewById(R.id.ll_cacu_confirm_user);
        mCreateNewPasswordLL = findViewById(R.id.ll_cacu_create_password);

        mWarningBoard = findViewById(R.id.cacu_warning_board);

        mWarningTV = findViewById(R.id.tv_wbl_notice);
        mHeaderTV = findViewById(R.id.tv_cacu_headed);

        mUsernameET = findViewById(R.id.et_cacu_username);
        mPasswordET = findViewById(R.id.et_cacu_password);
        mNewPasswordET = findViewById(R.id.et_cacu_new_password);
        mConfirmPasswordET = findViewById(R.id.et_cacu_confirm_password);

        mConfirmBtn = findViewById(R.id.btn_cacu_confirm_user);
        mUpdatePasswordBtn = findViewById(R.id.btn_cacu_update_password);

        showConfirmUserLayout();
        hideWarning();
        hideCreateNewPasswordLayout();

        mHeaderTV.setText("Confirm Yourself");

        mConfirmBtn.setOnClickListener(this::onClickedConfirmUser);
        mUpdatePasswordBtn.setOnClickListener(this::onClickedUpdatePassword);
    }

    private void onClickedConfirmUser(View view) {
        String username = mUsernameET.getText().toString().trim();
        String password = mPasswordET.getText().toString().trim();

        if (!AppController.getInstance().getLoginUserUsername().equals(username) || !AppController.getInstance().getLoginUserPassword().equals(password)) {
            showWarning("Username or Password Incorrect!!");
            return;
        }

        hideWarning();
        hideConfirmUserLayout();
        showCreateNewPasswordLayout();

        mHeaderTV.setText("Create New Password");
    }

    private void onClickedUpdatePassword(View view) {
        String newPassword = mNewPasswordET.getText().toString().trim();
        String confirmPassword = mConfirmPasswordET.getText().toString().trim();

        if (!newPassword.equals(confirmPassword)) {
            showWarning("Password doesn't match!!");
            return;
        }

        hideWarning();
        hideConfirmUserLayout();

        String url = getDomainUrl() + "/update_password/";

        Map<String, String> param = new HashMap<>();
        param.put("username", AppController.getInstance().getLoginUserUsername());
        param.put("password", newPassword);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response != null) {
                if (response.trim().equals("200")) {
                    Toast.makeText(this, "Password Updated", Toast.LENGTH_SHORT).show();
                    AppController.getInstance().saveLoginUserCredentials(AppController.getInstance().getLoginUserUsername(),
                            newPassword,
                            AppController.getInstance().getLoginUserEmail(),
                            AppController.getInstance().getLoginUserProfilePictureUrl(),
                            AppController.getInstance().getLoginUserAccountType());
                    onBackPressed();
                }
            }

        }, error -> {
            showWarning(getVolleyErrorMessage(error.toString()));
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppController.getInstance().getLoginCredentialHeader();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return param;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void showWarning(String string) {
        mWarningBoard.setVisibility(View.VISIBLE);
        mWarningTV.setText(string);
    }

    private void hideWarning() {
        mWarningBoard.setVisibility(View.GONE);
    }

    private void showConfirmUserLayout() {
        mConfirmUserLL.setVisibility(View.VISIBLE);
    }

    private void hideConfirmUserLayout() {
        mConfirmUserLL.setVisibility(View.GONE);
    }

    private void showCreateNewPasswordLayout() {
        mCreateNewPasswordLL.setVisibility(View.VISIBLE);
    }

    private void hideCreateNewPasswordLayout() {
        mCreateNewPasswordLL.setVisibility(View.GONE);
    }
}

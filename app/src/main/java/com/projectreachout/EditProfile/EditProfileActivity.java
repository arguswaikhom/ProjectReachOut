package com.projectreachout.EditProfile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.projectreachout.AppController;
import com.projectreachout.ChangePassword.ChangePasswordActivity;
import com.projectreachout.ImageCompression.FileUtil;
import com.projectreachout.ImageCompression.ImageCompression;
import com.projectreachout.R;
import com.projectreachout.SingleUploadBroadcastReceiver;
import com.projectreachout.Utilities.MessageUtilities.MessageUtils;
import com.projectreachout.Utilities.PermissionUtilities.DevicePermissionUtils;

import net.gotev.uploadservice.MultipartUploadRequest;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.projectreachout.GeneralStatic.JSONParsingObjectFromString;
import static com.projectreachout.GeneralStatic.JSONParsingStringFromObject;
import static com.projectreachout.GeneralStatic.getDomainUrl;
import static com.projectreachout.GeneralStatic.getVolleyErrorMessage;
import static com.projectreachout.GeneralStatic.showKeyBoard;

public class EditProfileActivity extends AppCompatActivity implements SingleUploadBroadcastReceiver.Delegate {

    private final String TAG = EditProfileActivity.class.getSimpleName();

    private DevicePermissionUtils mDevicePermissionUtils;

    private int PICK_IMAGE_REQUEST = 1;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;

    private EditText mFirstNameET;
    private EditText mLastNameET;
    private EditText mUserNameET;
    private EditText mEmailET;
    private EditText mPhoneNo;
    private EditText mLocation;
    private EditText mBio;

    private ImageView mProfilePictureIV;

    private Button mChooseProfilePictureBtn;
    private Button mChangePasswordBtn;
    private Button mSubmitBtn;

    private View mWarningView;

    private TextView mWarningTV;
    private TextView mAccountType;

    private LinearLayout mContentLL;

    private ProgressBar mProgressBar;
    private ProgressDialog mDialog;

    private SingleUploadBroadcastReceiver uploadReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ep_activity_edit_profile);

        uploadReceiver = new SingleUploadBroadcastReceiver();
        mDialog = new ProgressDialog(this);
        mDevicePermissionUtils = new DevicePermissionUtils(this);

        mDevicePermissionUtils.handlePermissions();

        mContentLL = findViewById(R.id.ll_eaep_content_layout);

        mProgressBar = findViewById(R.id.pb_eaep_progress_bar);

        mWarningView = findViewById(R.id.warning_view_eaep);
        mWarningView.setVisibility(View.GONE);

        mWarningTV = findViewById(R.id.tv_wbl_notice);
        mAccountType = findViewById(R.id.tv_eaep_account_type);

        mFirstNameET = findViewById(R.id.et_eaep_first_name);
        mLastNameET = findViewById(R.id.et_eaep_last_name);
        mUserNameET = findViewById(R.id.et_eaep_username);
        mEmailET = findViewById(R.id.et_eaep_email);
        mPhoneNo = findViewById(R.id.et_eaep_phone_no);
        mLocation = findViewById(R.id.et_eaep_location);
        mBio = findViewById(R.id.et_eaep_bio);

        mProfilePictureIV = findViewById(R.id.iv_eaep_user_profile_picture);

        mChooseProfilePictureBtn = findViewById(R.id.btn_eaep_choose_profile_picture);
        mChangePasswordBtn = findViewById(R.id.btn_eaep_change_password);
        mSubmitBtn = findViewById(R.id.btn_eaep_submit);

        hideLayoutContainer();
        showProgressBar();
        fetchData();

        mChooseProfilePictureBtn.setOnClickListener(this::uploadPost);
        mChangePasswordBtn.setOnClickListener(v -> startActivity(new Intent(this, ChangePasswordActivity.class)));

        mSubmitBtn.setOnClickListener(this::onClickedSubmit);
    }

    private void fetchData() {
        String url = getDomainUrl() + "/get_user_details/";
        Map<String, String> param = new HashMap<>();
        param.put("username", AppController.getInstance().getLoginUserUsername());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response != null) {
                if (!response.equals("401")) {
                    showLayoutContainer();
                    hideProgressBar();
                    parseData(response);
                }
            }
        }, error -> {
            hideProgressBar();
            Toast.makeText(EditProfileActivity.this, getVolleyErrorMessage(error.toString()), Toast.LENGTH_SHORT).show();
        }) {
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

    private void parseData(String response) {
        JSONObject jsonObject = JSONParsingObjectFromString(response);

        String username = JSONParsingStringFromObject(jsonObject, "username");
        String firstName = JSONParsingStringFromObject(jsonObject, "first_name");
        String lastName = JSONParsingStringFromObject(jsonObject, "last_name");
        String phoneNo = JSONParsingStringFromObject(jsonObject, "phone");
        String email = JSONParsingStringFromObject(jsonObject, "email");
        String address = JSONParsingStringFromObject(jsonObject, "address");
        String bio = JSONParsingStringFromObject(jsonObject, "bio");
        String profilePictureUrl = getDomainUrl() + JSONParsingStringFromObject(jsonObject, "avatar");
        String accountType = JSONParsingStringFromObject(jsonObject, "account_type");

        mAccountType.setText(accountType);

        mUserNameET.setText(username);
        mFirstNameET.setText(firstName);
        mLastNameET.setText(lastName);
        mPhoneNo.setText(phoneNo);
        mEmailET.setText(email);
        mLocation.setText(address);
        mBio.setText(bio);

        Glide.with(this).load(profilePictureUrl).apply(RequestOptions.circleCropTransform()).into(mProfilePictureIV);

        AppController.getInstance().saveLoginUserCredentials(username, AppController.getInstance().getLoginUserPassword(), email, profilePictureUrl, accountType);
    }

    private void uploadPost(View view) {
        if (!mDevicePermissionUtils.hasAllPermissionsGranted()) {
            MessageUtils.showShortToast(this, "Permission Required");
            mDevicePermissionUtils.requestAllPermissions();
            return;
        }
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                showError("Failed to open picture!");
                return;
            }
            try {
                File actualImage = FileUtil.from(this, data.getData());
                // mProfilePictureIV.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                ImageCompression imageCompression = new ImageCompression(this, actualImage);
                String path = imageCompression.customCompressImage();
                if (path != null) {
                    loadProfile(path);
                } else {
                    showError("Failed to read picture data!");
                }
            } catch (IOException e) {
                showError("Failed to read picture data!");
                e.printStackTrace();
            }
        }
    }

    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public String getPath(Uri uri) throws NullPointerException {
        String path;
        try {
            Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
            Objects.requireNonNull(cursor).moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = this.getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            Objects.requireNonNull(cursor).moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        } catch (Exception e) {
            return null;
        }
        return path;
    }

    private void loadProfile(String path) {
        Log.d(TAG, "Image cache path: " + path);

        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
        Glide.with(this).load(path).apply(RequestOptions.circleCropTransform()).into(mProfilePictureIV);

        String url = getDomainUrl() + "/update_user_profile_picture/";

        if (path != null) {
            String uploadId = UUID.randomUUID().toString();
            uploadReceiver.setDelegate(this);
            uploadReceiver.setUploadID(uploadId);
            try {
                new MultipartUploadRequest(this, uploadId, url)
                        .addHeader("Authorization", AppController.getInstance().getLoginCredential())
                        .addParameter("user_name", AppController.getInstance().getLoginUserUsername())
                        .addFileToUpload(path, "image")
                        .setMaxRetries(2)
                        .startUpload();

            } catch (Exception exc) {
                exc.printStackTrace();
                Toast.makeText(this, "Couldn't upload this picture...", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No picture selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void onClickedSubmit(View view) {
        String userName = mUserNameET.getText().toString();
        String firstName = mFirstNameET.getText().toString().trim();
        String lastName = mLastNameET.getText().toString().trim();
        String phoneNo = mPhoneNo.getText().toString().trim();
        String email = mEmailET.getText().toString().trim();
        String address = mLocation.getText().toString().trim();
        String bio = mBio.getText().toString().trim();


        if (userName.equals("")) {
            showWarning("UserName can't be empty");
            showKeyBoard(mUserNameET);
            return;
        }

        /*if (!email.equals("")) {
            if (!isValidMobile(phoneNo)) {
                showWarning("Invalid Phone no.");
                showKeyBoard(mPhoneNo);
                return;
            }
        }

        if (!email.equals("")) {
            if (!isValidEmail(email)) {
                showWarning("Invalid email address");
                showKeyBoard(mUserNameET);
                return;
            }
        }*/

        hideWarningView();

        String url = getDomainUrl() + "/update_user_details/";

        Map<String, String> param = new HashMap<>();
        param.put("requested_user", AppController.getInstance().getLoginUserUsername());
        param.put("username", userName);
        param.put("first_name", firstName);
        param.put("last_name", lastName);
        param.put("phone", phoneNo);
        param.put("email", email);
        param.put("address", address);
        param.put("bio", bio);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response != null) {
                if (response.trim().equals("409")) {
                    showWarning("Username already exist!! Try another name...");
                } else if (!response.trim().equals("401")) {
                    Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                    saveUserInfo(response);
                }
            }
        }, error -> showWarning(getVolleyErrorMessage(error.toString()))) {
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

    private void saveUserInfo(String response) {
        JSONObject jsonObject = JSONParsingObjectFromString(response);

        String username = JSONParsingStringFromObject(jsonObject, "username");
        String email = JSONParsingStringFromObject(jsonObject, "email");
        String profile_picture_url = getDomainUrl() + JSONParsingStringFromObject(jsonObject, "avatar");
        String accountType = JSONParsingStringFromObject(jsonObject, "account_type");

        AppController.getInstance().saveLoginUserCredentials(username, AppController.getInstance().getLoginUserPassword(), email, profile_picture_url, accountType);

        onBackPressed();
    }

    private void hideWarningView() {
        mWarningView.setVisibility(View.GONE);
    }

    private void showWarning(String s) {
        mWarningView.setVisibility(View.VISIBLE);
        mWarningTV.setText(s);
    }

    @Override
    public void onResume() {
        super.onResume();
        uploadReceiver.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        uploadReceiver.unregister(this);
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLayoutContainer() {
        mContentLL.setVisibility(View.INVISIBLE);
    }

    private void showLayoutContainer() {
        mContentLL.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onProgress(int progress) {
        mDialog.setMessage("Loading. Please wait...   " + progress + "%");
        mDialog.show();
    }

    @Override
    public void onProgress(long uploadedBytes, long totalBytes) {

    }

    @Override
    public void onError(Exception exception) {

    }

    @Override
    public void onCompleted(int serverResponseCode, byte[] serverResponseBody) {
        Log.v(TAG, "serverResponseCode: " + serverResponseCode + "\tserverResponseBody" + Arrays.toString(serverResponseBody));

        if (serverResponseCode == 200) {
            mDialog.dismiss();
            Toast.makeText(this, "Upload Completed", Toast.LENGTH_SHORT).show();
            fetchData();
        } else {
            Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCancelled() {

    }
}
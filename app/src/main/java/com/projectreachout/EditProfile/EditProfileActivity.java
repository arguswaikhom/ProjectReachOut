package com.projectreachout.EditProfile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.projectreachout.AppController;
import com.projectreachout.R;
import com.projectreachout.SingleUploadBroadcastReceiver;
import com.projectreachout.User.User;
import com.projectreachout.Utilities.ImageCompressionUtilities.FileUtil;
import com.projectreachout.Utilities.ImageCompressionUtilities.ImageCompression;
import com.projectreachout.Utilities.MessageUtilities.MessageUtils;
import com.projectreachout.Utilities.NetworkUtils.HttpVolleyRequest;
import com.projectreachout.Utilities.NetworkUtils.OnHttpResponse;
import com.projectreachout.Utilities.PermissionUtilities.DevicePermissionUtils;

import net.gotev.uploadservice.MultipartUploadRequest;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.projectreachout.GeneralStatic.getDomainUrl;
import static com.projectreachout.GeneralStatic.getDummyUrl;
import static com.projectreachout.GeneralStatic.getVolleyErrorMessage;
import static com.projectreachout.GeneralStatic.isValidMobile;
import static com.projectreachout.GeneralStatic.showKeyBoard;

public class EditProfileActivity extends AppCompatActivity implements SingleUploadBroadcastReceiver.Delegate, OnHttpResponse, MessageUtils.OnSnackBarActionListener, View.OnClickListener {

    private final String TAG = EditProfileActivity.class.getSimpleName();

    private DevicePermissionUtils mDevicePermissionUtils;

    private int PICK_IMAGE_REQUEST = 1;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;

    private EditText mUserNameET;
    private EditText mPhoneNo;
    private EditText mBio;

    private ImageView mProfilePictureIV;

    private Button mChooseProfilePictureBtn;
    private Button mSubmitBtn;

    private View mWarningView;
    private View mParentView;

    private TextView mWarningTV;
    private TextView mAccountType;
    private TextView mEmailTV;

    private LinearLayout mContentLL;

    private ProgressBar mProgressBar;
    private ProgressDialog mDialog;

    private SingleUploadBroadcastReceiver uploadReceiver;
    private final int RC_GET_USER_DETAILS = 1;
    private final int RC_UPDATE_USER_DETAILS = 2;

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

        mParentView = findViewById(android.R.id.content);
        mWarningView = findViewById(R.id.warning_view_eaep);
        mWarningView.setVisibility(View.GONE);

        mWarningTV = findViewById(R.id.tv_wbl_notice);
        mAccountType = findViewById(R.id.tv_eaep_account_type);

        mUserNameET = findViewById(R.id.et_eaep_username);
        mEmailTV = findViewById(R.id.tv_eaep_email);
        mPhoneNo = findViewById(R.id.et_eaep_phone_no);
        mBio = findViewById(R.id.et_eaep_bio);

        mProfilePictureIV = findViewById(R.id.iv_eaep_user_profile_picture);

        mChooseProfilePictureBtn = findViewById(R.id.btn_eaep_choose_profile_picture);
        mSubmitBtn = findViewById(R.id.btn_eaep_submit);

        hideLayoutContainer();
        showProgressBar();
        fetchData();

        mChooseProfilePictureBtn.setOnClickListener(this::uploadPost);

        mSubmitBtn.setOnClickListener(this);
    }

    private void fetchData() {
        String url = getDummyUrl() + "/get_user_details/";
        String user_id = AppController.getInstance().getFirebaseAuth().getUid();
        Map<String, String> param = new HashMap<>();
        param.put("user_id", user_id);

        HttpVolleyRequest httpVolleyRequest = new HttpVolleyRequest(Request.Method.POST, url, null, RC_GET_USER_DETAILS, null, param,this);
        httpVolleyRequest.execute();
    }

    private void parseData(String response) {
        User user = User.fromJson(response);
        AppController.getInstance().setUser(user);

        mAccountType.setText(user.getUser_type());
        mUserNameET.setText(user.getDisplay_name());
        mPhoneNo.setText(user.getPhone_number());
        mEmailTV.setText(user.getEmail());
        mBio.setText(user.getBio());

        Glide.with(getApplicationContext()).load(user.getProfile_image_url()).apply(RequestOptions.circleCropTransform()).into(mProfilePictureIV);
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

    private void onClickedSubmit() {
        String displayName = mUserNameET.getText().toString();
        String phoneNo = mPhoneNo.getText().toString().trim();
        String bio = mBio.getText().toString().trim();

        if (displayName.equals("")) {
            showWarning("UserName can't be empty");
            showKeyBoard(mUserNameET);
            return;
        }

        if (!phoneNo.equals("")) {
            if (!isValidMobile(phoneNo)) {
                showWarning("Invalid Phone no.");
                showKeyBoard(mPhoneNo);
                return;
            }
        }

        hideWarningView();

        String url = getDummyUrl() + "/update_user_details/";
        Map<String, String> param = new HashMap<>();
        param.put("user_id", AppController.getInstance().getFirebaseAuth().getUid());
        param.put("bio", bio);
        param.put("display_name", displayName);
        param.put("phone_number", phoneNo);

        HttpVolleyRequest httpVolleyRequest = new HttpVolleyRequest(Request.Method.POST, url, null, RC_UPDATE_USER_DETAILS,
                null, param,this);
        httpVolleyRequest.execute();
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

    @Override
    public void onHttpResponse(String response, int request) {
        Log.v(TAG, "Http response\n\n" + request + "\n\n" +  response + "\n\n");
        switch (request) {
            case RC_GET_USER_DETAILS: {
                showLayoutContainer();
                hideProgressBar();
                parseData(response);
                break;
            }
            case RC_UPDATE_USER_DETAILS: {
                if (response.trim().equals("200")) {
                    Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    MessageUtils.showActionIndefiniteSnackBar(mParentView, "Update failed!!", "RETRY", RC_UPDATE_USER_DETAILS, this);
                }
                break;
            }
        }
    }

    @Override
    public void onHttpErrorResponse(VolleyError error, int request) {
        if (request == RC_GET_USER_DETAILS) {
            hideProgressBar();
            Toast.makeText(EditProfileActivity.this, getVolleyErrorMessage(error.toString()), Toast.LENGTH_SHORT).show();
        } else if (request == RC_UPDATE_USER_DETAILS) {
            showWarning(getVolleyErrorMessage(error.toString()));
        }
    }

    @Override
    public void onActionBarClicked(View view, int requestCode) {
        if (requestCode == RC_UPDATE_USER_DETAILS) {
            onClickedSubmit();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_eaep_submit) {
            onClickedSubmit();
        }
    }
}
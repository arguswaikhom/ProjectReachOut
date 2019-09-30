package com.projectreachout;

import android.Manifest;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class AppController extends Application {
    public static final String TAG = AppController.class.getSimpleName();

    public static final String TAG_CREDENTIAL_SP = "login_user_credential";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_PASSWORD = "password";
    public static final String TAG_EMAIL = "email";
    public static final String TAG_PROFILE_PICTURE_URL = "profile_picture_url";
    public static final String TAG_ACCOUNT_TYPE = "account_type";
    public static String gUserType = "";

    public static final int STORAGE_PERMISSION_CODE = 123;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;

    private static AppController mInstance;

    private SharedPreferences mSharedPreferences;

    private int globalEventId;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mSharedPreferences = getSharedPreferences(TAG_CREDENTIAL_SP, MODE_PRIVATE);
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void setGlobalEventId(int gEventId) {
        this.globalEventId = gEventId;
    }

    public int getGlobalEventId() {
        return globalEventId;
    }

    public void saveLoginUserCredentials(String username, String password, String email, String profile_picture_url, String accountType) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(TAG_USERNAME, username);
        editor.putString(TAG_PASSWORD, password);
        editor.putString(TAG_EMAIL, email);
        editor.putString(TAG_PROFILE_PICTURE_URL, profile_picture_url);
        editor.putString(TAG_ACCOUNT_TYPE, accountType);
        editor.apply();
    }

    public boolean isUserLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(TAG_CREDENTIAL_SP, MODE_PRIVATE);
        if (sharedPreferences != null) {
            String username = sharedPreferences.getString(TAG_USERNAME, null);
            String password = sharedPreferences.getString(TAG_USERNAME, null);
            String email = sharedPreferences.getString(TAG_USERNAME, null);
            String profile_picture_url = sharedPreferences.getString(TAG_USERNAME, null);
            String account_type = sharedPreferences.getString(TAG_USERNAME, null);

            if ((username == null || password == null || email == null || profile_picture_url == null || account_type == null)) {
                return false;
            }
            return (!username.equals("") && !password.equals("") && !email.equals("") && !profile_picture_url.equals("") && !account_type.equals(""));
        }
        return true;
    }

    public void logout() {
        mSharedPreferences.edit().clear().apply();
    }

    public String getLoginUserUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences(TAG_CREDENTIAL_SP, MODE_PRIVATE);
        //return "bbake";
        return sharedPreferences.getString(TAG_USERNAME, null);
    }

    public String getLoginUserPassword() {
        SharedPreferences sharedPreferences = getSharedPreferences(TAG_CREDENTIAL_SP, MODE_PRIVATE);
        //return "bbake";
        return sharedPreferences.getString(TAG_PASSWORD, null);
    }

    public String getLoginUserEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences(TAG_CREDENTIAL_SP, MODE_PRIVATE);
        return sharedPreferences.getString(TAG_EMAIL, null);
    }

    public String getLoginUserProfilePictureUrl() {
        SharedPreferences sharedPreferences = getSharedPreferences(TAG_CREDENTIAL_SP, MODE_PRIVATE);
        return sharedPreferences.getString(TAG_PROFILE_PICTURE_URL, null);
    }

    public String getLoginUserAccountType() {
        SharedPreferences sharedPreferences = getSharedPreferences(TAG_CREDENTIAL_SP, MODE_PRIVATE);
        return sharedPreferences.getString(TAG_ACCOUNT_TYPE, null);
    }

    public String getLoginCredential() {
        String loginCredential = getLoginUserUsername() + ":" + getLoginUserPassword();
        return Base64.encodeToString(loginCredential.getBytes(), Base64.NO_WRAP);
    }

    public Map<String, String> getLoginCredentialHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", getLoginCredential());
        return headers;
    }

    public boolean isSuperUserAccount() {
        return getLoginUserAccountType().equals("superuser");
    }

    /*// Requesting permission
    public void requestPermission(Activity activity) {
        if (hasPermissionGranted())     return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            showSettingsDialog(activity);
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, STORAGE_PERMISSION_CODE);
    }*/

    public boolean hasPermissionGranted() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    /*public void showSettingsDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings(activity);
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // navigating user to app settings
    public void openSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, 101);
    }*/
}

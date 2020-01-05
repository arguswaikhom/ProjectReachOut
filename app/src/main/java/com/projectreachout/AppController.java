package com.projectreachout;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Base64;

import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.projectreachout.Login.LoginActivity;
import com.projectreachout.User.User;

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

    public static final int STORAGE_PERMISSION_CODE = 123;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;

    private static AppController mInstance;

    private SharedPreferences mSharedPreferences;
    private FirebaseAuth mFirebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private String globalEventId;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mSharedPreferences = getSharedPreferences(TAG_CREDENTIAL_SP, MODE_PRIVATE);
        mFirebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        //setUpAlarmManagerForNotificationSync();
    }

    /*private void setUpAlarmManagerForNotificationSync() {
        Intent intent = new Intent(this, NotificationAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,intent, 0);

        int intervalMillis = (int) TimeUnit.MINUTES.toMillis(BackgoundServerChecker.INTERVAL_MINUTES);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), intervalMillis, pendingIntent);
    }*/

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public FirebaseAuth getFirebaseAuth() {
        return mFirebaseAuth;
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return mGoogleSignInClient;
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

    public void setGlobalEventId(String gEventId) {
        this.globalEventId = gEventId;
    }

    public String getGlobalEventId() {
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

    public void setUserType(int userType) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(LoginActivity.USER_TYPE, userType);
        editor.apply();
    }

    public int getUserType() {
        SharedPreferences sharedPreferences = getSharedPreferences(TAG_CREDENTIAL_SP, MODE_PRIVATE);
        return sharedPreferences.getInt(LoginActivity.USER_TYPE, LoginActivity.GUEST_USER);
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

    public void clearSharedPreferences() {
        mSharedPreferences.edit().clear().apply();
    }

    public User getUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(TAG_CREDENTIAL_SP, MODE_PRIVATE);
        return new User(
                sharedPreferences.getString("user_id", null),
                getLoginUserUsername(),
                getLoginUserProfilePictureUrl(),
                sharedPreferences.getString("display_name", null),
                sharedPreferences.getString("email", null),
                sharedPreferences.getString("phone_number", null),
                getLoginUserAccountType(),
                sharedPreferences.getString("bio", null)
                );
    }

    public void setUser(User user) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("user_id", user.getUser_id());
        editor.putString(TAG_USERNAME, user.getUsername());
        editor.putString(TAG_PROFILE_PICTURE_URL, user.getProfile_image_url());
        editor.putString("display_name", user.getDisplay_name());
        editor.putString(TAG_EMAIL, user.getEmail());
        editor.putString("phone_number", user.getPhone_number());
        editor.putString(TAG_ACCOUNT_TYPE, user.getUser_type());
        editor.putString("bio", user.getBio());
        editor.apply();
    }

    public String getLoginUserUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences(TAG_CREDENTIAL_SP, MODE_PRIVATE);
        return sharedPreferences.getString(TAG_USERNAME, null);
    }

    public String getLoginUserPassword() {
        SharedPreferences sharedPreferences = getSharedPreferences(TAG_CREDENTIAL_SP, MODE_PRIVATE);
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

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    public Map<String, String> getLoginCredentialHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", getLoginCredential());
        return headers;
    }

    public boolean isSuperUserAccount() {
        return getLoginUserAccountType().equals("superuser");
    }

    public boolean hasPermissionGranted() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
    }
}

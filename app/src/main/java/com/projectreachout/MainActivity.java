package com.projectreachout;

import static com.projectreachout.GeneralStatic.FRAGMENT_ADD_POST;
import static com.projectreachout.GeneralStatic.FRAGMENT_EVENTS;
import static com.projectreachout.GeneralStatic.FRAGMENT_EXPENDITURES;
import static com.projectreachout.GeneralStatic.FRAGMENT_HOME;

import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.projectreachout.Article.AddArticle.AddNewArticleFragment;
import com.projectreachout.Article.GetArticle.ArticleMainFragment;
import com.projectreachout.Article.MyArticles.MyArticles;
import com.projectreachout.EditProfile.EditProfileActivity;
import com.projectreachout.Event.AddEvent.AddEventActivity;
import com.projectreachout.Event.GetEvent.ExpendituresMainFragment;
import com.projectreachout.Event.GetMyEvent.EventMainFragment;
import com.projectreachout.Login.FetchUserDetails;
import com.projectreachout.ManageUser.ManageUserActivity;
import com.projectreachout.User.User;
import com.projectreachout.Utilities.MessageUtilities.MessageUtils;
import com.projectreachout.Utilities.NetworkUtils.OnHttpResponse;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ArticleMainFragment.OnFragmentInteractionListener,
        AddNewArticleFragment.OnFragmentInteractionListener, EventMainFragment.OnFragmentInteractionListener,
        ExpendituresMainFragment.OnFragmentInteractionListener, View.OnClickListener, InstallStateUpdatedListener,
        MessageUtils.OnSnackBarActionListener, OnHttpResponse {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FragmentManager mFragmentManager;
    private View mHeaderView;
    private TextView mAccountTypeTV;
    private TextView mUsernameTV;
    private TextView mEmailTV;
    private TextView mUpdateAvailableTV;
    private TextView guestNoteTV;
    private ImageView mUserProfilePictureIV;
    private Button mMyArticlesBtn;
    private Button mEditProfileBtn;
    private ImageButton mShareAppLinkIB;
    private Toolbar mToolBar;
    private ProgressBar mLoadingPb;
    private View mInUpdateBoardV;
    private View mGuestNoteV;
    private final int RC_GET_USER_DETAILS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolBar = findViewById(R.id.sp_toolbar);
        setSupportActionBar(mToolBar);
        mLoadingPb = findViewById(R.id.pb_abm_loading);

        mFragmentManager = getSupportFragmentManager();
        loadFragment(new ArticleMainFragment(), FRAGMENT_HOME);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (AppController.getInstance().isAuthenticated()) {
            setUpNavView();
            inAppUpdateUtil();
            showUserDetails();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IN_APP_UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                String msg = "Update flow failed! Result code: " + resultCode;
                Log.v(TAG, "inApp: " + msg);
                MessageUtils.showShortToast(this, msg);

                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }

    private void setUpNavView() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mHeaderView = navigationView.getHeaderView(0);
        mGuestNoteV = mHeaderView.findViewById(R.id.cv_nhm_guest_note);
        guestNoteTV = mGuestNoteV.findViewById(R.id.tv_wbl_notice);

        Menu menu = navigationView.getMenu();
        try {
            if (AppController.getInstance().isSuperUserAccount()) {
                menu.findItem(R.id.nav_home).setVisible(true);
                menu.findItem(R.id.nav_add_article).setVisible(true);
                menu.findItem(R.id.nav_my_events).setVisible(true);
                menu.findItem(R.id.nav_events).setVisible(true);
                menu.findItem(R.id.nav_add_event).setVisible(true);
                menu.findItem(R.id.nav_manage_user).setVisible(true);
            } else if (AppController.getInstance().isStaffUserAccount()) {
                menu.findItem(R.id.nav_home).setVisible(true);
                menu.findItem(R.id.nav_add_article).setVisible(true);
                menu.findItem(R.id.nav_my_events).setVisible(true);
            } else {
                menu.findItem(R.id.nav_home).setVisible(true);
                showGuestNote();
            }
        } catch (NullPointerException npe) {
            AppController.getInstance().signOut(this);
        }

        mAccountTypeTV = mHeaderView.findViewById(R.id.tv_nhm_account_type);
        mUsernameTV = mHeaderView.findViewById(R.id.tv_nhm_username);
        mUserProfilePictureIV = mHeaderView.findViewById(R.id.iv_nhm_user_profile_picture);
        mEmailTV = mHeaderView.findViewById(R.id.tv_nhm_email);
        mMyArticlesBtn = mHeaderView.findViewById(R.id.btn_nhm_my_articles);
        mEditProfileBtn = mHeaderView.findViewById(R.id.btn_nhm_edit_profile);
        mShareAppLinkIB = mHeaderView.findViewById(R.id.ib_nhm_share_app_link);

        mInUpdateBoardV = mHeaderView.findViewById(R.id.cv_nhm_update_available);
        mUpdateAvailableTV = mInUpdateBoardV.findViewById(R.id.tv_wbl_notice);

        mUpdateAvailableTV.setOnClickListener(this);
        mShareAppLinkIB.setOnClickListener(this);
        mMyArticlesBtn.setOnClickListener(this::onClickedMyArticles);
        mEditProfileBtn.setOnClickListener(this::onClickedEditProfile);
    }

    private void onClickedEditProfile(View view) {
        startActivity(new Intent(this, EditProfileActivity.class));
    }

    private void onClickedMyArticles(View view) {
        startActivity(new Intent(this, MyArticles.class));
    }

    private boolean hasBackStack = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (hasBackStack) {
            hasBackStack = false;
            loadFragment(new ArticleMainFragment(), FRAGMENT_HOME);
        } else {
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AppController.getInstance().performIfAuthenticated(this)) {
            FetchUserDetails.fetch(this, RC_GET_USER_DETAILS);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (AppController.getInstance().getUserType() == LoginActivity.GUEST_USER) {
            getMenuInflater().inflate(R.menu.activity_main_option_menu, menu);
        }
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.amom_login: {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home: {
                hasBackStack = false;
                loadFragment(new ArticleMainFragment(), FRAGMENT_HOME);
                break;
            }
            case R.id.nav_add_article: {
                hasBackStack = true;
                loadFragment(new AddNewArticleFragment(), FRAGMENT_ADD_POST);
                break;
            }
            case R.id.nav_my_events: {
                hasBackStack = true;
                loadFragment(new EventMainFragment(), FRAGMENT_EVENTS);
                break;
            }
            case R.id.nav_events: {
                hasBackStack = true;
                loadFragment(new ExpendituresMainFragment(), FRAGMENT_EXPENDITURES);
                break;
            }
            case R.id.nav_add_event: {
                startActivity(new Intent(this, AddEventActivity.class));
                break;
            }
            case R.id.nav_manage_user: {
                startActivity(new Intent(this, ManageUserActivity.class));
                break;
            }
            case R.id.nav_share: {
                break;
            }
            case R.id.nav_logout: {
                AppController.getInstance().signOut(this);
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        TextView title = mToolBar.findViewById(R.id.tv_abm_title);
        title.setText(uri.toString());
    }

    @Override
    public void onUpdateProgressVisibility(int visibility) {
        mLoadingPb.setVisibility(visibility);
    }

    private void loadFragment(Fragment fragment, String name) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_abm_include, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_nhm_share_app_link: {
                shareAppLink();
                break;
            }
            case R.id.cv_nhm_update_available: {
                inAppUpdateUtil();
                break;
            }
        }
    }

    private void shareAppLink() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_link));
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    // InAppUpdate
    private final int INSTALL_REQUEST_CODE = 1;
    private final int IN_APP_UPDATE_REQUEST_CODE = 100;
    private AppUpdateManager mAppUpdateManager = null;

    private void inAppUpdateUtil() {
        Log.d(TAG, "inApp: inAppUpdateUtil()");
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.registerListener(this);
        Task<AppUpdateInfo> appUpdateInfoTask = mAppUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                requestUpdate(appUpdateInfo);
                Log.d(TAG, "inApp: inAppUpdateUtil() -> Update Available");
                showUpdateAvailable();
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            }
        });
    }

    private void requestUpdate(AppUpdateInfo appUpdateInfo) {
        Log.d(TAG, "inApp: requestUpdate()");
        try {
            mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, IN_APP_UPDATE_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStateUpdate(InstallState installState) {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackBarForCompleteUpdate();
        } else if (installState.installStatus() == InstallStatus.INSTALLED) {
            Log.d(TAG, "inApp: installed");
            Log.d(TAG, "inApp: manager: " + mAppUpdateManager);

            if (mAppUpdateManager != null) {
                mAppUpdateManager.unregisterListener(this);
            }
        } else {
            Log.i(TAG, "InstallStateUpdatedListener: state: " + installState.installStatus());
        }
    }

    private void popupSnackBarForCompleteUpdate() {
        Log.d(TAG, "inApp: popupSnackBarForCompleteUpdate()");

        View parentView = findViewById(android.R.id.content);
        MessageUtils.showActionIndefiniteSnackBar(parentView, "New app is ready!!", "Install", INSTALL_REQUEST_CODE, this);
    }

    @Override
    public void onActionBarClicked(View view, int requestCode) {
        Log.d(TAG, "inApp: onActionBarClicked()");

        if (requestCode == INSTALL_REQUEST_CODE) {
            if (mAppUpdateManager != null) {
                mAppUpdateManager.completeUpdate();
            }
        }
    }

    @Override
    public void onHttpResponse(String response, int request) {
        Log.v(TAG, response);
        if (request == RC_GET_USER_DETAILS) {
            AppController.getInstance().setUser(User.fromJson(response));
            showUserDetails();
        }
    }

    private void showUserDetails() {
        User user = AppController.getInstance().getUser();
        if (user.getUser_type() != null)
            mAccountTypeTV.setText(user.getUser_type());
        if (user.getDisplay_name() != null)
            mUsernameTV.setText(user.getDisplay_name());
        if (user.getEmail() != null)
            mEmailTV.setText(user.getEmail());
        if (URLUtil.isValidUrl(user.getProfile_image_url()))
            Glide.with(getApplicationContext())
                    .load(user.getProfile_image_url())
                    .apply(new RequestOptions().circleCrop())
                    .into(mUserProfilePictureIV);
    }

    @Override
    public void onHttpErrorResponse(VolleyError error, int request) {
        Log.v(TAG, "" + error);
    }

    private void showGuestNote() {
        String guestNote = "This app was designed only for people who work for Project ReachOut (PRO). Since your email is not in our database your account was created as a guest account.\n" +
                "\n" +
                "If you're a member of PRO contact one of our administrator to upgrade your account.";

        mGuestNoteV.setVisibility(View.VISIBLE);
        guestNoteTV.setText(guestNote);
    }

    private void showUpdateAvailable() {
        String updateAvailableMsg = "New update available!! Click here to update..";
        mInUpdateBoardV.setVisibility(View.VISIBLE);
        mUpdateAvailableTV.setText(updateAvailableMsg);
    }
}
package com.projectreachout;

import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.projectreachout.AddNewEvent.AddEventActivity;
import com.projectreachout.AddNewPost.AddNewPostFragment;
import com.projectreachout.EditProfile.EditProfileActivity;
import com.projectreachout.Event.EventMainFragment;
import com.projectreachout.Event.Expenditures.ExpendituresMainFragment;
import com.projectreachout.Login.LoginActivity;
import com.projectreachout.MyArticles.MyArticles;
import com.projectreachout.PostFeed.FeedMainFragment;
import com.projectreachout.Utilities.MessageUtilities.MessageUtils;

import static com.projectreachout.GeneralStatic.FRAGMENT_ADD_POST;
import static com.projectreachout.GeneralStatic.FRAGMENT_EVENTS;
import static com.projectreachout.GeneralStatic.FRAGMENT_EXPENDITURES;
import static com.projectreachout.GeneralStatic.FRAGMENT_HOME;
import static com.projectreachout.AppController.gUserType;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FeedMainFragment.OnFragmentInteractionListener,
        AddNewPostFragment.OnFragmentInteractionListener, EventMainFragment.OnFragmentInteractionListener,
        ExpendituresMainFragment.OnFragmentInteractionListener, View.OnClickListener, InstallStateUpdatedListener, MessageUtils.OnSnackBarActionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FragmentManager mFragmentManager;

    private View mHeaderView;

    private TextView mAccountTypeTV;
    private TextView mUsernameTV;
    private TextView mEmailTV;

    private ImageView mUserProfilePictureIV;

    private Button mMyArticlesBtn;
    private Button mEditProfileBtn;

    private ImageButton mShareAppLinkIB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.sp_toolbar);
        setSupportActionBar(toolbar);

        mFragmentManager = getSupportFragmentManager();

        loadFragment(new FeedMainFragment(), FRAGMENT_HOME);

        gUserType = getIntent().getStringExtra(LoginActivity.USER_TYPE);
        if(gUserType.equals(LoginActivity.AUTHORISED_USER)) {
            implementDrawerLayout(toolbar);
            setUpNavView();
        }

        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.registerListener(this);
        inAppUpdateUtil();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IN_APP_UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                String msg = "Update flow failed! Result code: " + resultCode;
                Log.v(TAG, msg);
                MessageUtils.showShortToast(this, msg);

                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }

    private void implementDrawerLayout(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setUpNavView() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        if (AppController.getInstance().isSuperUserAccount()) {
            menu.findItem(R.id.nav_events).setVisible(true);
            menu.findItem(R.id.nav_add_event).setVisible(true);
        } else {
            menu.findItem(R.id.nav_events).setVisible(false);
            menu.findItem(R.id.nav_add_event).setVisible(false);
        }

        mHeaderView = navigationView.getHeaderView(0);

        mAccountTypeTV = mHeaderView.findViewById(R.id.tv_nhm_account_type);
        mUsernameTV = mHeaderView.findViewById(R.id.tv_nhm_username);
        mUserProfilePictureIV = mHeaderView.findViewById(R.id.iv_nhm_user_profile_picture);
        mEmailTV = mHeaderView.findViewById(R.id.tv_nhm_email);
        mMyArticlesBtn = mHeaderView.findViewById(R.id.btn_nhm_my_articles);
        mEditProfileBtn = mHeaderView.findViewById(R.id.btn_nhm_edit_profile);
        mShareAppLinkIB = mHeaderView.findViewById(R.id.ib_nhm_share_app_link);

        mShareAppLinkIB.setOnClickListener(this);
        mMyArticlesBtn.setOnClickListener(this::onClickedMyArticles);
        mEditProfileBtn.setOnClickListener(this::onClickedEditProfile);
    }

    private void onClickedEditProfile(View view) {
        startActivity(new Intent(this, EditProfileActivity.class));
    }

    private void displayUserDetails() {
        AppController appController = AppController.getInstance();

        String username = appController.getLoginUserUsername();
        String email = appController.getLoginUserEmail();
        String profile_picture_url = appController.getLoginUserProfilePictureUrl();
        String account_type = appController.getLoginUserAccountType();

        mUsernameTV.setText(username);
        mEmailTV.setText(email);
        mAccountTypeTV.setText(account_type);

        try {
            Glide.with(this)
                    .load(profile_picture_url)
                    .apply(RequestOptions.circleCropTransform())
                    .into(mUserProfilePictureIV);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

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
            loadFragment(new FeedMainFragment(), FRAGMENT_HOME);
        } else {
            finish();
        }
        //super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (gUserType.equals(LoginActivity.AUTHORISED_USER)){
            login();
            displayUserDetails();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAppUpdateManager != null) {
            inAppUpdateUtil();
        } else {
            mAppUpdateManager = AppUpdateManagerFactory.create(this);
            mAppUpdateManager.registerListener(this);
            inAppUpdateUtil();
        }
    }

    private void login() {
        if (!AppController.getInstance().isUserLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (gUserType.equals(LoginActivity.GUEST_USER)) {
            getMenuInflater().inflate(R.menu.activity_main_option_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.amom_login: {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home: {
                hasBackStack = false;
                loadFragment(new FeedMainFragment(), FRAGMENT_HOME);
                break;
            }
            case R.id.nav_add_article: {
                hasBackStack = true;
                loadFragment(new AddNewPostFragment(), FRAGMENT_ADD_POST);
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
            case R.id.nav_share: {
                break;
            }
            case R.id.nav_send_report: {
                startActivity(new Intent(this, LoginActivity.class));
                break;
            }
            case R.id.nav_logout: {
                logOut();
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOut() {
        AppController.getInstance().logout();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        try {
            getSupportActionBar().setTitle(uri.toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void loadFragment(Fragment fragment, String name) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_abm_include, fragment);
        fragmentTransaction.commit();
    }

    private void inflateFeedMainFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_abm_include, new FeedMainFragment())
                .commit();
    }

    private void inflateAddNewPostFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_abm_include, new AddNewPostFragment())
                .commit();
    }

    private void inflateEventMainFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_abm_include, new EventMainFragment())
                .commit();
    }

    private void inflateExpendituresMainFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_abm_include, new ExpendituresMainFragment())
                .commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_nhm_share_app_link: {
                shareAppLink();
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
        Task<AppUpdateInfo> appUpdateInfoTask = mAppUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                requestUpdate(appUpdateInfo);
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            }
        });
    }

    private void requestUpdate(AppUpdateInfo appUpdateInfo) {
        try {
            mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, IN_APP_UPDATE_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStateUpdate(InstallState installState) {
        if (installState.installStatus() == InstallStatus.DOWNLOADED){
            popupSnackBarForCompleteUpdate();
        } else if (installState.installStatus() == InstallStatus.INSTALLED){
            if (mAppUpdateManager != null){
                mAppUpdateManager.unregisterListener(this);
            }
        } else {
            Log.i(TAG, "InstallStateUpdatedListener: state: " + installState.installStatus());
        }
    }

    private void popupSnackBarForCompleteUpdate() {
        View parentView = findViewById(android.R.id.content);
        MessageUtils.showActionIndefiniteSnackBar(parentView, "New app is ready!!", "Install", INSTALL_REQUEST_CODE, this);
    }

    @Override
    public void onActionBarClicked(View view, int requestCode) {
        if (requestCode == INSTALL_REQUEST_CODE) {
            if (mAppUpdateManager != null){
                mAppUpdateManager.completeUpdate();
            }
        }
    }
}
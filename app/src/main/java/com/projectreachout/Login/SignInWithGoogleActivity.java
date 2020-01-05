package com.projectreachout.Login;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.projectreachout.AppController;
import com.projectreachout.MainActivity;
import com.projectreachout.R;
import com.projectreachout.Utilities.MessageUtilities.MessageUtils;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

public class SignInWithGoogleActivity extends AppCompatActivity implements View.OnClickListener, MessageUtils.OnSnackBarActionListener {
    private View mParentView;
    private final int RC_SIGN_IN = 100;
    private final String TAG = SignInWithGoogleActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ln_activity_sign_in_with_google);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        mParentView = findViewById(android.R.id.content);
        RelativeLayout mRootLayout = findViewById(R.id.iasiwg_root_layout);
        GoogleSignInButton mSignInButton = findViewById(R.id.iasiwg_sign_in_button);
        mSignInButton.setOnClickListener(this);

        AnimationDrawable animationDrawable = (AnimationDrawable) mRootLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);

        animationDrawable.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = AppController.getInstance().getFirebaseAuth().getCurrentUser();
        if (currentUser != null) {
            signedIn();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iasiwg_sign_in_button) {
            signInWithGoogle();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                MessageUtils.showActionIndefiniteSnackBar(mParentView, "Sign in failed", "RETRY", RC_SIGN_IN, SignInWithGoogleActivity.this);
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            } catch (Exception e) {
                MessageUtils.showActionIndefiniteSnackBar(mParentView, "Sign in failed", "RETRY", RC_SIGN_IN, SignInWithGoogleActivity.this);
                Log.w(TAG, "signInResult:failed=" + e.getStackTrace());
            }
        }
    }

    private void firebaseAuthWithGoogle(@NonNull GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        AppController.getInstance().getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = AppController.getInstance().getFirebaseAuth().getCurrentUser();
                        if (user != null) {
                            signedIn();
                        } else {
                            MessageUtils.showActionIndefiniteSnackBar(mParentView, "Sign in failed", "RETRY", RC_SIGN_IN, SignInWithGoogleActivity.this);
                        }
                    } else {
                        MessageUtils.showActionIndefiniteSnackBar(mParentView, "Sign in failed", "RETRY", RC_SIGN_IN, SignInWithGoogleActivity.this);
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void signedIn() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void signInWithGoogle() {
        Intent signInIntent = AppController.getInstance().getGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActionBarClicked(View view, int requestCode) {
        if (requestCode == RC_SIGN_IN) {
            signInWithGoogle();
        }
    }
}

package com.mhr.demoapp.login;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mhr.demoapp.R;
import com.mhr.demoapp.dashboard.DashboardActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private static final String TAG = "LoginActivity";
    @Inject
    LoginPresenter loginPresenter;
    GoogleApiClient googleApiClient;

    private CallbackManager mCallbackManager;

    // [END declare_auth]
    private FirebaseAuth mAuth;
    // [START declare_auth]

    // [END declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    // [START declare_auth_listener]

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        ((View) findViewById(R.id.rootView)).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = ((View) findViewById(R.id.rootView)).getRootView().getHeight() - ((View) findViewById(R.id.rootView)).getHeight();
                if (heightDiff > dpToPx(LoginActivity.this, 200)) { // if more than 200 dp, it's probably a keyboard...
                    ((LinearLayout) findViewById(R.id.layout_bottom)).setVisibility(View.GONE);
                } else {
                    ((LinearLayout) findViewById(R.id.layout_bottom)).setVisibility(View.VISIBLE);
                }
            }
        });
        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.putExtra(getString(R.string.name_of_user), user.getDisplayName());
                    startActivity(intent);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]
                loginPresenter.hideProgress();
                // [END_EXCLUDE]
            }
        };
        // [END auth_state_listener]
        loginPresenter.loadLogin();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            loginPresenter.hideProgress();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                loginPresenter.firebaseAuthWithGoogle(account);
            }
        }
    }

    @Override
    public void onLoginLoaded(GoogleApiClient googleApiClient, CallbackManager mCallbackManager, FirebaseAuth mAuth) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = new String(Base64.encode(md.digest(), 0));
                Log.d("MY KEY HASH:", sign);
                //  Toast.makeText(getApplicationContext(),sign,     Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }

        this.mAuth = mAuth;
        this.googleApiClient = googleApiClient;
        this.mCallbackManager = mCallbackManager;

        final LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                loginPresenter.handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                loginPresenter.hideProgress();
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                loginPresenter.hideProgress();
                // [END_EXCLUDE]
            }
        });

        ((AppCompatButton) findViewById(R.id.button_facebook)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
                //loginPresenter.loginWithFacebook();
            }
        });
    }

    public void onClickSignInGoogle(View v) {
        loginPresenter.showProgress(getString(R.string.message_dialog_signing_in));
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(this.googleApiClient);
        googleApiClientSignOut();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onClickSignIn(View v) {
        loginPresenter.initiateLogin();
    }

    public void onClickRegister(View v) {
        loginPresenter.initiateRegistration();
    }

    public void onClickLayout(View v) {
        loginPresenter.toggleViews();
    }

    private void googleApiClientSignOut() {
        try {
            Auth.GoogleSignInApi.signOut(this.googleApiClient);
        } catch (Exception e) {
        }
    }

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // [END on_start_add_listener]
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
}

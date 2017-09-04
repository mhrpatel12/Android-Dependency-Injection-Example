package com.mhr.demoapp.login;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.util.Log;
import android.view.View;

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
import com.mhr.demoapp.R;

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

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
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
    public void onLoginLoaded(GoogleApiClient googleApiClient, CallbackManager mCallbackManager) {
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
}

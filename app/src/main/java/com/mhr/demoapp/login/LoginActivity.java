package com.mhr.demoapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mhr.demoapp.R;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class LoginActivity extends AppCompatActivity implements LoginView {

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
        loginPresenter.loadLogin(googleApiClient);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
    public void onLoginLoaded(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
        ((AppCompatButton) findViewById(R.id.button_facebook)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
                loginPresenter.loginWithFacebook(loginButton);
            }
        });
        Log.v("TEST", "Login page is loaded.");
    }

    public void onClickSignInGoogle(View v) {
        loginPresenter.showProgress(getString(R.string.message_dialog_signing_in));
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(this.googleApiClient);
        googleApiClientSignOut();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void googleApiClientSignOut() {
        try {
            Auth.GoogleSignInApi.signOut(this.googleApiClient);
        } catch (Exception e) {
        }
    }
}

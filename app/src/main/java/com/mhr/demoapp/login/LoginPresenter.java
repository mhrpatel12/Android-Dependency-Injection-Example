package com.mhr.demoapp.login;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by mertsimsek on 25/05/2017.
 */

public interface LoginPresenter {
    void loadLogin(GoogleApiClient googleApiClient);

    void showProgress(String dialogMessage);

    void hideProgress();

    void firebaseAuthWithGoogle(GoogleSignInAccount acct);

    void loginWithFacebook(LoginButton loginButton);
}

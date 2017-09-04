package com.mhr.demoapp.login;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by mertsimsek on 25/05/2017.
 */

public interface LoginPresenter {
    void loadLogin();

    void showProgress(String dialogMessage);

    void hideProgress();

    void firebaseAuthWithGoogle(GoogleSignInAccount acct);

    void toggleViews();

    void initiateLogin();

    void initiateRegistration();

    void handleFacebookAccessToken(AccessToken token);
}

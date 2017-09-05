package com.mhr.demoapp.login;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by Mihir on 05/09/2017.
 */

public interface LoginPresenter {
    void loadLogin();

    void showProgress(String dialogMessage);

    void hideProgress();

    void firebaseAuthWithGoogle(GoogleSignInAccount acct);

    void toggleViews();

    void initiateLogin();

    boolean isGPSEnables();

    void buildAlertMessageNoGps();

    void initiateRegistration();

    void handleFacebookAccessToken(AccessToken token);

    void hideKeyboard();
}

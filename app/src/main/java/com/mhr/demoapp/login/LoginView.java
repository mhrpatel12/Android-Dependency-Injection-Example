package com.mhr.demoapp.login;

import com.facebook.CallbackManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by mertsimsek on 25/05/2017.
 */

public interface LoginView {
    void onLoginLoaded(GoogleApiClient googleApiClient, CallbackManager mCallbackManager,FirebaseAuth mAuth);
}

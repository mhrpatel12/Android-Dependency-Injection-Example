package com.mhr.demoapp.login;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by mertsimsek on 25/05/2017.
 */

public interface LoginView {
    void onLoginLoaded(GoogleApiClient googleApiClient);
}

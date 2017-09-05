package com.mhr.demoapp.dashboard;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mertsimsek on 25/05/2017.
 */

public interface DashboardPresenter {
    void loadDashboard();

    void logTimeAndLocation(String dateTime, LatLng latLng);

    void showProgress(String dialogMessage);

    void hideProgress();

    void logout();
}

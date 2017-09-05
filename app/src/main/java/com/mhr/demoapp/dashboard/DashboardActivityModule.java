package com.mhr.demoapp.dashboard;

import com.mhr.demoapp.data.DatabaseService;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mertsimsek on 25/05/2017.
 */
@Module
public class DashboardActivityModule {

    DashboardActivity dashboardActivity;

    @Provides
    DashboardView provideDashboardView(DashboardActivity dashboardActivity) {
        this.dashboardActivity = dashboardActivity;
        return dashboardActivity;
    }

    @Provides
    DashboardPresenter provideDashboardPresenter(DashboardView dashboardView, DatabaseService databaseService) {
        return new DashboardPresenterImpl(dashboardActivity, dashboardView, databaseService);
    }
}

package com.mhr.demoapp.dashboard;

import com.mhr.demoapp.login.LoginActivityModule;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by mertsimsek on 25/05/2017.
 */
@Subcomponent(modules = DashboardActivityModule.class)
public interface DashboardActivityComponent extends AndroidInjector<DashboardActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<DashboardActivity> {
    }
}

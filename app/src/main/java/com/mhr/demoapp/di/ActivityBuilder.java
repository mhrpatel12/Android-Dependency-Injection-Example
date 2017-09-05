package com.mhr.demoapp.di;

import android.app.Activity;

import com.mhr.demoapp.dashboard.DashboardActivity;
import com.mhr.demoapp.dashboard.DashboardActivityComponent;
import com.mhr.demoapp.login.LoginActivity;
import com.mhr.demoapp.login.LoginActivityComponent;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * Created by Mihir on 05/09/2017.
 */
@Module
public abstract class ActivityBuilder {

    @Binds
    @IntoMap
    @ActivityKey(LoginActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindMainActivity(LoginActivityComponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(DashboardActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindDetailActivity(DashboardActivityComponent.Builder builder);
}

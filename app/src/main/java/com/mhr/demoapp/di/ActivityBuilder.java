package com.mhr.demoapp.di;

import android.app.Activity;

import com.mhr.demoapp.login.LoginActivity;
import com.mhr.demoapp.login.LoginActivityComponent;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * Created by mertsimsek on 25/05/2017.
 */
@Module
public abstract class ActivityBuilder {

    @Binds
    @IntoMap
    @ActivityKey(LoginActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindMainActivity(LoginActivityComponent.Builder builder);
}

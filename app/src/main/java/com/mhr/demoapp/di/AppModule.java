package com.mhr.demoapp.di;

import android.app.Application;
import android.content.Context;

import com.mhr.demoapp.dashboard.DashboardActivityComponent;
import com.mhr.demoapp.login.LoginActivityComponent;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Mihir on 05/09/2017.
 */
@Module(subcomponents = {
        LoginActivityComponent.class,
        DashboardActivityComponent.class})
public class AppModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }

}

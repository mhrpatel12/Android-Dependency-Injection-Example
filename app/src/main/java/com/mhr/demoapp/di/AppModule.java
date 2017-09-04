package com.mhr.demoapp.di;

import android.app.Application;
import android.content.Context;

import com.mhr.demoapp.login.LoginActivityComponent;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mertsimsek on 25/05/2017.
 */
@Module(subcomponents = {
        LoginActivityComponent.class})
public class AppModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }

}

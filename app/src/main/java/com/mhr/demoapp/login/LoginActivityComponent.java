package com.mhr.demoapp.login;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by mertsimsek on 25/05/2017.
 */
@Subcomponent(modules = LoginActivityModule.class)
public interface LoginActivityComponent extends AndroidInjector<LoginActivity>{
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<LoginActivity>{}
}

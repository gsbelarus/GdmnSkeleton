package com.gsbelarus.gedemin.skeleton.app;

import android.support.annotation.NonNull;

import com.gsbelarus.gedemin.skeleton.base.BaseApplication;


public class App extends BaseApplication {

    @NonNull
    @Override
    protected String getPrefLanguageCode() {
        return "";
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //
    }
}

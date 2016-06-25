package com.gsbelarus.gedemin.skeleton.app;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

import com.gsbelarus.gedemin.skeleton.base.BaseApplication;

//TODO AppCrashHandler
public class App extends BaseApplication {

    @NonNull
    @Override
    protected String getPrefLanguageCode() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return pref.getString("lang_preference", "ru");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //
    }
}

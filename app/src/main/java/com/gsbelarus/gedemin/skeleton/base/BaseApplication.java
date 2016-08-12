package com.gsbelarus.gedemin.skeleton.base;


import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;

import java.util.Locale;

abstract public class BaseApplication extends Application {

    private static Context appContext; //TODO dagger

    public static Context getContext() {
        return appContext;
    }

    /**
     * Returns the language code or the empty string if no language was set.
     */
    @NonNull
    protected abstract String getPrefLanguageCode();


    @Override
    public void onCreate() {
        super.onCreate();

        appContext = this;
        changeLocale();
    }

    public void changeLocale() {
        Configuration config = appContext.getResources().getConfiguration();
        String prefLanguageCode = getPrefLanguageCode();
        if (!prefLanguageCode.isEmpty() && !config.locale.getLanguage().equals(prefLanguageCode)) {
            Locale locale = new Locale(prefLanguageCode);
            Locale.setDefault(locale);
            config.locale = locale;
            appContext.getResources().updateConfiguration(config, appContext.getResources().getDisplayMetrics());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        changeLocale();
    }
}

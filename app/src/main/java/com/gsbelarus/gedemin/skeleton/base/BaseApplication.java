package com.gsbelarus.gedemin.skeleton.base;


import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.gsbelarus.gedemin.skeleton.core.util.AnalyticsTrackers;

import java.util.Locale;

abstract public class BaseApplication extends Application {

    public static final String TAG = BaseApplication.class
            .getSimpleName();

    private static BaseApplication mInstance;

    private static Context appContext; //TODO dagger

    /**
     * Returns the language code or the empty string if no language was set.
     */
    @NonNull
    protected abstract String getPrefLanguageCode();


    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        appContext = this;

        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

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

    public void trackScreenView(String screenName) {

        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    public void trackException(Exception e) {

        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */

    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    public static Context getContext() {
        return appContext;
    }

}

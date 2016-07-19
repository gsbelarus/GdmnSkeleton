package com.gsbelarus.gedemin.skeleton.app;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.BaseApplication;

//TODO AppCrashHandler
public class App extends BaseApplication {

    @NonNull
    @Override
    protected String getPrefLanguageCode() {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        TODO Settings
//        int lang = [pref.getInt(SettingsFragment.LANG_LIST, SettingsFragment.DEFAULT_LANG_PREF_ITEM)];
        int lang = 0;

        return getResources().getStringArray(R.array.language_list_values)[lang];
    }
}

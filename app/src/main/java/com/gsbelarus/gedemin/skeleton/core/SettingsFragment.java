package com.gsbelarus.gedemin.skeleton.core;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.core.util.ThemeUtils;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals(ThemeUtils.PREF_THEME)) {
            final ListPreference list = (ListPreference) preference;
            list.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    int index = list.findIndexOfValue(newValue.toString());
                    if (index != -1) {
                        getActivity().recreate();
                    }
                    return true;
                }
            });
        }
        return super.onPreferenceTreeClick(preference);

    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
}
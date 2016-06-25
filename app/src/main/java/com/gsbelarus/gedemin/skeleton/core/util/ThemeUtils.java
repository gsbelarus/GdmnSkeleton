package com.gsbelarus.gedemin.skeleton.core.util;

import android.content.Context;
import android.support.v7.preference.PreferenceManager;

import com.gsbelarus.gedemin.skeleton.R;

public class ThemeUtils {

    public static final String PREF_THEME = "theme_preference";
    public static final String LIGHT_THEME = "light";
    public static final String DARK_THEME = "dark";

    private boolean darkMode;
    private Context context;

    public ThemeUtils(Context context) {
        this.context = context;
        isChanged(false); // invalidate stored booleans
    }

    public boolean isDarkMode() {
        String dark = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_THEME, LIGHT_THEME);
        return dark.equals(DARK_THEME);
    }

    public boolean isChanged(boolean checkForChanged) {
        final boolean darkTheme = isDarkMode();

        boolean changed = false;
        if (checkForChanged) {
            changed = darkMode != darkTheme;
        }
        darkMode = darkTheme;
        return changed;
    }

    public int getCurrentTheme() {
        if (darkMode)
            return R.style.AppTheme_Dark;
        return R.style.AppTheme;
    }
}

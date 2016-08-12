package com.gsbelarus.gedemin.skeleton.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.BaseApplication;
import com.gsbelarus.gedemin.skeleton.base.BasicAccountHelper;

//TODO AppCrashHandler
public class App extends BaseApplication {

    public static Account getDemoSyncAccount(Context context) {
        String authority = context.getString(R.string.authority);
        Account account = new Account("Demo Account", context.getString(R.string.account_type));
        AccountManager.get(context).addAccountExplicitly(account, null, Bundle.EMPTY);
        ContentResolver.removePeriodicSync(account, authority, Bundle.EMPTY);

        ContentResolver.setSyncAutomatically(account, authority, false);
        ContentResolver.setIsSyncable(account, authority, 1);

        return account;
    }

    @NonNull
    @Override
    protected String getPrefLanguageCode() {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        TODO Settings
//        int lang = [pref.getInt(SettingsFragment.LANG_LIST, SettingsFragment.DEFAULT_LANG_PREF_ITEM)];
        int lang = 0;

        return getResources().getStringArray(R.array.language_list_values)[lang];
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (BasicAccountHelper.getSelectedAccount(getContext()) == null) {
            BasicAccountHelper.setSelectedAccount(getContext(), getDemoSyncAccount(getContext()));
        }
    }
}

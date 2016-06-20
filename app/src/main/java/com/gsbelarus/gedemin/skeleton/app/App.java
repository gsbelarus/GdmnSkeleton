package com.gsbelarus.gedemin.skeleton.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.PeriodicSync;
import android.support.annotation.NonNull;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.BaseApplication;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;

//TODO AppCrashHandler
public class App extends BaseApplication {

    public static final String SYNC_ACCOUNT_NAME = "Sync Account";
    public static final String SYNC_ACCOUNT_TYPE = "com.gsbelarus.gedemin.skeleton.sync";

    public static Account getSyncAccount(Context context) {
        String authority = context.getString(R.string.authority);
        Account account = new Account(SYNC_ACCOUNT_NAME, SYNC_ACCOUNT_TYPE);
        AccountManager accountManager = AccountManager.get(context);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            Logger.d(SYNC_ACCOUNT_NAME + " created");
            ContentResolver.setSyncAutomatically(account, authority, true);
            ContentResolver.setIsSyncable(account, authority, 1);
        }
        for (PeriodicSync periodicSync : ContentResolver.getPeriodicSyncs(account, authority)) {
            Logger.d(periodicSync.account.name, "period: " + periodicSync.period + " s");
        }

        return account;
    }

    @NonNull
    @Override
    protected String getPrefLanguageCode() {
        return "";
    }
}

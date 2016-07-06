package com.gsbelarus.gedemin.skeleton.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.PeriodicSync;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.core.data.CoreDatabaseManager;
import com.gsbelarus.gedemin.skeleton.core.data.CoreSyncService;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;

public class SyncService extends CoreSyncService {

    public static final String TAG_SERVER_URL = "server_url";

    public static final String SYNC_ACCOUNT_NAME = "Sync Account";

    public static Account getSyncAccount(Context context, Bundle bundle) {
        String authority = context.getString(R.string.authority);
        Account account = new Account(SYNC_ACCOUNT_NAME, context.getString(R.string.account_type));
        AccountManager accountManager = AccountManager.get(context);
        if (accountManager.addAccountExplicitly(account, null, bundle)) {
            Logger.d(SYNC_ACCOUNT_NAME + " created");
            ContentResolver.setSyncAutomatically(account, authority, true);
            ContentResolver.setIsSyncable(account, authority, 1);
        }
        for (PeriodicSync periodicSync : ContentResolver.getPeriodicSyncs(account, authority)) {
            Logger.d(periodicSync.account.name, "period: " + periodicSync.period + " s");
        }

        return account;
    }

    public static Account getDefaultSyncAccount(Context context) {
        Bundle bundle = new Bundle();
        bundle.putString(SyncService.TAG_SERVER_URL, "http://services.odata.org/V4/(S(5i2qvfszd0uktnpibrgfu2qs))/OData/OData.svc/");
        return getSyncAccount(context, bundle);
    }

    @Override
    protected String getUrl(Account account) {
        AccountManager accountManager = AccountManager.get(getApplicationContext());
        return accountManager.getUserData(account, TAG_SERVER_URL);
    }

    @NonNull
    @Override
    protected String getNamespace() {
        return "ODataDemo";
    }

    @Override
    protected void onHandleRow(String tableName, ContentValues contentValues) {
        super.onHandleRow(tableName, contentValues);
//        Logger.d(tableName, contentValues.keySet());
    }

    @Override
    protected Notification getStartSyncNotification() {
        return super.getStartSyncNotification();
    }

    @Override
    protected Notification getErrorSyncNotification(String errorMessage) {
        return super.getErrorSyncNotification(errorMessage);
    }

    @Override
    public void onCreateDatabase(CoreDatabaseManager coreDatabaseManager) {
        super.onCreateDatabase(coreDatabaseManager);
    }

    @Override
    public void onUpgradeDatabase(CoreDatabaseManager coreDatabaseManager, int oldVersion, int newVersion) {
        super.onUpgradeDatabase(coreDatabaseManager, oldVersion, newVersion);
    }
}

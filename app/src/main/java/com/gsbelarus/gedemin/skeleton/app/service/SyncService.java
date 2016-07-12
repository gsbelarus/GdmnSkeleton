package com.gsbelarus.gedemin.skeleton.app.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.PeriodicSync;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.core.data.CoreDatabaseManager;
import com.gsbelarus.gedemin.skeleton.core.data.CoreSyncService;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;

public class SyncService extends CoreSyncService {

    public static final String TAG_SERVER_URL = "server_url";

    public static Account getSyncAccount(Context context, String name, Bundle bundle) {
        String authority = context.getString(R.string.authority);
        Account account = new Account(name, context.getString(R.string.account_type));
        AccountManager.get(context).addAccountExplicitly(account, null, bundle);
        ContentResolver.removePeriodicSync(account, authority, Bundle.EMPTY);
        ContentResolver.setSyncAutomatically(account, authority, true);
        ContentResolver.setIsSyncable(account, authority, 1);

        for (PeriodicSync periodicSync : ContentResolver.getPeriodicSyncs(account, authority)) {
            Logger.d(periodicSync.account.name, "period: " + periodicSync.period + " s");
        }

        return account;
    }

    public static Account getDefaultSyncAccount(Context context) {
        Bundle bundle = new Bundle();
        bundle.putString(SyncService.TAG_SERVER_URL, "http://services.odata.org/V4/(S(5i2qvfszd0uktnpibrgfu2qs))/OData/OData.svc/");
        Account account = getSyncAccount(context, "Default Sync Account", bundle);
        ContentResolver.addPeriodicSync(account, context.getString(R.string.authority), getTaskBundle(TypeTask.BACKGROUND), 86400);
        return account;
    }

    public static Account getDemoSyncAccount(Context context) {
        return getSyncAccount(context, "Demo Account", Bundle.EMPTY);
    }

    @Nullable
    @Override
    protected String getUrl(Account account, Bundle extras) {
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
    public void onCreateDemoDatabase(CoreDatabaseManager coreDatabaseManager) {
        super.onCreateDemoDatabase(coreDatabaseManager);
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

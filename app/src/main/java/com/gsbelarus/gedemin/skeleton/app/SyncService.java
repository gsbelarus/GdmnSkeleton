package com.gsbelarus.gedemin.skeleton.app;

import android.app.Notification;
import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.gsbelarus.gedemin.skeleton.core.CoreDatabaseManager;
import com.gsbelarus.gedemin.skeleton.core.CoreSyncService;

public class SyncService extends CoreSyncService {

    @NonNull
    @Override
    protected String getUrl() {
        String url = "http://services.odata.org/V4/(S(5i2qvfszd0uktnpibrgfu2qs))/OData/OData.svc/";
//        String url = "http://gs.selfip.biz/OData/";
        return url;
    }

    @NonNull
    @Override
    protected String getNamespace() {
        return "ODataDemo";
    }

    @Override
    protected void onHandleRow(String tableName, ContentValues contentValues) {
        super.onHandleRow(tableName, contentValues);
//        LogUtil.d(tableName, contentValues.keySet());
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
    protected Notification getProgressSyncNotification(int max, int progress) {
        return super.getProgressSyncNotification(max, progress);
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

package com.gsbelarus.gedemin.skeleton.app;

import android.app.Notification;
import android.content.ContentValues;

import com.gsbelarus.gedemin.skeleton.core.CoreSyncService;
import com.gsbelarus.gedemin.skeleton.core.LogUtil;

public class SyncService extends CoreSyncService {

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
}

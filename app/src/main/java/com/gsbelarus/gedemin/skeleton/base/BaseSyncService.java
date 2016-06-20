package com.gsbelarus.gedemin.skeleton.base;

import android.accounts.Account;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;

import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.net.ssl.SSLHandshakeException;

public abstract class BaseSyncService extends Service {

    private static final int ID_SYNC_NOTIFICATION = 1001;
    private static final int ID_ERROR_NOTIFICATION = 1002;

    private CoreSyncAdapter syncAdapter;
    private NotificationManager notificationManager;

    public static void startSync(Context context, Account account, TypeTask typeTask) {
        startSync(context, account, typeTask, new Bundle());
    }

    public static void startSync(Context context, Account account, TypeTask typeTask, Bundle bundle) {
        bundle.putString(TypeTask.class.getSimpleName(), typeTask.name());
        ContentResolver.requestSync(account, context.getString(R.string.authority), bundle);
    }

    public static void cancelSync(Context context, Account account) {
        ContentResolver.cancelSync(account, context.getString(R.string.authority));
    }

    public static void addPeriodicSync(Context context, Account account, long pollFrequency) {
        addPeriodicSync(context, account, new Bundle(), pollFrequency);
    }

    public static void addPeriodicSync(Context context, Account account, Bundle bundle, long pollFrequency) {
        bundle.putString(TypeTask.class.getSimpleName(), TypeTask.BACKGROUND.name());
        ContentResolver.addPeriodicSync(account, context.getString(R.string.authority), bundle, pollFrequency);
    }

    public static void removePeriodicSync(Context context, Account account) {
        removePeriodicSync(context, account, new Bundle());
    }

    public static void removePeriodicSync(Context context, Account account, Bundle bundle) {
        bundle.putString(TypeTask.class.getSimpleName(), TypeTask.BACKGROUND.name());
        ContentResolver.removePeriodicSync(account, context.getString(R.string.authority), bundle);
    }

    protected abstract void onPerformSync(Account account, Bundle extras, ContentProviderClient provider, SyncResult syncResult) throws Exception;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(ID_SYNC_NOTIFICATION);
        syncAdapter = new CoreSyncAdapter(getApplicationContext(), true);
        Logger.d();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        notificationManager.cancel(ID_SYNC_NOTIFICATION);
        Logger.d();
    }

    protected Notification getStartSyncNotification() {
        return new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("GDMN")
                .setContentText("Synchronization")
                .setSmallIcon(android.R.drawable.ic_popup_sync)
                .setWhen(System.currentTimeMillis())
                .setProgress(0, 0, true)
                .setStyle((new NotificationCompat.BigTextStyle()
                        .setBigContentTitle("GDMN")
                        .bigText("Synchronization")
                ))
                .build();
    }

    protected Notification getErrorSyncNotification(String errorMessage) {
        return new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("GDMN")
                .setContentText(errorMessage)
                .setSmallIcon(R.drawable.ic_sync_problem_black_24dp)
                .setWhen(System.currentTimeMillis())
                .setStyle((new NotificationCompat.BigTextStyle()
                        .setBigContentTitle("GDMN")
                        .bigText("Error: " + errorMessage)
                ))
                .build();
    }

    protected TypeTask getTypeTask(Bundle bundle) {
        String taskName = bundle.getString(TypeTask.class.getSimpleName());
        if (taskName == null) return TypeTask.BACKGROUND;
        return TypeTask.valueOf(taskName);
    }

    public enum TypeTask {FOREGROUND, BACKGROUND}

    private class CoreSyncAdapter extends AbstractThreadedSyncAdapter {

        public CoreSyncAdapter(Context context, boolean autoInitialize) {
            super(context, autoInitialize);
        }

        @Override
        public void onPerformSync(Account account, final Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            if (getTypeTask(extras) == TypeTask.FOREGROUND) {
                startForeground(ID_SYNC_NOTIFICATION, getStartSyncNotification());
            }

            String error = null;
            try {
                BaseSyncService.this.onPerformSync(account, extras, provider, syncResult);
            } catch (SSLHandshakeException e) {
                Logger.e(e);
                error = getString(R.string.sync_certificate_error);
            } catch (SocketTimeoutException e) {
                Logger.e(e);
                error = getString(R.string.sync_error_timeout);
                if (getTypeTask(extras) == TypeTask.BACKGROUND) {
                    syncResult.stats.numIoExceptions++;
                }
            } catch (IOException e) {
                Logger.e(e);
                error = getString(R.string.sync_error_connection);
                if (getTypeTask(extras) == TypeTask.BACKGROUND) {
                    syncResult.stats.numIoExceptions++;
                }
            } catch (Exception e) {
                Logger.e(e);
                error = getString(R.string.sync_unknown) + ": " + e.getMessage();
            }

            if (getTypeTask(extras) == TypeTask.FOREGROUND) {
                stopForeground(true);
                if (error != null) {
                    notificationManager.notify(ID_ERROR_NOTIFICATION, getErrorSyncNotification(error));
                }
            }
        }
    }
}

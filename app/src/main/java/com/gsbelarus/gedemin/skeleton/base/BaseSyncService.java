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
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

public abstract class BaseSyncService extends Service {

    private static final int ID_SYNC_NOTIFICATION = 1001;
    private static final int ID_ERROR_NOTIFICATION = 1002;

    private BaseSyncAdapter syncAdapter;
    private NotificationManager notificationManager;

    public static Bundle getTaskBundle(TypeTask typeTask) {
        return getTaskBundle(typeTask, new Bundle());
    }

    public static Bundle getTaskBundle(TypeTask typeTask, Bundle bundle) {
        bundle.putString(TypeTask.class.getSimpleName(), typeTask.name());
        switch (typeTask) {
            case BACKGROUND:
                break;
            case FOREGROUND:
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, true);
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                break;
        }
        return bundle;
    }

    protected abstract void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) throws IOException;

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
        syncAdapter = new BaseSyncAdapter(getApplicationContext(), true);
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
                .setSmallIcon(R.mipmap.ic_sync_alert)
                .setWhen(System.currentTimeMillis())
                .setStyle((new NotificationCompat.BigTextStyle()
                        .setBigContentTitle("GDMN")
                        .bigText(errorMessage)
                ))
                .build();
    }

    protected TypeTask getTypeTask(Bundle bundle) {
        String taskName = bundle.getString(TypeTask.class.getSimpleName());
        if (taskName == null) return TypeTask.BACKGROUND;
        return TypeTask.valueOf(taskName);
    }

    public enum TypeTask {FOREGROUND, BACKGROUND}

    private class BaseSyncAdapter extends AbstractThreadedSyncAdapter {

        public BaseSyncAdapter(Context context, boolean autoInitialize) {
            super(context, autoInitialize);
        }

        @Override
        public void onPerformSync(Account account, final Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            if (getTypeTask(extras) == TypeTask.FOREGROUND) {
                startForeground(ID_SYNC_NOTIFICATION, getStartSyncNotification());
            }

            String error = null;
            try {
                BaseSyncService.this.onPerformSync(account, extras, authority, provider, syncResult);
            } catch (SSLHandshakeException e) {
                Logger.e(e);
                error = getString(R.string.sync_error_certificate);
            } catch (SocketTimeoutException e) {
                Logger.e(e);
                error = getString(R.string.sync_error_timeout);
                syncResult.stats.numIoExceptions++;
            } catch (UnknownHostException e) {
                Logger.e(e);
                error = getString(R.string.sync_error_unknown_host);
            } catch (IOException e) {
                Logger.e(e);
                error = getString(R.string.sync_error_connection);
                syncResult.stats.numIoExceptions++;
            } catch (RuntimeException e) {
                Logger.e(e);
                error = e.getMessage();
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

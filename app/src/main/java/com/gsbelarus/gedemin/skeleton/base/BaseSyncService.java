package com.gsbelarus.gedemin.skeleton.base;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.core.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSyncService extends IntentService {

    private static final int ID_SYNC_NOTIFICATION = 1001;
    private static final int ID_ERROR_NOTIFICATION = 1002;

    private List<OnSyncListener> onSyncListeners = new ArrayList<>();
    private Handler handler;
    private NotificationManager notificationManager;

    public BaseSyncService() {
        super(BaseSyncService.class.getSimpleName());
    }

    public static Intent startSync(Context context, Class<? extends BaseSyncService> service, TypeTask typeTask) {
        Intent intent = new Intent(context, service);
        intent.setAction(typeTask.name());
        context.startService(intent);
        return intent;
    }

    public static boolean bindService(Context context, Class<? extends BaseSyncService> service, ServiceConnection serviceConnection) {
        Intent intent = new Intent(context, service);
        return context.bindService(intent, serviceConnection, BIND_NOT_FOREGROUND);
    }

    protected abstract void handleIntentBackground(Intent intent) throws Exception;

    @Override
    public final IBinder onBind(Intent intent) {
        return new SyncBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setIntentRedelivery(true);
        handler = new Handler();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(ID_SYNC_NOTIFICATION);
        LogUtil.d();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        notificationManager.cancel(ID_SYNC_NOTIFICATION);
        LogUtil.d();
    }

    @Override
    protected final void onHandleIntent(final Intent intent) {
        if (intent != null) {
            if (getTypeTask(intent) == TypeTask.FOREGROUND) {
                startForeground(ID_SYNC_NOTIFICATION, getStartSyncNotification());
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyStartSync();
                }
            });

            String error = null;
            try {
                handleIntentBackground(intent);
            } catch (Exception e) {
                LogUtil.e(e);
                error = e.getMessage();
            }

            stopForeground(true);
            final String errorMessage = error;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!notifyFinishSync(errorMessage) && TypeTask.valueOf(intent.getAction()) == TypeTask.FOREGROUND) {
                        if (errorMessage != null) {
                            notificationManager.notify(ID_ERROR_NOTIFICATION, getErrorSyncNotification(errorMessage));
                        }
                    }
                }
            });
        }
    }

    protected Notification getStartSyncNotification() {
        return new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("GDMN")
                .setContentText("Synchronization")
                .setSmallIcon(android.R.drawable.ic_popup_sync)
                .setWhen(System.currentTimeMillis())
                .setStyle((new NotificationCompat.BigTextStyle()
                        .setBigContentTitle("GDMN")
                        .bigText("Synchronization")
                ))
                .build();
    }

    protected Notification getErrorSyncNotification(String errorMessage) {
        return new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("GDMN")
                .setContentText("Error: " + errorMessage)
                .setSmallIcon(R.drawable.ic_sync_problem_black_24dp)
                .setWhen(System.currentTimeMillis())
                .setStyle((new NotificationCompat.BigTextStyle()
                        .setBigContentTitle("GDMN")
                        .bigText("Error: " + errorMessage)
                ))
                .build();
    }

    protected Notification getProgressSyncNotification(int max, int progress) {
        return new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("GDMN")
                .setContentText("Synchronization")
                .setSmallIcon(android.R.drawable.ic_popup_sync)
                .setProgress(max, progress, false)
                .setWhen(System.currentTimeMillis())
                .setStyle((new NotificationCompat.BigTextStyle()
                        .setBigContentTitle("GDMN")
                        .bigText("Synchronization")
                ))
                .build();
    }

    private void notifyStartSync() {
        for (OnSyncListener onSyncListener : onSyncListeners) {
            onSyncListener.onStartSync();
        }
    }

    private boolean notifyFinishSync(@Nullable String error) {
        boolean handled = false;
        for (OnSyncListener onSyncListener : onSyncListeners) {
            if (onSyncListener.onFinishSync(error)) {
                handled = true;
            }
        }
        return handled;
    }

    protected TypeTask getTypeTask(Intent intent) {
        return TypeTask.valueOf(intent.getAction());
    }

    protected void publishProcess(final int max, final int progress) {
        notificationManager.notify(ID_SYNC_NOTIFICATION, getProgressSyncNotification(max, progress));
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (OnSyncListener onSyncListener : onSyncListeners) {
                    try {
                        onSyncListener.onProcessChange(max, progress);
                    } catch (Exception e) {
                        LogUtil.d(e);
                    }
                }
            }
        });
    }

    public enum TypeTask {
        FOREGROUND, BACKGROUND
    }

    public static abstract class OnSyncListener {
        public void onStartSync() {
        }

        public void onProcessChange(int max, int progress) {
        }

        public boolean onFinishSync(@Nullable String error) {
            return false;
        }
    }

    public class SyncBinder extends Binder {

        public void addOnSyncListener(OnSyncListener onSyncListener) {
            if (!onSyncListeners.contains(onSyncListener))
                onSyncListeners.add(onSyncListener);
        }

        public void removeOnSyncListener(OnSyncListener onSyncListener) {
            onSyncListeners.remove(onSyncListener);
        }
    }
}
package com.gsbelarus.gedemin.skeleton.base;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncStatusObserver;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gsbelarus.gedemin.skeleton.core.util.Logger;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class BasicSyncStatusNotifier {

    private Handler handler;
    private String authority;

    private Map<Account, Params> syncAccounts = new LinkedHashMap<>();

    public BasicSyncStatusNotifier(String authority) {
        this.authority = authority;
        handler = new Handler();
    }

    public void addSyncStatusListenerForAll(@NonNull Context context, @NonNull final Callback callback) {
        for (Account account : AccountManager.get(context).getAccounts()) {
            addSyncStatusListener(account, callback);
        }
    }

    public void addSyncStatusListener(@NonNull final Account account, @NonNull final Callback callback) {
        if (syncAccounts.containsKey(account)) {
            throw new IllegalArgumentException("account already has SyncStatusCallback");
        }
        final Params params = new Params();
        syncAccounts.put(account, params);
        params.syncActive = isSyncActive(account) && !isSyncPending(account);
        params.syncObserverHandle = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE, new SyncStatusObserver() {
            @Override
            public void onStatusChanged(int which) {
                final boolean isSyncActive = isSyncActive(account) && !isSyncPending(account);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (syncAccounts.containsKey(account)) {
                            if (isSyncActive) {
                                if (!params.syncActive) {
                                    params.syncActive = true;
                                    callback.onStartSync(account);
                                }
                            } else {
                                if (params.syncActive) {
                                    params.syncActive = false;
                                    callback.onFinishSync(account);
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    public void removeSyncStatusListener(@NonNull Account account) {
        if (!syncAccounts.containsKey(account)) {
            throw new IllegalArgumentException("account has no SyncStatusCallback");
        }
        ContentResolver.removeStatusChangeListener(syncAccounts.get(account).syncObserverHandle);
        syncAccounts.remove(account);
    }

    public void clearSyncStatusListeners() {
        Iterator<Map.Entry<Account, Params>> iterator = syncAccounts.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Account, Params> entry = iterator.next();
            ContentResolver.removeStatusChangeListener(entry.getValue().syncObserverHandle);
            iterator.remove();
        }
    }

    public boolean isSyncPending(@NonNull Account account) {
        return ContentResolver.isSyncPending(account, authority);
    }

    public boolean isSyncActive(@NonNull Account account) {
        return ContentResolver.isSyncActive(account, authority);
    }

    public interface Callback {
        void onStartSync(Account account);

        void onFinishSync(Account account);
    }

    private class Params {
        Object syncObserverHandle;
        boolean syncActive;
    }
}

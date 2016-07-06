package com.gsbelarus.gedemin.skeleton.base;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncStatusObserver;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.gsbelarus.gedemin.skeleton.R;

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

    public void addSyncStatusListener(@NonNull final Account account, @NonNull final OnSyncStatusListener onSyncStatusListener) {
        if (syncAccounts.containsKey(account)) {
            throw new IllegalArgumentException("account already has listener");
        }
        final Params params = new Params();
        syncAccounts.put(account, params);
        params.syncActive = isSyncActive(account);
        params.syncObserverHandle = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE, new SyncStatusObserver() {
            @Override
            public void onStatusChanged(int which) {
                final boolean isSyncActive = isSyncActive(account);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (syncAccounts.containsKey(account)) {
                            if (isSyncActive) {
                                params.syncActive = true;
                                onSyncStatusListener.onStart(account);
                            } else {
                                if (params.syncActive) {
                                    params.syncActive = false;
                                    onSyncStatusListener.onFinish(account);
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    public void removeSyncStatusListener(Account account) {
        if (!syncAccounts.containsKey(account)) {
            throw new IllegalArgumentException("account has no listener");
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

    public boolean isSyncActive(Account account) {
        return ContentResolver.isSyncActive(account, authority) &&
                !ContentResolver.isSyncPending(account, authority);
    }

    public interface OnSyncStatusListener {
        void onStart(Account account);

        void onFinish(Account account);
    }

    private class Params {
        Object syncObserverHandle;
        boolean syncActive;
    }
}
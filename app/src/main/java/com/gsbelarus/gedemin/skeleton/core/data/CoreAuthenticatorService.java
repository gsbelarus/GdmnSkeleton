package com.gsbelarus.gedemin.skeleton.core.data;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.gsbelarus.gedemin.skeleton.base.BasicAccountHelper;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;
import com.gsbelarus.gedemin.skeleton.core.view.CoreAccountAuthenticatorActivity;

public abstract class CoreAuthenticatorService extends Service {

    private Authenticator authenticator;
    private BasicAccountHelper.LifeCycleDelegate lifeCycleDelegate;

    protected abstract Class<? extends CoreAccountAuthenticatorActivity> getAuthActivity();

    protected void onDeleteAccount(Account account) {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (authenticator == null) {
            authenticator = new Authenticator(getApplicationContext());
        }
        BasicAccountHelper basicAccountHelper = new BasicAccountHelper(getApplicationContext());
        lifeCycleDelegate = basicAccountHelper.setOnDeletedListener(new BasicAccountHelper.OnDeletedListener() {
            @Override
            public void onDeleted(Account account) {
                onDeleteAccount(account);
                CoreDatabaseManager coreDatabaseManager = CoreDatabaseManager.getInstance(getApplicationContext(), account);
                coreDatabaseManager.open();
                coreDatabaseManager.deleteDatabase();
                coreDatabaseManager.close();
            }
        });
        lifeCycleDelegate.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        lifeCycleDelegate.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }

    public class Authenticator extends AbstractAccountAuthenticator {

        public Authenticator(Context context) {
            super(context);
        }

        @Override
        public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType,
                                 String[] requiredFeatures, Bundle options)
                throws NetworkErrorException {
            Logger.d();
            final Intent intent = new Intent(getApplicationContext(), getAuthActivity());
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
            final Bundle bundle = new Bundle();
            if (options != null) {
                bundle.putAll(options);
            }
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
            return bundle;
        }

        @Override
        public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options)
                throws NetworkErrorException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType,
                                   Bundle options) throws NetworkErrorException {
            final Bundle result = new Bundle();
            final AccountManager am = AccountManager.get(getApplicationContext());
            String authToken = am.peekAuthToken(account, authTokenType);
            if (!TextUtils.isEmpty(authToken)) {
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            } else {
                addAccount(response, account.type, null, null, options);
            }
            return result;
        }

        @Override
        public String getAuthTokenLabel(String authTokenType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType,
                                        Bundle options) throws NetworkErrorException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features)
                throws NetworkErrorException {
            throw new UnsupportedOperationException();
        }
    }
}

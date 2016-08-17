package com.gsbelarus.gedemin.skeleton.app.service;

import android.accounts.Account;
import android.support.annotation.NonNull;

import com.gsbelarus.gedemin.skeleton.app.view.activity.LoginActivity;
import com.gsbelarus.gedemin.skeleton.core.data.CoreAuthenticatorService;
import com.gsbelarus.gedemin.skeleton.core.data.CoreDatabaseManager;
import com.gsbelarus.gedemin.skeleton.core.view.CoreAccountAuthenticatorActivity;

public class AuthService extends CoreAuthenticatorService {

    @Override
    protected Class<? extends CoreAccountAuthenticatorActivity> getAuthActivity() {
        return LoginActivity.class;
    }

    @Override
    protected void onDeleteAccount(CoreDatabaseManager coreDatabaseManager, @NonNull Account account) {
        super.onDeleteAccount(coreDatabaseManager, account);
    }
}

package com.gsbelarus.gedemin.skeleton.app.service;

import android.accounts.Account;

import com.gsbelarus.gedemin.skeleton.app.view.activity.LoginActivity;
import com.gsbelarus.gedemin.skeleton.core.data.CoreAuthenticatorService;
import com.gsbelarus.gedemin.skeleton.core.view.CoreAccountAuthenticatorActivity;

public class AuthService extends CoreAuthenticatorService {

    @Override
    protected Class<? extends CoreAccountAuthenticatorActivity> getAuthActivity() {
        return LoginActivity.class;
    }

    @Override
    protected void onDeleteAccount(Account account) {
        super.onDeleteAccount(account);
    }
}

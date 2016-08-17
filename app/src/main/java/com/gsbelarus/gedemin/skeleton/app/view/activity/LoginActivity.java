package com.gsbelarus.gedemin.skeleton.app.view.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.app.service.SyncService;
import com.gsbelarus.gedemin.skeleton.base.BaseSyncService;
import com.gsbelarus.gedemin.skeleton.core.util.CoreNetworkInfo;
import com.gsbelarus.gedemin.skeleton.core.util.CoreUtils;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;
import com.gsbelarus.gedemin.skeleton.core.view.CoreAccountAuthenticatorActivity;

public class LoginActivity extends CoreAccountAuthenticatorActivity {

    public static final String TAG_SERVER_URL = "server_url";
    private static final String URL_ADDRESS = "http://services.odata.org/V4/(S(5i2qvfszd0uktnpibrgfu2qs))/OData/OData.svc/";

    private AlertDialog progressDialog;

    @Override
    protected int getLayoutResource() {
        return R.layout.app_activity_login;
    }

    @Nullable
    @Override
    protected Integer getToolbarIdResource() {
        return null;
    }

    @Override
    protected ActivityType getActivityType() {
        return ActivityType.HIGH_LEVEL;
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        final EditText loginEt = (EditText) findViewById(R.id.login_et);
        final EditText passwordEt = (EditText) findViewById(R.id.password_et);

        Button loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(URL_ADDRESS, loginEt.getText().toString(), passwordEt.getText().toString(), null);
            }
        });
    }

    @Override
    protected void onSignInProgress() {
        Logger.d();
        showProgressDialog();
    }

    @Override
    protected void onSignInSuccess(Account account) {
        Logger.d(account);

        cancelProgressDialog();

        String authority = getString(R.string.authority);
        ContentResolver.removePeriodicSync(account, authority, Bundle.EMPTY);

        ContentResolver.setSyncAutomatically(account, authority, true);
        ContentResolver.setIsSyncable(account, authority, 1);

        ContentResolver.addPeriodicSync(account, context.getString(R.string.authority),
                SyncService.getTaskBundle(BaseSyncService.TypeTask.BACKGROUND), 86400);

        AccountManager.get(context).setUserData(account, TAG_SERVER_URL, URL_ADDRESS);
    }

    @Override
    protected void onSignInError(Exception error) {
        Logger.d(error);

        cancelProgressDialog();

        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(error.getMessage())
                .setPositiveButton("Скрыть", null)
                .show();
    }

    private void showProgressDialog() {
        progressDialog = new AlertDialog.Builder(this)
                .setMessage("Подождите...")
                .setCancelable(false)
                .show();
    }

    private void cancelProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }
}

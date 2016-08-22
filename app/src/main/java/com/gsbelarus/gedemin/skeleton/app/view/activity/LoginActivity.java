package com.gsbelarus.gedemin.skeleton.app.view.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.SignInButton;
import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.app.service.SyncService;
import com.gsbelarus.gedemin.skeleton.base.BaseSyncService;
import com.gsbelarus.gedemin.skeleton.core.util.CoreNetworkInfo;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;
import com.gsbelarus.gedemin.skeleton.core.view.CoreAccountAuthenticatorActivity;

public class LoginActivity extends CoreAccountAuthenticatorActivity {

    public static final String TAG_SERVER_URL = "server_url";
    private static final String URL_ADDRESS = "http://services.odata.org/V4/(S(5i2qvfszd0uktnpibrgfu2qs))/OData/OData.svc/";
    private static final String TAG_ACCOUNT = "account";

    private AlertDialog dialog;
    private BottomSheetDialog bottomSheetDialog;

    private Account account;

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
    protected String getServersClientId() {
        return getString(R.string.server_client_id);
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        final EditText loginEt = (EditText) findViewById(R.id.login_et);
        final EditText passwordEt = (EditText) findViewById(R.id.password_et);
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        };
        loginEt.setOnFocusChangeListener(onFocusChangeListener);
        passwordEt.setOnFocusChangeListener(onFocusChangeListener);

        Button loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentFocus() != null) {
                    getCurrentFocus().clearFocus();
                }
                CoreNetworkInfo.runIfNetworkAvailable(findViewById(R.id.content), new Runnable() {
                    @Override
                    public void run() {
                        String login = loginEt.getText().toString();
                        String password = passwordEt.getText().toString();
                        gdmnSignIn(URL_ADDRESS, login, password, null);
                    }
                });
            }
        });
        SignInButton signInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gdmnSignInWithGoogle(URL_ADDRESS);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cancelBottomSheetDialog();
        cancelProgressDialog();
    }

    @Override
    protected void onGdmnSignInSuccess(Account account) {
        this.account = account;
        Logger.d(account);

        setupAccount(account);

        showBottomSheetDialog();
    }

    @Override
    protected void onGdmnGoogleSignInSuccess(Account account) {
        Logger.d(account);
        setupAccount(account);
        finish();
    }

    @Override
    protected void onAssociatedSuccess(Account account) {
        Logger.d(account);
        cancelBottomSheetDialog();
        finish();
    }

    @Override
    protected void onTaskStart() {
        Logger.d();
        showProgressDialog();
    }

    @Override
    protected void onTaskFinish(@Nullable Exception e) {
        Logger.d();
        cancelProgressDialog();
        if (e != null) {
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Error")
                    .setMessage(e.getMessage())
                    .setPositiveButton("Скрыть", null)
                    .show();
        }
    }

    private void setupAccount(Account account) {
        String authority = getString(R.string.authority);
        ContentResolver.removePeriodicSync(account, authority, Bundle.EMPTY);

        ContentResolver.setSyncAutomatically(account, authority, true);
        ContentResolver.setIsSyncable(account, authority, 1);

        ContentResolver.addPeriodicSync(account, context.getString(R.string.authority),
                SyncService.getTaskBundle(BaseSyncService.TypeTask.BACKGROUND), 86400);

        AccountManager.get(context).setUserData(account, TAG_SERVER_URL, URL_ADDRESS);
    }

    private void showBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.app_login_bottom_sheet);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!isChangingConfigurations()) finish();
            }
        });
        bottomSheetDialog.show();

        Button okButton = (Button) bottomSheetDialog.findViewById(R.id.ok_bt);
        if (okButton != null) {
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CoreNetworkInfo.runIfNetworkAvailable(findViewById(R.id.content), new Runnable() {
                        @Override
                        public void run() {
                            associateWithGoogle(account, URL_ADDRESS, null);
                        }
                    });
                }
            });
        }
        Button cancelButton = (Button) bottomSheetDialog.findViewById(R.id.cancel_bt);
        if (cancelButton != null) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.cancel();
                }
            });
        }
    }

    private void cancelBottomSheetDialog() {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.cancel();
        }
    }

    private void showProgressDialog() {
        dialog = new AlertDialog.Builder(this)
                .setMessage("Подождите...")
                .setCancelable(false)
                .show();
    }

    private void cancelProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(TAG_ACCOUNT, account);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            account = savedInstanceState.getParcelable(TAG_ACCOUNT);
            if (account != null) {
                showBottomSheetDialog();
            }
        }
        if (isTaskProcess()) {
            showProgressDialog();
        }
    }
}

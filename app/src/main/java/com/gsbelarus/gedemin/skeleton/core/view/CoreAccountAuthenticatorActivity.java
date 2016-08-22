package com.gsbelarus.gedemin.skeleton.core.view;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.AccountsException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.gsbelarus.gedemin.skeleton.base.view.BaseActivity;
import com.gsbelarus.gedemin.skeleton.core.data.task.AssociateWithGoogleTask;
import com.gsbelarus.gedemin.skeleton.core.data.task.BackgroundTask;
import com.gsbelarus.gedemin.skeleton.core.data.task.BackgroundTaskListener;
import com.gsbelarus.gedemin.skeleton.core.data.task.BackgroundTaskResult;
import com.gsbelarus.gedemin.skeleton.core.data.task.GdmnGoogleSignInTask;
import com.gsbelarus.gedemin.skeleton.core.data.task.GdmnSignInTask;
import com.gsbelarus.gedemin.skeleton.core.data.task.entity.AssosiateWithGoogleParams;
import com.gsbelarus.gedemin.skeleton.core.data.task.entity.GdmnGoogleSignInParams;
import com.gsbelarus.gedemin.skeleton.core.data.task.entity.GdmnGoogleSignInResult;
import com.gsbelarus.gedemin.skeleton.core.data.task.entity.GdmnSignInParams;
import com.gsbelarus.gedemin.skeleton.core.util.AuthSignInHelper;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class CoreAccountAuthenticatorActivity extends BaseActivity {

    protected static final int REQUEST_CODE_ASSOCIATE_WITH_GOOGLE = 123;
    protected static final int REQUEST_CODE_GDMN_SIGN_IN = 321;

    private static final String TAG_ACCOUNT = "associate_account";
    private static final String TAG_URL = "url";
    private static final String TAG_PARAMS = "params";

    private AuthSignInHelper authSignInHelper;
    private BackgroundTask backgroundTask;

    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private Bundle mResultBundle = null;

    private Account account;
    private String url;
    private LinkedHashMap<String, String> params;

    private BackgroundTaskListener<AssosiateWithGoogleParams, Void, Boolean> associateWithGoogleListener = new BackgroundTaskListener<AssosiateWithGoogleParams, Void, Boolean>() {
        @Override
        public void onPreExecute() {
            onTaskStart();
        }

        @Override
        public void onProgressUpdate(Void... values) {

        }

        @Override
        public void onPostExecute(BackgroundTaskResult<AssosiateWithGoogleParams, Boolean> backgroundTaskResult) {
            if (backgroundTaskResult.getResult() != null && backgroundTaskResult.getResult()) {
                onAssociatedSuccess(account);
            }
            onTaskFinish(backgroundTaskResult.getException());
            backgroundTask = null;
        }
    };

    private BackgroundTaskListener<GdmnGoogleSignInParams, Void, GdmnGoogleSignInResult> gdmnGoogleSignInListener = new BackgroundTaskListener<GdmnGoogleSignInParams, Void, GdmnGoogleSignInResult>() {
        @Override
        public void onPreExecute() {
            onTaskStart();
        }

        @Override
        public void onProgressUpdate(Void... values) {

        }

        @Override
        public void onPostExecute(BackgroundTaskResult<GdmnGoogleSignInParams, GdmnGoogleSignInResult> backgroundTaskResult) {
            if (backgroundTaskResult.getException() == null) {
                try {
                    onGdmnGoogleSignInSuccess(onCreateAccount(
                            backgroundTaskResult.getResult().getLogin(),
                            backgroundTaskResult.getResult().getPassword(),
                            backgroundTaskResult.getResult().getGdmnToken(),
                            backgroundTaskResult.getResult().getParams()));
                } catch (AccountsException e) {
                    backgroundTaskResult.setResult(null);
                    backgroundTaskResult.setException(e);
                }
            }
            onTaskFinish(backgroundTaskResult.getException());
            backgroundTask = null;
        }
    };

    private BackgroundTaskListener<GdmnSignInParams, Void, String> gdmnSignInListener = new BackgroundTaskListener<GdmnSignInParams, Void, String>() {
        @Override
        public void onPreExecute() {
            onTaskStart();
        }

        @Override
        public void onProgressUpdate(Void... values) {

        }

        @Override
        public void onPostExecute(BackgroundTaskResult<GdmnSignInParams, String> backgroundTaskResult) {
            if (backgroundTaskResult.getException() == null) {
                try {
                    GdmnSignInParams gdmnSignInParams = backgroundTaskResult.getParams()[0];
                    onGdmnSignInSuccess(onCreateAccount(
                            gdmnSignInParams.getLogin(),
                            gdmnSignInParams.getPassword(),
                            backgroundTaskResult.getResult(),
                            gdmnSignInParams.getParams()));
                } catch (AccountsException e) {
                    backgroundTaskResult.setResult(null);
                    backgroundTaskResult.setException(new AccountsException("Account already exists"));
                }
            }
            onTaskFinish(backgroundTaskResult.getException());
            backgroundTask = null;
        }
    };

    protected abstract String getServersClientId();

    protected abstract void onTaskStart();

    protected abstract void onTaskFinish(@Nullable Exception e);

    protected abstract void onGdmnSignInSuccess(Account account);

    protected abstract void onGdmnGoogleSignInSuccess(Account account);

    protected abstract void onAssociatedSuccess(Account account);

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        if (icicle != null) {
            url = icicle.getString(TAG_URL);
            account = icicle.getParcelable(TAG_ACCOUNT);
            params = (LinkedHashMap<String, String>) icicle.getSerializable(TAG_PARAMS);
        }

        authSignInHelper = new AuthSignInHelper();
        authSignInHelper.createAPIClient(this, getServersClientId());

        mAccountAuthenticatorResponse = getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }

        backgroundTask = (BackgroundTask) getLastCustomNonConfigurationInstance();
        if (backgroundTask instanceof GdmnSignInTask) {
            ((GdmnSignInTask) backgroundTask).setTaskListener(gdmnSignInListener);
        } else if (backgroundTask instanceof AssociateWithGoogleTask) {
            ((AssociateWithGoogleTask) backgroundTask).setTaskListener(associateWithGoogleListener);
        } else if (backgroundTask instanceof GdmnGoogleSignInTask) {
            ((GdmnGoogleSignInTask) backgroundTask).setTaskListener(gdmnGoogleSignInListener);
        }
    }

    @Override
    public void finish() {
        if (mAccountAuthenticatorResponse != null) {
            if (mResultBundle != null) {
                mAccountAuthenticatorResponse.onResult(mResultBundle);
            } else {
                mAccountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED, "canceled");
            }
            mAccountAuthenticatorResponse = null;
        }
        super.finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(TAG_URL, url);
        outState.putParcelable(TAG_ACCOUNT, account);
        outState.putSerializable(TAG_PARAMS, params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        GoogleSignInResult result;
        switch (requestCode) {
            case REQUEST_CODE_ASSOCIATE_WITH_GOOGLE:
                result = authSignInHelper.getSignInResultFromIntent(data);
                if (result.getSignInAccount() != null) {                                            //TODO handle error
                    Logger.d("idToken: " + result.getSignInAccount().getIdToken());
                    backgroundTask = new AssociateWithGoogleTask();
                    ((AssociateWithGoogleTask) backgroundTask).setTaskListener(associateWithGoogleListener);
                    ((AssociateWithGoogleTask) backgroundTask).execute(new AssosiateWithGoogleParams(
                            url,
                            AccountManager.get(getApplicationContext()).peekAuthToken(account, account.type),
                            result.getSignInAccount().getIdToken(),
                            params));
                }
                break;
            case REQUEST_CODE_GDMN_SIGN_IN:
                result = authSignInHelper.getSignInResultFromIntent(data);
                if (result.getSignInAccount() != null) {                                            //TODO handle error
                    Logger.d("idToken: " + result.getSignInAccount().getIdToken());
                    backgroundTask = new GdmnGoogleSignInTask();
                    ((GdmnGoogleSignInTask) backgroundTask).setTaskListener(gdmnGoogleSignInListener);
                    ((GdmnGoogleSignInTask) backgroundTask).execute(new GdmnGoogleSignInParams(
                            url,
                            result.getSignInAccount().getIdToken(),
                            params
                    ));
                }
                break;
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (backgroundTask != null) {
            backgroundTask.setTaskListener(null);
            return backgroundTask;
        }
        return super.onRetainCustomNonConfigurationInstance();
    }

    protected void gdmnSignInWithGoogle(String url) {
        if (backgroundTask == null || backgroundTask.getStatus() == AsyncTask.Status.FINISHED) {
            this.url = url;
            authSignInHelper.signOut(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        startActivityForResult(authSignInHelper.getSignInIntent(), REQUEST_CODE_GDMN_SIGN_IN);
                    }
                }
            });
        }
    }

    protected void gdmnSignIn(@NonNull String url, String login, @Nullable String password, @Nullable LinkedHashMap<String, String> params) {
        if (backgroundTask == null || backgroundTask.getStatus() == AsyncTask.Status.FINISHED) {
            backgroundTask = new GdmnSignInTask();
            ((GdmnSignInTask) backgroundTask).setTaskListener(gdmnSignInListener);
            ((GdmnSignInTask) backgroundTask).execute(new GdmnSignInParams(url, login, password, params));
        }
    }

    protected void associateWithGoogle(Account account, String url, LinkedHashMap<String, String> params) {
        if (backgroundTask == null || backgroundTask.getStatus() == AsyncTask.Status.FINISHED) {
            this.account = account;
            this.url = url;
            this.params = params;
            authSignInHelper.signOut(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        startActivityForResult(authSignInHelper.getSignInIntent(), REQUEST_CODE_ASSOCIATE_WITH_GOOGLE);
                    }
                }
            });
        }
    }

    private Account onCreateAccount(String login, String password, String gdmnToken, LinkedHashMap<String, String> params) throws AccountsException {
        final Account account = new Account(login, getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
        final AccountManager am = AccountManager.get(getApplicationContext());
        final Bundle result = new Bundle();
        if (am.addAccountExplicitly(account, password, mapToBundle(new Bundle(), params))) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, gdmnToken);
            am.setAuthToken(account, account.type, gdmnToken);

            setAccountAuthenticatorResult(result);
            setResult(RESULT_OK);
            return account;
        }
        throw new AccountsException("Account already exists");
    }

    private Bundle mapToBundle(Bundle bundle, Map<String, String> map) {
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                bundle.putString(entry.getKey(), entry.getValue());
            }
        }
        return bundle;
    }

    protected Bundle getAccountAuthenticatorResult() {
        return mResultBundle;
    }

    protected void setAccountAuthenticatorResult(Bundle result) {
        mResultBundle = result;
    }

    protected boolean isTaskProcess() {
        return backgroundTask != null && backgroundTask.getStatus() != AsyncTask.Status.FINISHED;
    }
}

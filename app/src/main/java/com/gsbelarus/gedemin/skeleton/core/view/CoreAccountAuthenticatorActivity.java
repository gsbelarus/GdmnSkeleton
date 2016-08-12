package com.gsbelarus.gedemin.skeleton.core.view;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.view.BaseActivity;
import com.gsbelarus.gedemin.skeleton.core.util.CoreAuthTokenLoader;
import com.gsbelarus.gedemin.skeleton.core.util.Result;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class CoreAccountAuthenticatorActivity extends BaseActivity {

    private static final String TAG_URL = "url";
    private static final String TAG_LOGIN = "login";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_PARAMS = "params";
    private static final int LOADER_ID = 1;

    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private Bundle mResultBundle = null;

    private LoaderManager.LoaderCallbacks<Result<String>> loaderCallbacks = new LoaderManager.LoaderCallbacks<Result<String>>() {
        @Override
        public Loader<Result<String>> onCreateLoader(int id, Bundle args) {
            onSignInProgress();
            return new CoreAuthTokenLoader(getApplicationContext(),
                    args.getString(TAG_URL),
                    args.getString(TAG_LOGIN),
                    args.getString(TAG_PASSWORD),
                    (LinkedHashMap<String, String>) args.getSerializable(TAG_PARAMS));
        }

        @Override
        public void onLoadFinished(Loader<Result<String>> loader, Result<String> data) {
            if (loader instanceof CoreAuthTokenLoader) {
                if (data.getException() == null) {
                    onTokenReceived(
                            ((CoreAuthTokenLoader) loader).getLogin(),
                            ((CoreAuthTokenLoader) loader).getPassword(),
                            data.getData(),
                            ((CoreAuthTokenLoader) loader).getParams());
                } else {
                    onSignInError(data.getException());
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Result<String>> loader) {
        }
    };

    protected abstract void onSignInProgress();

    protected abstract void onSignInSuccess(Account account);

    protected abstract void onSignInError(Exception error);

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mAccountAuthenticatorResponse = getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }

        if (getSupportLoaderManager().getLoader(LOADER_ID) != null) {
            getSupportLoaderManager().initLoader(LOADER_ID, null, loaderCallbacks);
            onSignInProgress();
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

    protected void login(String url, String login, String password, LinkedHashMap<String, String> params) {
        Bundle bundle = new Bundle();
        bundle.putString(TAG_URL, url);
        bundle.putString(TAG_LOGIN, login);
        bundle.putString(TAG_PASSWORD, password);
        bundle.putSerializable(TAG_PARAMS, params);
        if (getSupportLoaderManager().getLoader(LOADER_ID) == null) {
            getSupportLoaderManager().initLoader(LOADER_ID, bundle, loaderCallbacks);
        } else {
            getSupportLoaderManager().restartLoader(LOADER_ID, bundle, loaderCallbacks);
        }
    }

    private void onTokenReceived(String login, String password, String token, LinkedHashMap<String, String> params) {
        final Account account = new Account(login, getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
        final AccountManager am = AccountManager.get(this);
        final Bundle result = new Bundle();
        if (am.addAccountExplicitly(account, password, mapToBundle(new Bundle(), params))) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, token);
            am.setAuthToken(account, account.type, token);
        } else {
            result.putString(AccountManager.KEY_ERROR_MESSAGE, getString(R.string.sign_error_already_exists));
        }
        onSignInSuccess(account);
        setAccountAuthenticatorResult(result);
        setResult(RESULT_OK);
        finish();
    }

    private Bundle mapToBundle(Bundle bundle, Map<String, String> map) {
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                bundle.putString(entry.getKey(), entry.getValue());
            }
        }
        return bundle;
    }

    private void setAccountAuthenticatorResult(Bundle result) {
        mResultBundle = result;
    }
}

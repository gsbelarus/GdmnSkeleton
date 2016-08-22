package com.gsbelarus.gedemin.skeleton.core.util;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

public class AuthSignInHelper {

    private GoogleApiClient googleApiClient;
    private Scope[] scopes = new Scope[0];

    public void createAPIClient(FragmentActivity activity, String serverClientId) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(serverClientId)
                .requestEmail()
                .build();

        scopes = gso.getScopeArray();
        googleApiClient = new GoogleApiClient.Builder(activity.getApplicationContext())
                .enableAutoManage(activity, null)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public OptionalPendingResult<GoogleSignInResult> silentSignIn() {
        return Auth.GoogleSignInApi.silentSignIn(googleApiClient);
    }

    public void signOut(@Nullable ResultCallback<Status> resultCallback) {
        PendingResult<Status> result = Auth.GoogleSignInApi.signOut(googleApiClient);
        if (resultCallback != null) {
            result.setResultCallback(resultCallback);
        }
    }

    public Intent getSignInIntent() {
        return Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
    }

    public GoogleSignInResult getSignInResultFromIntent(Intent intent) {
        return Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public Scope[] getScopes() {
        return scopes;
    }
}

package com.gsbelarus.gedemin.skeleton.app.view.fragment;

import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.view.fragment.BaseFragment;
import com.gsbelarus.gedemin.skeleton.core.util.AuthSignInHelper;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;

public class AuthSignInFragment extends BaseFragment implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    /**
     * Configuration
     */

    @Override
    protected int getLayoutResource() {
        return R.layout.app_fragment_auth_signin;
    }

    private AuthSignInHelper authSignInHelper;
    private ViewGroup rootView;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.d("onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {
        super.onCreateView(rootView, savedInstanceState);

        this.rootView = rootView;

        SignInButton signInButton = (SignInButton) rootView.findViewById(R.id.google_sign_in_button);

        signInButton.setOnClickListener(this);
        rootView.findViewById(R.id.google_sign_out_button).setOnClickListener(this);
        rootView.findViewById(R.id.google_disconnect_button).setOnClickListener(this);

        authSignInHelper = new AuthSignInHelper(getActivity(), getContext(), getResources());

        String serverClientId = getString(R.string.server_client_id);

        authSignInHelper.createAPIClient(serverClientId, signInButton);

    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = authSignInHelper.optionalPendingResult();

        if (opr.isDone()) {
            Logger.d("Got cached sign-in");

            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            authSignInHelper.showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    authSignInHelper.hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AuthSignInHelper.REQUEST_CODE_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Logger.d("handleSignInResult:" + result.isSuccess());
        Logger.d("handleSignInResult:GET_TOKEN & GET_AUTH_CODE:success:" + result.getStatus().isSuccess());

        if (result.isSuccess()) {

            authSignInHelper.signInResult(result);

            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void signIn() {

        NetworkInfo netInfo = authSignInHelper.signInNetInfo();

        if(netInfo != null) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(authSignInHelper.getGoogleApiClient());
            startActivityForResult(signInIntent, AuthSignInHelper.REQUEST_CODE_SIGN_IN);
        }


    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(authSignInHelper.getGoogleApiClient()).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(authSignInHelper.getGoogleApiClient()).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            rootView.findViewById(R.id.google_sign_in_button).setVisibility(View.GONE);
            rootView.findViewById(R.id.google_sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            Logger.d(R.string.signed_out);

            rootView.findViewById(R.id.google_sign_in_button).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.google_sign_out_and_disconnect).setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                signIn();
                break;
            case R.id.google_sign_out_button:
                signOut();
                break;
            case R.id.google_disconnect_button:
                revokeAccess();
                break;
            default:
                break;
        }
    }

}

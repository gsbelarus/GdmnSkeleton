package com.gsbelarus.gedemin.skeleton.app.view.fragment;

import android.content.Intent;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.BasicUtils;
import com.gsbelarus.gedemin.skeleton.base.view.fragment.BaseFragment;
import com.gsbelarus.gedemin.skeleton.core.util.AuthSignInHelper;
import com.gsbelarus.gedemin.skeleton.core.util.CoreNetworkInfo;
import com.gsbelarus.gedemin.skeleton.core.util.CoreUtils;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;

public class AuthSignInFragment extends BaseFragment implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final int REQUEST_CODE_SIGN_IN = 123;

    /**
     * Configuration
     */

    @Override
    protected int getLayoutResource() {
        return R.layout.app_fragment_auth_signin;
    }

    private AuthSignInHelper authSignInHelper;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.d("onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {
        super.onCreateView(rootView, savedInstanceState);

        String serverClientId = getString(R.string.server_client_id);

        authSignInHelper = new AuthSignInHelper();
        authSignInHelper.createAPIClient(getActivity(), serverClientId);

        SignInButton signInButton = (SignInButton) rootView.findViewById(R.id.google_sign_in_button);
        signInButton.setScopes(authSignInHelper.getScopes());
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(this);
        rootView.findViewById(R.id.google_sign_out_button).setOnClickListener(this);
        rootView.findViewById(R.id.google_disconnect_button).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = authSignInHelper.silentSignIn();

        if (opr.isDone()) {
            Logger.d("Got cached sign-in");

            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
                break;
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Logger.d("handleSignInResult:" + result.isSuccess());
        Logger.d("handleSignInResult:GET_TOKEN & GET_AUTH_CODE:success:" + result.getStatus().isSuccess());

        if (result.isSuccess()) {

            GoogleSignInAccount acct = result.getSignInAccount();
            String personDisplayName = acct.getDisplayName();
            String personFamilyName = acct.getFamilyName();
            String personGivenName = acct.getGivenName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            Logger.d("\n" + getString(R.string.signed_display_name_fmt, personDisplayName) + "\n" +
                    getString(R.string.signed_family_name_fmt, personFamilyName) + "\n" +
                    getString(R.string.signed_given_name_fmt, personGivenName) + "\n" +
                    getString(R.string.signed_email_fmt, personEmail) + "\n" +
                    getString(R.string.signed_id_fmt, personId) + "\n" +
                    getString(R.string.signed_photo_fmt, personPhoto));

            // Getting id token
            String idToken = acct.getIdToken();
            Logger.d("idToken:" + idToken + "\n" + getString(R.string.id_token_fmt, idToken));

            // Getting authorization code
            String authCode = acct.getServerAuthCode();
            Logger.d("authCode:" + authCode + "\n" + getString(R.string.auth_code_fmt, authCode));

            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void signIn() {
        if (getView() != null) {
            CoreNetworkInfo.runIfNetworkAvailable(getView(), new Runnable() {
                @Override
                public void run() {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(authSignInHelper.getGoogleApiClient());
                    startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
                }
            });
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
        if (getView() != null) {
            if (signedIn) {
                getView().findViewById(R.id.google_sign_in_button).setVisibility(View.GONE);
                getView().findViewById(R.id.google_sign_out_and_disconnect).setVisibility(View.VISIBLE);
            } else {
                Logger.d(R.string.signed_out);

                getView().findViewById(R.id.google_sign_in_button).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.google_sign_out_and_disconnect).setVisibility(View.GONE);
            }
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

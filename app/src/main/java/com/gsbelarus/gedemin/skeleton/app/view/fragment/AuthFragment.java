package com.gsbelarus.gedemin.skeleton.app.view.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.view.fragment.BaseFragment;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;

public class AuthFragment extends BaseFragment implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    /**
     * Configuration
     */

    @Override
    protected int getLayoutResource() {
        return R.layout.app_fragment_auth;
    }

    private static final int REQUEST_CODE_SIGN_IN = 9001;

    private GoogleApiClient googleApiClient;
    private ProgressDialog progressDialog;
    private ViewGroup rootView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {
        super.onCreateView(rootView, savedInstanceState);

        this.rootView = rootView;

        SignInButton signInButton = (SignInButton) rootView.findViewById(R.id.google_sign_in_button);
        String serverClientId = getString(R.string.server_client_id);

        signInButton.setOnClickListener(this);
        rootView.findViewById(R.id.google_sign_out_button).setOnClickListener(this);
        rootView.findViewById(R.id.google_disconnect_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestIdToken(serverClientId)
                .requestServerAuthCode(serverClientId)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            Logger.d("Got cached sign-in");

            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Logger.d("handleSignInResult:" + result.isSuccess());
        Logger.d("handleSignInResult:GET_TOKEN & GET_AUTH_CODE:success:" + result.getStatus().isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.

            // Getting profile information
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

            //TODO save google drive

            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.d("onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
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
        }
    }

}

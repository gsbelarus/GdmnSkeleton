package com.gsbelarus.gedemin.skeleton.core.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.Scope;
import com.gsbelarus.gedemin.skeleton.R;

public class AuthSignInHelper implements GoogleApiClient.OnConnectionFailedListener {

    public static final int REQUEST_CODE_SIGN_IN = 9001;

    public String TAG = "AuthSignInHelperTest";

    private GoogleApiClient googleApiClient;
    private ProgressDialog progressDialog;

    FragmentActivity activity;
    Resources resources;
    Context context;

    public AuthSignInHelper(FragmentActivity activity, Context context, Resources resources) {
        this.activity = activity;
        this.resources = resources;
        this.context = context;

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.d("onConnectionFailed:" + connectionResult);
    }

    public void createAPIClient(String serverClientId, SignInButton signInButton) {

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestIdToken(serverClientId)
                .requestServerAuthCode(serverClientId)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.

        googleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(activity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
    }

    public void signInResult(GoogleSignInResult result) {
        // Signed in successfully, show authenticated UI.

        // Getting profile information
        GoogleSignInAccount acct = result.getSignInAccount();
        String personDisplayName = acct.getDisplayName();
        String personFamilyName = acct.getFamilyName();
        String personGivenName = acct.getGivenName();
        String personEmail = acct.getEmail();
        String personId = acct.getId();
        Uri personPhoto = acct.getPhotoUrl();

        Logger.d("\n" + context.getString(R.string.signed_display_name_fmt, personDisplayName) + "\n" +
                context.getString(R.string.signed_family_name_fmt, personFamilyName) + "\n" +
                context.getString(R.string.signed_given_name_fmt, personGivenName) + "\n" +
                context.getString(R.string.signed_email_fmt, personEmail) + "\n" +
                context.getString(R.string.signed_id_fmt, personId) + "\n" +
                context.getString(R.string.signed_photo_fmt, personPhoto));

        // Getting id token
        String idToken = acct.getIdToken();
        Logger.d("idToken:" + idToken + "\n" + context.getString(R.string.id_token_fmt, idToken));

        // Getting authorization code
        String authCode = acct.getServerAuthCode();
        Logger.d("authCode:" + authCode + "\n" + context.getString(R.string.auth_code_fmt, authCode));

        //TODO getting necessary key and saving
    }

    public OptionalPendingResult<GoogleSignInResult> optionalPendingResult() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);

        return opr;
    }

    public NetworkInfo signInNetInfo() {

        ConnectivityManager conMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null) {

            new AlertDialog.Builder(activity)
                    .setTitle(resources.getString(R.string.app_name))
                    .setMessage(R.string.no_internet_connect)
                    .setPositiveButton("OK", null).show();

        }

        return netInfo;
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getString(R.string.loading));
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

}

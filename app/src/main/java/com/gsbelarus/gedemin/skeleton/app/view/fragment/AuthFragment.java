package com.gsbelarus.gedemin.skeleton.app.view.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.view.fragment.BaseFragment;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class AuthFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    /**
     * Configuration
     */

    @Override
    protected int getLayoutResource() {
        return R.layout.app_fragment_auth;
    }

    private static final int REQUEST_CODE_SIGN_IN = 9001;
    private static final  int REQUEST_CODE_DRIVE_OPENER = 9002;

    private GoogleApiClient googleApiClient;
    private GoogleApiClient driveApiClient;
    private ProgressDialog progressDialog;
    private ViewGroup rootView;

    @Override
    protected void onCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {
        super.onCreateView(rootView, savedInstanceState);

        this.rootView = rootView;

        SignInButton signInButton = (SignInButton) rootView.findViewById(R.id.google_sign_in_button);
        String serverClientId = getString(R.string.server_client_id);

        signInButton.setOnClickListener(this);
        rootView.findViewById(R.id.google_sign_out_button).setOnClickListener(this);
        rootView.findViewById(R.id.google_disconnect_button).setOnClickListener(this);
        rootView.findViewById(R.id.google_drive_create_file).setOnClickListener(this);
        rootView.findViewById(R.id.google_drive_open_file).setOnClickListener(this);

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
    public void onResume() {
        super.onResume();

        if(driveApiClient == null) {

            driveApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        Logger.d(TAG, "driveApiClient connect");
        driveApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(driveApiClient != null) {

            Logger.d(TAG, "driveApiClient disconnect");
            driveApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.d("onConnectionFailed:" + connectionResult);
    }

    /**
     * It invoked when Google API client connected
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Logger.d(getActivity().getApplicationContext(), "Connected");
        Toast.makeText(getActivity().getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
    }

    /**
     * It invoked when connection suspend
     */
    @Override
    public void onConnectionSuspended(int i) {
        Logger.d(TAG, "DriveApiClient connection suspended");
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

            //TODO get necessary key and save on Google Drive

            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void signIn() {

        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null) {

            new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage(R.string.no_internet_connect)
                    .setPositiveButton("OK", null).show();

        } else {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
        }

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

    private void createFileOnDrive() {

        Drive.DriveApi.newDriveContents(driveApiClient).setResultCallback(
                new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(final DriveApi.DriveContentsResult result) {
                        if (result.getStatus().isSuccess()) {

                            final DriveContents driveContents = result.getDriveContents();

                            // Perform I/O off the UI thread.
                            new Thread() {
                                @Override
                                public void run() {

                                    // write content to DriveContents
                                    OutputStream outputStream = driveContents.getOutputStream();
                                    Writer writer = new OutputStreamWriter(outputStream);
                                    try {
                                        writer.write("Hello abhay!"); //TODO change text
                                        writer.close();
                                    } catch (IOException e) {
                                        Log.e(TAG, e.getMessage());
                                    }

                                    //TODO update text
                                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                            .setTitle("abhaytest2")
                                            .setMimeType("text/plain")
                                            .setStarred(true)
                                            .build();

                                    // create a file in root folder
                                    Drive.DriveApi.getRootFolder(driveApiClient)
                                            .createFile(driveApiClient, changeSet, driveContents)
                                            .setResultCallback(
                                                    new ResultCallback<DriveFolder.DriveFileResult>() {
                                                        @Override
                                                        public void onResult(@NonNull DriveFolder.DriveFileResult driveFileResult) {
                                                            if (driveFileResult.getStatus().isSuccess()) {
                                                                Logger.d(getActivity().getApplicationContext(), "file created: " +
                                                                        "" + driveFileResult.getDriveFile().getDriveId());
                                                            }
                                                        }
                                                    }
                                            );
                                }
                            }.start();
                        }
                    }
                });
    }


    private void openFileFromDrive() {

        Drive.DriveApi.newDriveContents(driveApiClient).setResultCallback(
                new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(final DriveApi.DriveContentsResult result) {
                        if(result.getStatus().isSuccess()) {

                            IntentSender intentSender = Drive.DriveApi
                                    .newOpenFileActivityBuilder()
                                    .setMimeType(new String[] { "text/plain", "text/html" })
                                    .build(driveApiClient);

                            try {

                                getActivity().startIntentSenderForResult(intentSender, REQUEST_CODE_DRIVE_OPENER, null, 0, 0, 0);

                            } catch (IntentSender.SendIntentException e) {

                                Log.w(TAG, "Unable to send intent", e);
                            }
                        }
                    }
                });
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
            rootView.findViewById(R.id.google_drive_create_file).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.google_drive_open_file).setVisibility(View.VISIBLE);
        } else {
            Logger.d(R.string.signed_out);

            rootView.findViewById(R.id.google_sign_in_button).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.google_sign_out_and_disconnect).setVisibility(View.GONE);
            rootView.findViewById(R.id.google_drive_create_file).setVisibility(View.GONE);
            rootView.findViewById(R.id.google_drive_open_file).setVisibility(View.GONE);
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
            case R.id.google_drive_create_file:
                createFileOnDrive();
                break;
            case R.id.google_drive_open_file:
                openFileFromDrive();
                break;
            default:
                break;
        }
    }

}

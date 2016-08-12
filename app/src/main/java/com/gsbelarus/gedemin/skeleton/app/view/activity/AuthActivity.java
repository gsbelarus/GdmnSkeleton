package com.gsbelarus.gedemin.skeleton.app.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.app.view.fragment.AuthDriveFragment;
import com.gsbelarus.gedemin.skeleton.app.view.fragment.AuthSignInFragment;
import com.gsbelarus.gedemin.skeleton.base.view.BaseActivity;

public class AuthActivity extends BaseActivity {

    /**
     * Configuration
     */

    @Override
    protected int getLayoutResource() {
        return R.layout.app_basic_activity;
    }

    @Nullable
    @Override
    protected Integer getToolbarIdResource() {
        return R.id.toolbar;
    }

    @Override
    protected ActivityType getActivityType() {
        return ActivityType.TITLED_SUB_LEVEL;
    }

    private static final int REQUEST_CODE_OPENER = 9002;

    private AuthSignInFragment authFragment;
    private AuthDriveFragment driveFragment;
    private int posAuthItem;
    private DriveId mFileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String names[] = {"AuthAPI", "DriveAPI"};

        //TODO fixed state for AlertDialog
        if (savedInstanceState == null) {

            new AlertDialog.Builder(this)
                    .setTitle(R.string.choose_auth_api)
                    .setItems(names, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            switch (which) {
                                case 0:
                                    authFragment = new AuthSignInFragment();
                                    includeFragment(R.id.activity_content_fragment_place, authFragment, AuthSignInFragment.class.getCanonicalName());
                                    break;
                                case 1:
                                    driveFragment = new AuthDriveFragment();
                                    includeFragment(R.id.activity_content_fragment_place, driveFragment, AuthDriveFragment.class.getCanonicalName());
                                    break;
                                default:
                                    break;
                            }

                            posAuthItem = which;
                        }
                    }).show();

        } else {

            switch (posAuthItem) {
                case 0:
                    authFragment = findSupportFragment(AuthSignInFragment.class.getCanonicalName());
                    break;
                case 1:
                    driveFragment = findSupportFragment(AuthDriveFragment.class.getCanonicalName());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Handle Response of selected file
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        switch (requestCode) {

            case REQUEST_CODE_OPENER:

                if (resultCode == RESULT_OK) {

                    mFileId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    Log.e("file id", mFileId.getResourceId() + "");

                    String url = "https://drive.google.com/open?id=" + mFileId.getResourceId();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }

                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

}

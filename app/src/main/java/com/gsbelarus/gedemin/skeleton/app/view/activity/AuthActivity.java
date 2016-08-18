package com.gsbelarus.gedemin.skeleton.app.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.app.view.fragment.AuthDriveFragment;
import com.gsbelarus.gedemin.skeleton.app.view.fragment.AuthSignInFragment;
import com.gsbelarus.gedemin.skeleton.base.view.BaseActivity;
import com.gsbelarus.gedemin.skeleton.core.util.AuthDriveHelper;

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

    private AuthSignInFragment authFragment;
    private AuthDriveFragment driveFragment;
    private int posAuthItem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            posAuthItem = savedInstanceState.getInt("posAuthItem");
        }

        if (posAuthItem == -1) {

            new AlertDialog.Builder(this)
                    .setTitle(R.string.choose_auth_api)
                    .setItems(R.array.auth_api_list, new DialogInterface.OnClickListener() {
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("posAuthItem", posAuthItem);
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

            case AuthDriveHelper.REQUEST_CODE_OPENER:

                if (resultCode == RESULT_OK) {

                    AuthDriveHelper authDriveHelper = new AuthDriveHelper();

                    Intent i = authDriveHelper.openFileFromGoogleDriveResponse(data, "https://drive.google.com/open?id=");
                    startActivity(i);
                }

                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

}

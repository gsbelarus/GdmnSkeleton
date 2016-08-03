package com.gsbelarus.gedemin.skeleton.app.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.app.view.fragment.AuthFragment;
import com.gsbelarus.gedemin.skeleton.base.view.BaseActivity;

public class AuthActivity extends BaseActivity{

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

    private static final  int REQUEST_CODE_DRIVE_OPENER = 9002;

    private AuthFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            fragment = new AuthFragment();
            includeFragment(R.id.activity_content_fragment_place, fragment, AuthFragment.class.getCanonicalName());
        } else {
            fragment = findSupportFragment(AuthFragment.class.getCanonicalName());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_DRIVE_OPENER) {

            if(resultCode == RESULT_OK) {

                DriveId mFileId = data.getParcelableExtra(
                        OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                Log.e("file id", mFileId.getResourceId() + "");

                String url = "https://drive.google.com/open?id="+ mFileId.getResourceId();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }

        }
    }
}

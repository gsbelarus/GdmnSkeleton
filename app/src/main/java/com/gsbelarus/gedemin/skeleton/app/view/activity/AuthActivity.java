package com.gsbelarus.gedemin.skeleton.app.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

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
}

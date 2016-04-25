package com.gsbelarus.gedemin.skeleton.view.activity;

import android.support.annotation.Nullable;
import android.os.Bundle;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.BaseActivity;
import com.gsbelarus.gedemin.skeleton.databinding.ActivityMainBinding;
import com.gsbelarus.gedemin.skeleton.view.fragment.MainFragment;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    /**
     * Сonfiguration
     */

    @Override
    protected ActivityType getActivityType() {
        return ActivityType.HIGH_LEVEL;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Nullable
    @Override
    protected Integer getToolbarIdResource() {
        return R.id.toolbar;
    }


    MainFragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setScrollFlagsToolbar(0);

        if (savedInstanceState == null) {
            fragment = new MainFragment();
            includeFragment(R.id.activity_content_fragment_place, fragment, MainFragment.class.getCanonicalName());
        } else {
            fragment = findSupportFragment(MainFragment.class.getCanonicalName());
        }
    }

    @Override
    protected void initViews() {}
}

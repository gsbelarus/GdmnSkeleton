package com.gsbelarus.gedemin.skeleton.app.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.app.view.fragment.DetailFragment;
import com.gsbelarus.gedemin.skeleton.base.view.BaseActivity;


public class DetailActivity extends BaseActivity {

    /**
     * Сonfiguration
     */

    @Override
    protected ActivityType getActivityType() {
        return ActivityType.TITLED_SUB_LEVEL;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.basic_activity;
    }

    @Nullable
    @Override
    protected Integer getToolbarIdResource() {
        return R.id.toolbar;
    }


    DetailFragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setScrollFlagsToolbar(0);

        if (savedInstanceState == null) {
            fragment = new DetailFragment();
            includeFragment(R.id.activity_content_fragment_place, fragment, DetailFragment.class.getCanonicalName());
        } else {
            fragment = findSupportFragment(DetailFragment.class.getCanonicalName());
        }
    }

}

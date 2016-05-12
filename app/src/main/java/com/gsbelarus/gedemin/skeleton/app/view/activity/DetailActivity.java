package com.gsbelarus.gedemin.skeleton.app.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.app.view.fragment.DetailFragment;
import com.gsbelarus.gedemin.skeleton.base.view.BaseActivity;


public class DetailActivity extends BaseActivity {

    private long dataId;
    private DetailFragment fragment;

    public static <T extends AppCompatActivity> Intent newStartIntent(Context context, long dataId) {
        Bundle extrasBundle = new Bundle();
        extrasBundle.putLong(DetailFragment.ARGUMENT_KEY_DATA_ID, dataId);

        return newStartIntent(context, DetailActivity.class, extrasBundle);
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setScrollFlagsToolbar(0);

        if (getIntent().getExtras().containsKey(DetailFragment.ARGUMENT_KEY_DATA_ID)) {
            dataId = getIntent().getExtras().getLong(DetailFragment.ARGUMENT_KEY_DATA_ID);
        }

        if (savedInstanceState == null) {
            fragment = DetailFragment.newInstance(dataId);
            includeFragment(R.id.activity_content_fragment_place, fragment, DetailFragment.class.getCanonicalName());
        } else {
            fragment = findSupportFragment(DetailFragment.class.getCanonicalName());
        }
    }
}

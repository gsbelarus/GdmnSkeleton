package com.gsbelarus.gedemin.skeleton.app.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.view.BaseActivity;
import com.gsbelarus.gedemin.skeleton.base.view.fragment.BaseDetailCursorFragment;
import com.gsbelarus.gedemin.skeleton.core.CoreDetailCursorFragment;


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


    protected long dataId;
    protected BaseDetailCursorFragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setScrollFlagsToolbar(0);

        initFragment(savedInstanceState);
    }

    protected void initFragment(Bundle savedInstanceState) {
        initFragment(savedInstanceState, CoreDetailCursorFragment.class);
    }

    protected void initFragment(Bundle savedInstanceState, Class<? extends BaseDetailCursorFragment> cl) {
        if (savedInstanceState == null) {
            try {
                fragment = cl.newInstance().newInstance(cl, dataId);

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            includeFragment(R.id.activity_content_fragment_place, fragment, cl.getCanonicalName());
        } else {
            fragment = findSupportFragment(cl.getCanonicalName());
        }
    }

    @Override
    protected void handleSavedInstanceState(@NonNull Bundle savedInstanceState) {}

    @Override
    protected void handleIntentExtras(@NonNull Bundle extras) {
        if (extras.containsKey(CoreDetailCursorFragment.ARGUMENT_KEY_DATA_ID)) {
            dataId = extras.getLong(CoreDetailCursorFragment.ARGUMENT_KEY_DATA_ID);
        }
    }

    public static <T extends AppCompatActivity> Intent newStartIntent(Context context, long dataId) {
        Bundle extrasBundle = new Bundle();
        extrasBundle.putLong(CoreDetailCursorFragment.ARGUMENT_KEY_DATA_ID, dataId);

        return newStartIntent(context, DetailActivity.class, extrasBundle);
    }
}

package com.gsbelarus.gedemin.skeleton.app.view.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.view.BaseActivity;
import com.gsbelarus.gedemin.skeleton.core.CoreEditCursorFragment;

public class EditActivity extends BaseActivity {

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

    private long dataId;
    private CoreEditCursorFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setScrollFlagsToolbar(0);

        if (savedInstanceState == null) {
            fragment = CoreEditCursorFragment.newInstance(dataId);
            includeFragment(R.id.activity_content_fragment_place, fragment, CoreEditCursorFragment.class.getCanonicalName());
        } else {
            fragment = findSupportFragment(CoreEditCursorFragment.class.getCanonicalName());
        }
    }

    @Override
    protected void handleSavedInstanceState(@NonNull Bundle savedInstanceState) {}

    @Override
    protected void handleIntentExtras(@NonNull Bundle extras) {
        if (extras.containsKey(CoreEditCursorFragment.ARGUMENT_KEY_DATA_ID)) {
            dataId = extras.getLong(CoreEditCursorFragment.ARGUMENT_KEY_DATA_ID);
        }
    }

    public static <T extends AppCompatActivity> Intent newStartIntent(Context context, long dataId) {
        Bundle extrasBundle = new Bundle();
        extrasBundle.putLong(CoreEditCursorFragment.ARGUMENT_KEY_DATA_ID, dataId);

        return newStartIntent(context, EditActivity.class, extrasBundle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) return false;

        return super.onOptionsItemSelected(item);
    }
}

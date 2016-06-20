package com.gsbelarus.gedemin.skeleton.app.view.activity;

import android.accounts.Account;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.app.App;
import com.gsbelarus.gedemin.skeleton.app.view.fragment.MainRecyclerCursorFragment;
import com.gsbelarus.gedemin.skeleton.base.view.BaseActivity;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;

public class MainActivity extends BaseActivity {

    /**
     * Сonfiguration
     */

    @Override
    protected ActivityType getActivityType() {
        return ActivityType.HIGH_LEVEL;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.app_basic_activity;
    }

    @Nullable
    @Override
    protected Integer getToolbarIdResource() {
        return R.id.toolbar;
    }


    private MainRecyclerCursorFragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSyncStatusListener(App.getSyncAccount(getApplicationContext()), new OnSyncStatusListener() {
            @Override
            public void onStart(Account account) {
                Logger.d();
            }

            @Override
            public void onFinish(Account account) {
                Logger.d();
                fragment.disableLayoutRefreshing(); //TODO  onPause
            }
        });

//        setScrollFlagsToolbar(0);

        if (savedInstanceState == null) {

            fragment = new MainRecyclerCursorFragment();
            includeFragment(R.id.activity_content_fragment_place, fragment, MainRecyclerCursorFragment.class.getCanonicalName());
        } else {
            fragment = findSupportFragment(MainRecyclerCursorFragment.class.getCanonicalName());
        }
    }

    @Override
    protected void handleSavedInstanceState(@NonNull Bundle savedInstanceState) {
    }

    @Override
    protected void handleIntentExtras(@NonNull Bundle extras) {
    }
}

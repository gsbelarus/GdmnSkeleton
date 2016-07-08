package com.gsbelarus.gedemin.skeleton.app.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.appindexing.AppIndex;
import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.app.service.SyncService;
import com.gsbelarus.gedemin.skeleton.app.view.fragment.MainRecyclerCursorFragment;
import com.gsbelarus.gedemin.skeleton.base.BaseSyncService;
import com.gsbelarus.gedemin.skeleton.base.view.BaseActivity;
import com.gsbelarus.gedemin.skeleton.core.util.IndexingHelper;
import com.gsbelarus.gedemin.skeleton.core.util.LogUtil;

public class MainActivity extends BaseActivity {

    private MainRecyclerCursorFragment fragment;

    private IndexingHelper indexingHelper;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectService(SyncService.class, new BaseSyncService.OnSyncListener() {
            @Override
            public void onStartSync() {
                super.onStartSync();
                LogUtil.d();
            }

            @Override
            public boolean onFinishSync(@Nullable String error) {
                LogUtil.d();

                fragment.disableLayoutRefreshing(); //TODO  onPause

                return super.onFinishSync(error);
            }
        });
//        setScrollFlagsToolbar(0);

        if (savedInstanceState == null) {
            fragment = new MainRecyclerCursorFragment();
            includeFragment(R.id.activity_content_fragment_place, fragment, MainRecyclerCursorFragment.class.getCanonicalName());
        } else {
            fragment = findSupportFragment(MainRecyclerCursorFragment.class.getCanonicalName());
        }

        // TODO: Define a title for the content shown.
        // TODO: Make sure this auto-generated URL is correct.
        // TODO: Define a description for the content show.
        indexingHelper = new IndexingHelper(this, Uri.parse("http://host/path"), "Main screen", "Description");

    }

    @Override
    public void onStart() {
        super.onStart();

        indexingHelper.getClient().connect();
        AppIndex.AppIndexApi.start(indexingHelper.getClient(), indexingHelper.getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        AppIndex.AppIndexApi.end(indexingHelper.getClient(), indexingHelper.getIndexApiAction());
        indexingHelper.getClient().disconnect();
    }

    @Override
    protected void handleSavedInstanceState(@NonNull Bundle savedInstanceState) {
    }

    @Override
    protected void handleIntentExtras(@NonNull Bundle extras) {
    }
}

package com.gsbelarus.gedemin.skeleton.base.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.gsbelarus.gedemin.skeleton.base.BaseSyncService;
import com.gsbelarus.gedemin.skeleton.core.util.LogUtil;


abstract public class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getCanonicalName();

    protected Context context;
    private Toolbar toolbar;

    private BaseSyncService.SyncBinder syncBinder;
    private ServiceConnection serviceConnection;
    private BaseSyncService.OnSyncListener onSyncListener;

    public static <T extends AppCompatActivity> Intent newStartIntent(Context context, Class<T> cl, Bundle extrasBundle) {
        Intent intent = new Intent(context, cl);
        intent.putExtras(extrasBundle);

        return intent;
    }

    /**
     * Сonfiguration
     */
    protected abstract ActivityType getActivityType();

    @LayoutRes
    protected abstract int getLayoutResource();

    @Nullable
    @IdRes
    protected abstract Integer getToolbarIdResource();

    /**
     * если AppBarLayout (toolbar должен находиться именно в нем) отсутствует,
     * необходимо переопределить
     */
    protected boolean hasAppBar() {
        return true;
    }

    protected abstract void handleSavedInstanceState(@NonNull Bundle savedInstanceState);

    protected abstract void handleIntentExtras(@NonNull Bundle extras);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResource());

        context = getBaseContext();
//        setupContentFragment();
        switch (getActivityType()) {
            case HIGH_LEVEL:
                setupHighLevelActivity();
                break;
            case SUB_LEVEL:
                setupSubActivity();
                break;
            case TITLED_SUB_LEVEL:
                setupSubActivityWithTitle();
                break;
        }

        // restore saved state
        if (savedInstanceState != null) handleSavedInstanceState(savedInstanceState);

        // handle intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) handleIntentExtras(extras);
    }

    protected void setupHighLevelActivity() {
        if (hasAppBar()) {
            setupAppBar();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    protected void setupSubActivity() {
        if (hasAppBar()) {
            setupAppBar();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);

                getSupportActionBar().setDisplayUseLogoEnabled(false);
                getSupportActionBar().setShowHideAnimationEnabled(true);
            }
        }
    }

    protected void setupSubActivityWithTitle() {
        setupSubActivity();
        if (hasAppBar() && getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private void setupAppBar() {
//        ViewGroup contentFrame = (ViewGroup) findViewById(getContentFrameIdResource());
//
//        appBarLayout = (AppBarLayout) getLayoutInflater().inflate(getAppBarLayoutResource(), contentFrame, false);
//        if (contentFrame instanceof LinearLayout)
//            contentFrame.addView(appBarLayout, 0);
//        else
//            contentFrame.addView(appBarLayout);

        if (getToolbarIdResource() != null) {
            toolbar = (Toolbar) findViewById(getToolbarIdResource());
            setSupportActionBar(toolbar);
        }

//        ViewGroup contentFragment = (ViewGroup) findViewById(getContentFragmentPlaceIdResource());
//        getLayoutInflater().inflate(R.layout.gdmnlib_view_appbar_shadow, contentFragment, true);
//
//        setVisibilityAppBarShadow(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public void setToolbarTitle(@StringRes int titleResId) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(titleResId);
    }

    @Nullable
    public Toolbar getToolbar() {
        return toolbar;
    }

    protected void includeFragment(@IdRes int fragmentPlaceIdResource, Fragment fragment, @Nullable String tag) {
        getSupportFragmentManager().beginTransaction()
                .add(fragmentPlaceIdResource, fragment, tag)
                .commitAllowingStateLoss();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    protected <T extends Fragment> T findSupportFragment(String tag) {
        return (T) getSupportFragmentManager().findFragmentByTag(tag);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        disconnectService();
    }

    protected boolean connectService(final Class<? extends BaseSyncService> serviceClass, final BaseSyncService.OnSyncListener onSyncListener) {
        disconnectService();
        this.onSyncListener = onSyncListener;
        return BaseSyncService.bindService(getApplicationContext(), serviceClass, serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                syncBinder = (BaseSyncService.SyncBinder) service;
                syncBinder.addOnSyncListener(BaseActivity.this.onSyncListener);
                LogUtil.d();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                syncBinder = null;
                connectService(serviceClass, onSyncListener);// // FIXME: 07.06.2016
                LogUtil.d();
            }
        });
    }

    private void disconnectService() {
        if (syncBinder != null && onSyncListener != null) {
            syncBinder.removeOnSyncListener(onSyncListener);
        }
        if (serviceConnection != null) {
            getApplicationContext().unbindService(serviceConnection);
        }
    }

    public boolean isSyncProcess() {
        return syncBinder != null;
    }

    protected enum ActivityType {
        HIGH_LEVEL, SUB_LEVEL, TITLED_SUB_LEVEL
    }
}

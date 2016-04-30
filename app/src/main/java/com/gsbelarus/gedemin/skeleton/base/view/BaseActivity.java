package com.gsbelarus.gedemin.skeleton.base.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


abstract public class BaseActivity<T_ActivityDataBinding extends ViewDataBinding> extends AppCompatActivity {

    public enum ActivityType {
        HIGH_LEVEL, SUB_LEVEL, TITLED_SUB_LEVEL
    }

    protected final String TAG = this.getClass().getCanonicalName();

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


    protected T_ActivityDataBinding activityDataBinding;
    protected Context context;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDataBinding();

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

        initViews();

        activityDataBinding.executePendingBindings();
    }

    protected void initDataBinding() {
        activityDataBinding = DataBindingUtil.setContentView(this, getLayoutResource());
    }

    protected abstract void initViews();

    protected void setupHighLevelActivity() {
        if (hasAppBar()) {
            setupAppBar();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    protected void setupSubActivity() {
        if (hasAppBar() ) {
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

        if (getToolbarIdResource() != null) { //TODO не проверять - ex
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

}

package com.gsbelarus.gedemin.skeleton.base.view;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
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

import com.gsbelarus.gedemin.skeleton.base.BasicAccountHelper;

abstract public class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getCanonicalName();

    protected enum ActivityType {
        HIGH_LEVEL, SUB_LEVEL, TITLED_SUB_LEVEL
    }

    /**
     * Сonfiguration
     */

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

    protected abstract ActivityType getActivityType();


    protected Context context;
    private Toolbar toolbar;
    private BasicAccountHelper.AccountChangeManager accountChangeManager;


    public static <T extends AppCompatActivity> Intent newStartIntent(Context context, Class<T> cl, Bundle extrasBundle) {
        Intent intent = new Intent(context, cl);
        intent.putExtras(extrasBundle);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResource());

        BasicAccountHelper basicAccountHelper = new BasicAccountHelper(getApplicationContext());
        accountChangeManager = basicAccountHelper.setOnChangedListener(new BasicAccountHelper.OnChangedListener() {
            @Override
            public void onChanged(Account oldAccount, Account newAccount) {
                onAccountChanged(oldAccount, newAccount);
            }
        });
        accountChangeManager.onCreate();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        accountChangeManager.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        accountChangeManager.onResume();
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

    protected void handleSavedInstanceState(@NonNull Bundle savedInstanceState) {}
    protected void handleIntentExtras(@NonNull Bundle extras) {}
    protected void onAccountChanged(Account oldAccount, Account newAccount) {}

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

    protected void replaceFragment(@IdRes int fragmentPlaceIdResource, Fragment fragment, @Nullable String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(fragmentPlaceIdResource, fragment, tag)
                .commitAllowingStateLoss();
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

    protected void setSelectedAccount(Account account) {
        accountChangeManager.setSelectedAccount(account);
    }

    protected Account getSelectedAccount() {
        return BasicAccountHelper.getSelectedAccount(context);
    }

    protected void chooseAccount(String accountType) {
        accountChangeManager.chooseAccount(this, accountType);
    }
}

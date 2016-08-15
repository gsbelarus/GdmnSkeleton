package com.gsbelarus.gedemin.skeleton.app.view.activity;

import android.accounts.Account;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.app.view.fragment.MainRecyclerCursorFragment;
import com.gsbelarus.gedemin.skeleton.base.view.BaseActivity;
import com.gsbelarus.gedemin.skeleton.core.util.IndexingHelper;

public class MainActivity extends BaseActivity {

    /**
     * Configuration
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

    private IndexingHelper indexingHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        super.handleSavedInstanceState(savedInstanceState);
    }

    @Override
    protected void handleIntentExtras(@NonNull Bundle extras) {
        super.handleIntentExtras(extras);
    }

    @Override
    protected void onAccountChanged(Account oldAccount, Account newAccount) {
        super.onAccountChanged(oldAccount, newAccount);

        fragment = new MainRecyclerCursorFragment();
        replaceFragment(R.id.activity_content_fragment_place, fragment, MainRecyclerCursorFragment.class.getCanonicalName());
        if (newAccount == null) {
            chooseAccount(getString(R.string.account_type));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.choose_account_menu, menu);          //TODO for etsts
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_choose_account:                             //TODO for tests
                chooseAccount(getString(R.string.account_type));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

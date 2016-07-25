package com.gsbelarus.gedemin.skeleton.app.view.fragment;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.app.service.SyncService;
import com.gsbelarus.gedemin.skeleton.app.view.RequestCode;
import com.gsbelarus.gedemin.skeleton.app.view.activity.DetailActivity;
import com.gsbelarus.gedemin.skeleton.app.view.activity.EditActivity;
import com.gsbelarus.gedemin.skeleton.base.BaseSyncService;
import com.gsbelarus.gedemin.skeleton.base.BasicSyncStatusNotifier;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.listener.OnRecyclerItemClickListener;
import com.gsbelarus.gedemin.skeleton.core.data.CoreContract;
import com.gsbelarus.gedemin.skeleton.core.util.CoreNetworkInfo;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;
import com.gsbelarus.gedemin.skeleton.core.view.fragment.CoreSearchableRecyclerCursorFragment;

//TODO SwipeRefreshProvider
public class MainRecyclerCursorFragment extends CoreSearchableRecyclerCursorFragment implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    /**
     * Configuration
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_main;
    }


    private SwipeRefreshLayout swipeRefreshLayout;
    private BasicSyncStatusNotifier syncStatusNotifier;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getAdapter().setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onClick(View view, int position, int viewType) {
                long id = getAdapter().getAdapterDataSource().getItemId(position);
                startActivity(DetailActivity.newStartIntent(getActivity(), id));
            }
        });
    }

    @Override
    protected void onCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {
        super.onCreateView(rootView, savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        fab.setOnClickListener(this);

        initRefreshLayout(rootView);

        BasicSyncStatusNotifier.OnSyncStatusListener onSyncStatusListener = new BasicSyncStatusNotifier.OnSyncStatusListener() {
            @Override
            public void onStartSync(Account account) {
                Logger.d();
            }

            @Override
            public void onFinishSync(Account account) {
                Logger.d();
                if (getDataCursor() == null) {
                    restartLoader();            //TODO temp
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        };
        syncStatusNotifier = new BasicSyncStatusNotifier(getString(R.string.authority));
        syncStatusNotifier.addSyncStatusListener(SyncService.getDefaultSyncAccount(getContext()), onSyncStatusListener);
        syncStatusNotifier.addSyncStatusListener(SyncService.getDemoSyncAccount(getContext()), onSyncStatusListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        syncStatusNotifier.clearSyncStatusListeners();
    }

    private void initRefreshLayout(ViewGroup rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getIntArray(R.array.swipe_refresh));
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        if (!CoreNetworkInfo.isNetworkAvailable(getContext())) swipeRefreshLayout.setRefreshing(false);
        CoreNetworkInfo.runWithNetworkConnection(getView(), new Runnable() {
            @Override
            public void run() {
                ContentResolver.requestSync(SyncService.getDefaultSyncAccount(getContext()),
                        getString(R.string.authority), SyncService.getTaskBundle(BaseSyncService.TypeTask.FOREGROUND));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;

        if (v.getId() == R.id.fab_add) {
            getDatabaseManager().beginTransaction();
            Long dataId = getDatabaseManager().insert(CoreContract.TEST_TABLE, CoreContract.TEST_TABLE_NULLHACK_COLUMN, new ContentValues()); //TODO create nullhack column
            if (dataId != null) startActivityForResult(EditActivity.newStartIntent(getActivity(), dataId), RequestCode.REQUEST_CODE_EDIT_CHANGED);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestCode.REQUEST_CODE_EDIT_CHANGED) {
            if (resultCode == Activity.RESULT_OK) {
                getDatabaseManager().commitTransaction();
            } else {
                getDatabaseManager().cancelTransaction();
            }
        }
    }

}

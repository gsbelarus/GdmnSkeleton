package com.gsbelarus.gedemin.skeleton.app.view.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.app.App;
import com.gsbelarus.gedemin.skeleton.app.service.SyncService;
import com.gsbelarus.gedemin.skeleton.app.view.RequestCode;
import com.gsbelarus.gedemin.skeleton.app.view.activity.DetailActivity;
import com.gsbelarus.gedemin.skeleton.app.view.activity.EditActivity;
import com.gsbelarus.gedemin.skeleton.base.BaseSyncService;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.listener.OnRecyclerItemClickListener;
import com.gsbelarus.gedemin.skeleton.core.data.CoreContract;
import com.gsbelarus.gedemin.skeleton.core.view.fragment.CoreSearchableRecyclerCursorFragment;

//TODO SwipeRefreshProvider
public class MainRecyclerCursorFragment extends CoreSearchableRecyclerCursorFragment implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    /**
     * Сonfiguration
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_main;
    }


    private SwipeRefreshLayout swipeRefreshLayout;


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
    public void onResume() {
        super.onResume();

        // Tracking the screen view
        App.getInstance().trackScreenView(this.getClass().getSimpleName());
    }

    @Override
    protected void doOnCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {
        super.doOnCreateView(rootView, savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        fab.setOnClickListener(this);

        initRefreshLayout(rootView);
    }

    private void initRefreshLayout(ViewGroup rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getIntArray(R.array.swipe_refresh));
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        BaseSyncService.startSync(getContext(), SyncService.class, BaseSyncService.TypeTask.FOREGROUND);
    }

    public void disableLayoutRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;

        if (v.getId() == R.id.fab_add) {
            getDatabaseManager().beginTransaction();
            Long dataId = getDatabaseManager().insert(CoreContract.TEST_TABLE, CoreContract.TEST_TABLE_NULLHACK_COLUMN, new ContentValues()); //TODO create nullhack column
            if (dataId != null)
                startActivityForResult(EditActivity.newStartIntent(getActivity(), dataId), RequestCode.REQUEST_CODE_EDIT_CHANGED);
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

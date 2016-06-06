package com.gsbelarus.gedemin.skeleton.base.view.fragment;

import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.gsbelarus.gedemin.skeleton.base.view.adapter.BasicCursorRecyclerViewAdapter;


// TODO 1. PullToRefreshBaseFragment  2. <DB_MANAGER_T extends BaseDatabaseManager>
public abstract class BaseRecyclerCursorFragment extends BaseCursorFragment {

    private RecyclerView.LayoutManager layoutManager;

    protected abstract BasicCursorRecyclerViewAdapter getAdapter();
    protected abstract RecyclerView.LayoutManager createLayoutManager();


    protected void setupRecyclerView(RecyclerView recyclerView) {   //TODO избавиться
        layoutManager = createLayoutManager();
//        if (savedLayoutManagerState != null) {
//            layoutManager.onRestoreInstanceState(savedLayoutManagerState);
//            savedLayoutManagerState = null;
//        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(getAdapter());
    }

    @Override
    protected void setDataCursor(@Nullable Cursor cursor) {
        getAdapter().swapCursor(cursor);
    }

    @Override
    protected void bindViewOnCursorLoaded() {}

}

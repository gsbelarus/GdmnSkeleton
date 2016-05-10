package com.gsbelarus.gedemin.skeleton.base.view.fragment;

import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.gsbelarus.gedemin.skeleton.base.view.adapter.BasicCursorRecyclerViewAdapter;


// TODO 1. PullToRefreshBaseFragment  2. <DB_MANAGER_T extends BaseDatabaseManager>
public abstract class BaseRecyclerCursorFragment extends BaseCursorFragment {

    private DataSetObserver dataSetObserver;
    private final DataSetObservable dataSetObservable = new DataSetObservable();

    private RecyclerView.LayoutManager layoutManager;

    protected abstract BasicCursorRecyclerViewAdapter getAdapter();
    protected abstract RecyclerView.LayoutManager createLayoutManager();


    protected void setupRecyclerView(RecyclerView recyclerView) {
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


/////////
    @Override
    protected void registerObservers(@Nullable Cursor cursor) {
        super.registerObservers(cursor);

        if (dataSetObserver == null) {
            dataSetObserver = new CursorDataSetObserver();
        }
        if (cursor != null) {
            cursor.registerDataSetObserver(dataSetObserver);
            dataSetObservable.registerObserver(dataSetObserver);
        }
    }

    @Override
    protected void unregisterObservers(@Nullable Cursor cursor) {
        super.unregisterObservers(cursor);

        if (cursor != null) {
            if (dataSetObserver != null) {
                cursor.unregisterDataSetObserver(dataSetObserver);
                dataSetObservable.unregisterObserver(dataSetObserver);
            }
        }
    }

    protected void notifyDataSetInvalidated() { //TODO test
        dataSetObservable.notifyInvalidated();
    }

    public class CursorDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            super.onChanged();

            getAdapter().getAdapterDataSource().setDataValid(true);
            getAdapter().notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();

            getAdapter().getAdapterDataSource().setDataValid(false);
            notifyDataSetInvalidated();
        }
    }
}

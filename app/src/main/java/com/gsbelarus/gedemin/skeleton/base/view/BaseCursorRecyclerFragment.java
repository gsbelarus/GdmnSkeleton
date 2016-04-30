package com.gsbelarus.gedemin.skeleton.base.view;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;

import com.gsbelarus.gedemin.skeleton.base.data.BaseDatabaseManager;
import com.gsbelarus.gedemin.skeleton.base.data.loader.BasicCursorLoader;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.BasicCursorRecyclerViewAdapter;
import com.gsbelarus.gedemin.skeleton.core.CoreDatabaseManager;

// TODO <DB_MANAGER_T extends BaseDatabaseManager>
public abstract class BaseCursorRecyclerFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_ID = 0;

    private BaseDatabaseManager databaseManager;
    private ContentObserver contentObserver;
    private RecyclerView.LayoutManager layoutManager;

    protected abstract BasicCursorRecyclerViewAdapter getAdapter();
    protected abstract RecyclerView.LayoutManager createLayoutManager();
    protected abstract BaseDatabaseManager createDatabaseManager();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseManager = createDatabaseManager();
        databaseManager.open();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) { //TODO onPostCreate?
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);

        if (getLoaderManager().getLoader(LOADER_ID) == null) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public void onDestroyView() {
        getLoaderManager().destroyLoader(LOADER_ID); //TODO ?

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        databaseManager.close();
    }

    protected void setupRecyclerView(RecyclerView recyclerView) {
        layoutManager = createLayoutManager();
//        if (savedLayoutManagerState != null) {
//            layoutManager.onRestoreInstanceState(savedLayoutManagerState);
//            savedLayoutManagerState = null;
//        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(getAdapter());
    }

    protected BaseDatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Override
    public abstract BasicCursorLoader onCreateLoader(int id, Bundle args);

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
        getAdapter().swapCursor(cursor);
        registerContentObserver(); // TODO выше?
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getAdapter().swapCursor(null);
    }

    private void registerContentObserver() { //TODO проверить
        if (contentObserver == null) {
            contentObserver = new CursorAdapterContentObserver(new Handler());
        }
        if (getAdapter().getDataCursor() != null) {
            getAdapter().getDataCursor().registerContentObserver(contentObserver);
        }
    }

    private void unregisterContentObserver() {      //TODO проверить
        if (getAdapter().getDataCursor() != null) {
            if (contentObserver != null) {
                getAdapter().getDataCursor().unregisterContentObserver(contentObserver);
            }
            getAdapter().getDataCursor().close();
        }
    }

    private void onContentChanged() {
        unregisterContentObserver();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }


    private class CursorAdapterContentObserver extends ContentObserver {

        public CursorAdapterContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onContentChanged();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            onContentChanged();
        }
    }
}

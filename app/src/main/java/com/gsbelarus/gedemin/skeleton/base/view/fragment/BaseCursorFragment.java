package com.gsbelarus.gedemin.skeleton.base.view.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.gsbelarus.gedemin.skeleton.base.data.BaseDatabaseManager;


abstract public class BaseCursorFragment<FRAGMENTSTATE_T extends BaseFragment.BasicState> extends BaseFragment<FRAGMENTSTATE_T> implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 0;

    private BaseDatabaseManager databaseManager;


    protected abstract BaseDatabaseManager createDatabaseManager();
    protected abstract Cursor getDataCursor();
    protected abstract void setDataCursor(@Nullable Cursor cursor);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseManager = createDatabaseManager();
        databaseManager.open();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        databaseManager.close();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getLoaderManager().getLoader(LOADER_ID) == null) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public void onDestroyView() {
        getLoaderManager().destroyLoader(LOADER_ID);

        super.onDestroyView();
    }

    public void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public abstract Loader<Cursor> onCreateLoader(int id, Bundle args);

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
        swapCursor(cursor);
    }

    protected void bindViewOnCursorLoaded() {}

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        swapCursor(null);
    }

    @Nullable
    public Cursor swapCursor(@Nullable Cursor newCursor) {
        final Cursor oldCursor = getDataCursor();
        setDataCursor(newCursor);

        bindViewOnCursorLoaded();

        return oldCursor;
    }

    // accessors

    protected BaseDatabaseManager getDatabaseManager() {
        return databaseManager;
    }

}

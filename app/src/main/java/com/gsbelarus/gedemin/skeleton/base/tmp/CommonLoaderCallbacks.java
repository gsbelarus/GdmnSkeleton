package com.gsbelarus.gedemin.skeleton.base.tmp;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.gsbelarus.gedemin.skeleton.base.CommonCursorLoader;
import com.gsbelarus.gedemin.skeleton.data.DatabaseManager;

//TODO support Loader ?

public class CommonLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface LoaderCallbacksListener {
        void onLoadFinished(Loader<Cursor> loader, Cursor data);
        void onLoaderReset(final Loader<Cursor> loader) ;
    }

    private final int loaderId;

    private LoaderCallbacksListener loaderCallbacksListener; //WeakReference
    private LoaderManager loaderManager;
    private Context context;

    private DatabaseManager databaseManager;
    private String tableName;
    private String[] projection;
    private String selection;
    private String[] selectionArgs;
    private String sortOrder;


    public CommonLoaderCallbacks(Context context,
                                 DatabaseManager databaseManager,
                                 LoaderCallbacksListener loaderCallbacksListener,
                                 int loaderId,
                                 LoaderManager loaderManager,
                                 String[] projection, String selection, String[] selectionArgs, String sortOrder, String tableName) {
        this.context = context;
        this.databaseManager = databaseManager;
        this.loaderCallbacksListener = loaderCallbacksListener;
        this.loaderId = loaderId;
        this.loaderManager = loaderManager;
        this.projection = projection;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.sortOrder = sortOrder;
        this.tableName = tableName;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CommonCursorLoader(context, databaseManager, tableName, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        loaderCallbacksListener.onLoadFinished(loader, data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loaderCallbacksListener.onLoaderReset(loader);
    }

    public void onStart(Context context) {
        final Loader<?> loader = loaderManager.getLoader(loaderId);
        if (loader == null) {
            loaderManager.initLoader(loaderId, null, this);
        } else {
            loaderManager.restartLoader(loaderId, null, this);
        }
    }

    public void onStop(Context context) {
        loaderManager.destroyLoader(loaderId);
    }

}

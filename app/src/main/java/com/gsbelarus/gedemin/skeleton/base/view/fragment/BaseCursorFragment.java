package com.gsbelarus.gedemin.skeleton.base.view.fragment;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.gsbelarus.gedemin.skeleton.base.data.BaseDatabaseManager;
import com.gsbelarus.gedemin.skeleton.core.LogUtil;


abstract public class BaseCursorFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_ID = 0;

    private BaseDatabaseManager databaseManager;
    private ContentObserver contentObserver; //TODO list
//    private DataSetObserver dataSetObserver;
//    private final DataSetObservable dataSetObservable = new DataSetObservable();

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

    @Override
    public void onDestroy() {
        super.onDestroy();

        databaseManager.close();
    }

    @Override
    public abstract Loader<Cursor> onCreateLoader(int id, Bundle args);

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
        swapCursor(cursor);
        bindViewOnCursorLoaded();
    }

    abstract protected void bindViewOnCursorLoaded();


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        swapCursor(null);
    }

    @Nullable
    public Cursor swapCursor(@Nullable Cursor newCursor) {
        final Cursor oldCursor = getDataCursor();
        setDataCursor(newCursor);

        unregisterObservers(oldCursor);
        registerObservers(newCursor);

        return oldCursor;
    }

    protected void registerObservers(@Nullable Cursor cursor) {
        if (contentObserver == null) {
            contentObserver = new CursorContentObserver(new Handler());
        }
//        if (dataSetObserver == null) {
//            dataSetObserver = new CursorDataSetObserver();
//        }

        if (cursor != null) {
            cursor.registerContentObserver(contentObserver);
//            cursor.registerDataSetObserver(dataSetObserver);
//            dataSetObservable.registerObserver(dataSetObserver);
        }
    }

    protected void unregisterObservers(@Nullable Cursor cursor) {
        if (cursor != null) {
            if (contentObserver != null) {
                cursor.unregisterContentObserver(contentObserver);
            }
//            if (dataSetObserver != null) {
//                cursor.unregisterDataSetObserver(dataSetObserver);
//                dataSetObservable.unregisterObserver(dataSetObserver);
//            }
        }
    }

//    protected void notifyDataSetInvalidated() { //TODO test
//        dataSetObservable.notifyInvalidated();
//    }

    private void onContentChanged() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    protected BaseDatabaseManager getDatabaseManager() {
        return databaseManager;
    }


    public class CursorContentObserver extends ContentObserver {

        public CursorContentObserver(Handler handler) {
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


//    public class CursorDataSetObserver extends DataSetObserver {
//
//        @Override
//        public void onChanged() {
//            super.onChanged();
//
////            getAdapterDataSource().setDataValid(true);
////            notifyDataSetChanged();  TODO
//        }
//
//        @Override
//        public void onInvalidated() {
//            super.onInvalidated();
//
////            getAdapterDataSource().setDataValid(false);
//            notifyDataSetInvalidated();
//        }
//    }

}

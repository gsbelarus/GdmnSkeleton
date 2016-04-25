package com.gsbelarus.gedemin.skeleton.base;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;

import com.gsbelarus.gedemin.skeleton.data.DatabaseManager;


public abstract class BaseCursorRecyclerFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_ID = 0;

    protected DatabaseManager databaseManager;
    private RecyclerView.LayoutManager layoutManager;


    protected abstract BaseCursorRecyclerViewAdapter getAdapter();
    protected abstract RecyclerView.LayoutManager createLayoutManager();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseManager = DatabaseManager.getInstance(getContext());
        databaseManager.open();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this); //getActivity().getSupportLoaderManager()

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
    public abstract CommonCursorLoader onCreateLoader(int id, Bundle args);

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        getAdapter().swapCursor(data);
        //TODO updateEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getAdapter().swapCursor(null);
    }



    //onCreateView
    protected void setupRecyclerView(RecyclerView recyclerView) {
        layoutManager = createLayoutManager();
//        if (savedLayoutManagerState != null) {
//            layoutManager.onRestoreInstanceState(savedLayoutManagerState);
//            savedLayoutManagerState = null;
//        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(getAdapter());
    }

}

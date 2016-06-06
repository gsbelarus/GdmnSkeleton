package com.gsbelarus.gedemin.skeleton.base.view.adapter;

import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;

import com.gsbelarus.gedemin.skeleton.base.view.adapter.datasource.CursorRecyclerAdapterDataSource;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.item.CursorRecyclerItemViewTypeModel;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.viewhandler.CursorRecyclerAdapterViewHandler;

//  <T>

public class BasicCursorRecyclerViewAdapter extends BasicRecyclerViewAdapter implements Filterable,
        CursorFilter.CursorFilterClient {

    private DataSetObserver dataSetObserver;
    private final DataSetObservable dataSetObservable = new DataSetObservable();

    private CursorFilter cursorFilter;
    private FilterQueryProvider filterQueryProvider;

    public BasicCursorRecyclerViewAdapter(@LayoutRes int layout,
                                          String[] from,
                                          int[] to) {
        setAdapterDataSource(new CursorRecyclerAdapterDataSource(null));

        CursorRecyclerAdapterViewHandler viewHandler = new CursorRecyclerAdapterViewHandler(new CursorRecyclerItemViewTypeModel(layout, from, to));
        setAdapterViewHandler(viewHandler);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @NonNull
    @Override
    public CursorRecyclerAdapterDataSource getAdapterDataSource() {
        return (CursorRecyclerAdapterDataSource) super.getAdapterDataSource();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!getAdapterDataSource().isDataValid()) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        super.onBindViewHolder(holder, position);
    }

    @Override
    public Filter getFilter() {
        return cursorFilter != null ? cursorFilter : new CursorFilter(this);
    }

    @Override
    public Cursor getDataCursor() {
        return getAdapterDataSource().getDataCursor();
    }

    @Override
    public void setFiltratedCursor(@Nullable Cursor cursor) {
        swapCursor(cursor);
    }

    public void setFilterQueryProvider(FilterQueryProvider filterQueryProvider) {
        this.filterQueryProvider = filterQueryProvider;
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        return filterQueryProvider != null ? filterQueryProvider.runQuery(constraint): getDataCursor();
    }

    @Override
    public CharSequence convertToString(Cursor cursor) { //TODO ?
        return cursor == null ? "" : cursor.toString();
    }

    @Nullable
    public Cursor swapCursor(@Nullable Cursor newCursor) {
        final Cursor oldCursor = getAdapterDataSource().swapCursor(newCursor);
        unregisterObservers(oldCursor);

        if (getDataCursor() != null) {
            registerObservers(getDataCursor());

            notifyDataSetChanged();
//            if (oldCursor != null) notifyItemRangeRemoved(0, oldCursor.getCount());  //TODO
//            if (newCursor != null) notifyItemRangeInserted(0, newCursor.getCount());
        } else {
            notifyDataSetChanged();
        }

        return oldCursor;
    }

    private void registerObservers(@Nullable Cursor cursor) {
        if (dataSetObserver == null) {
            dataSetObserver = new CursorDataSetObserver();
        }
        if (cursor != null) {
            cursor.registerDataSetObserver(dataSetObserver);
            dataSetObservable.registerObserver(dataSetObserver);
        }
    }

    private void unregisterObservers(@Nullable Cursor cursor) {
        if (cursor != null) {
            if (dataSetObserver != null) {
                cursor.unregisterDataSetObserver(dataSetObserver);
                dataSetObservable.unregisterObserver(dataSetObserver);
            }
        }
    }

    private void notifyDataSetInvalidated() { //TODO test
        dataSetObservable.notifyInvalidated();
    }


    private class CursorDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            super.onChanged();

            getAdapterDataSource().setDataValid(true);
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();

//            getAdapterDataSource().setDataValid(false);
//            notifyDataSetInvalidated();
            notifyDataSetChanged();
        }
    }

}
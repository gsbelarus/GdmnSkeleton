package com.gsbelarus.gedemin.skeleton.base.view.adapter;

import android.database.Cursor;
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

// TODO: 1. extends CursorAdapter  2. <T>

public class BasicCursorRecyclerViewAdapter extends BasicRecyclerViewAdapter implements Filterable,
        CursorFilter.CursorFilterClient {

    private Cursor cursor;
    private CursorFilter cursorFilter;
    private FilterQueryProvider filterQueryProvider;
    private Callback callback;

    public interface Callback {
        void updateDataCursor(Cursor cursor);
    }

    public BasicCursorRecyclerViewAdapter(@Nullable Cursor cursor,
                                          @LayoutRes int layout,
                                          String[] from,
                                          int[] to,
                                          Callback callback) {

        this.cursor = cursor;
        CursorRecyclerAdapterDataSource dataSource = new CursorRecyclerAdapterDataSource(cursor);
        setAdapterDataSource(dataSource);

        CursorRecyclerAdapterViewHandler viewHandler = new CursorRecyclerAdapterViewHandler(new CursorRecyclerItemViewTypeModel(layout, from, to));
        setAdapterViewHandler(viewHandler);

        this.callback = callback;
        //registerObservers(getDataCursor());
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @NonNull
    @Override
    public CursorRecyclerAdapterDataSource getAdapterDataSource() { //TODO норм или generic?
        return (CursorRecyclerAdapterDataSource) super.getAdapterDataSource();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!getAdapterDataSource().isDataValid()) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        super.onBindViewHolder(holder, position);
    }

    @Nullable
    public Cursor swapCursor(@Nullable Cursor newCursor) {
        final Cursor oldCursor = getAdapterDataSource().swapCursor(newCursor);
        //unregisterObservers(oldCursor);

        if (getDataCursor() != null) {
            //registerObservers(getDataCursor());

            if (oldCursor != null) notifyItemRangeRemoved(0, oldCursor.getCount());
            if (newCursor != null) notifyItemRangeInserted(0, newCursor.getCount());
        } else {
            notifyDataSetChanged();
        }


        return oldCursor;
    }

    @Override
    public Cursor getDataCursor() {
        return getAdapterDataSource().getDataCursor();
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
        return cursor == null ? "" : cursor.toString();
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if (filterQueryProvider != null) {
            return filterQueryProvider.runQuery(constraint);
        }

        return cursor;
    }

    @Override
    public Filter getFilter() {
        if (cursorFilter == null) {
            cursorFilter = new CursorFilter(this);
        }
        return cursorFilter;
    }

    public void setFilterQueryProvider(FilterQueryProvider filterQueryProvider) {
        this.filterQueryProvider = filterQueryProvider;
    }

    @Override
    public void updateCursor(@Nullable Cursor cursor) {
        callback.updateDataCursor(cursor);
    }
}
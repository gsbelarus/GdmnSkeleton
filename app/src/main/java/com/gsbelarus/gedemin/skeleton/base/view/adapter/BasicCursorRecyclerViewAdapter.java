package com.gsbelarus.gedemin.skeleton.base.view.adapter;

import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.provider.BaseColumns;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.gsbelarus.gedemin.skeleton.base.view.adapter.datasource.CursorRecyclerAdapterDataSource;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.item.CursorRecyclerItemViewTypeModel;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.item.ItemViewTypes;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.viewhandler.CursorRecyclerAdapterViewHandler;

// TODO: 1. extends CursorAdapter  2. <T>

public class BasicCursorRecyclerViewAdapter extends BasicRecyclerViewAdapter {


    public BasicCursorRecyclerViewAdapter(@Nullable Cursor cursor,
                                           @LayoutRes int layout,
                                           String[] from,
                                           int[] to) {

        CursorRecyclerAdapterDataSource dataSource = new CursorRecyclerAdapterDataSource(cursor);
        setAdapterDataSource(dataSource);

        CursorRecyclerAdapterViewHandler viewHandler = new CursorRecyclerAdapterViewHandler(new CursorRecyclerItemViewTypeModel(layout, from, to));
        setAdapterViewHandler(viewHandler);

        //registerObservers(getDataCursor());
    }

    public Cursor getDataCursor() {
        return getAdapterDataSource().getDataCursor();
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

}
package com.gsbelarus.gedemin.skeleton.base.view.adapter.datasource;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.gsbelarus.gedemin.skeleton.base.view.adapter.item.ItemViewTypes;


public class CursorRecyclerAdapterDataSource implements RecyclerAdapterDataSource<Cursor> {

    private Cursor dataCursor;
    private boolean dataValid;
    private int idColumnIndex;


    public CursorRecyclerAdapterDataSource() {}

    public CursorRecyclerAdapterDataSource(@Nullable Cursor dataCursor) {
        this.dataCursor = dataCursor;

        dataValid = this.dataCursor != null;
        idColumnIndex = dataValid ? this.dataCursor.getColumnIndexOrThrow(BaseColumns._ID) : -1;
    }

    @Override
    public long getItemId(int position) {
        if (getItem(position) != null) {
            return dataCursor.getLong(idColumnIndex);
        }
        return RecyclerView.NO_ID;
    }

    @Override
    public int getItemCount() {
        if (dataValid && dataCursor != null) {
            return dataCursor.getCount();
        }
        return 0;
    }

    @Nullable
    @Override
    public Cursor getItem(int position) {
        if (dataValid && dataCursor != null && dataCursor.moveToPosition(position)) {
            return dataCursor;
        } else {
            return null;
        }
    }

    @Override
    public int getViewType(int position) {
        return ItemViewTypes.DEFAULT_VIEW_TYPE;
    }

    //TODO
    @Nullable
    public synchronized Cursor swapCursor(@Nullable Cursor newCursor) {
        if (newCursor == dataCursor) return newCursor;

        Cursor oldCursor = dataCursor;
        dataCursor = newCursor;

        if (dataCursor != null) {
            idColumnIndex = newCursor.getColumnIndexOrThrow(BaseColumns._ID);
            dataValid = true;
        } else {
            idColumnIndex = -1;
            dataValid = false;
        }

        return oldCursor;
    }

    public void setDataValid(boolean dataValid) {
        this.dataValid = dataValid;
    }

    public boolean isDataValid() {
        return dataValid;
    }

    public Cursor getDataCursor() {
        return dataCursor;
    }
}

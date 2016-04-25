package com.gsbelarus.gedemin.skeleton.base;

import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


public abstract class BaseCursorRecyclerViewAdapter
        extends RecyclerView.Adapter<BaseCursorRecyclerViewAdapter.BaseCursorItemViewHolder> {

    public final String TAG = this.getClass().getCanonicalName();

    public static final int DEFAULT_VIEW_TYPE = 0;

    private Cursor dataCursor;
    private int idColumnIndex;
    private boolean dataValid;
    private DataSetObserver dataSetObserver;
    private ContentObserver contentObserver;
    private final DataSetObservable dataSetObservable = new DataSetObservable();


    public BaseCursorRecyclerViewAdapter(@Nullable Cursor dataCursor) {
        this.dataCursor = dataCursor;
        dataValid = dataCursor != null;
        idColumnIndex = dataValid ? this.dataCursor.getColumnIndexOrThrow("_id") : -1;

        registerObservers(dataCursor);
    }

    @Override
    public abstract BaseCursorItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(BaseCursorItemViewHolder viewHolder, int position) {
        if (!dataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (getItemWithMove(position) == null) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        viewHolder.bind(dataCursor); //onBindViewHolder(viewHolder, dataCursor);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        if (getItemWithMove(position) != null) {
            return dataCursor.getLong(idColumnIndex);
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        if (dataValid && dataCursor != null) {
            return dataCursor.getCount();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemWithMove(position) != null) {
            return getItemViewType(dataCursor);
        } else {
            return 0;
        }
    }

    public int getItemViewType(Cursor cursor) {
        return DEFAULT_VIEW_TYPE;
    }

    @Nullable
    public Cursor getItemWithMove(int position) {
        if (dataValid && dataCursor != null && dataCursor.moveToPosition(position)) {
            return dataCursor;
        } else {
            return null;
        }
    }

    @Nullable
    public Cursor getDataCursor() {
        return dataCursor;
    }

    protected void registerObservers(@Nullable Cursor cursor) {
        if (dataSetObserver == null) {
            dataSetObserver = new CursorAdapterDataSetObserver();
        }
        if (contentObserver == null) {
            contentObserver = new CursorAdapterContentObserver(new Handler());
        }

        if (cursor != null) {
            cursor.registerDataSetObserver(dataSetObserver);
            cursor.registerContentObserver(contentObserver);
        }
    }

    protected void unregisterObservers(@Nullable Cursor cursor) {
        if (cursor != null) {
            if (dataSetObserver != null) {
                cursor.unregisterDataSetObserver(dataSetObserver);
            }
            if (contentObserver != null) {
                cursor.unregisterContentObserver(contentObserver);
            }
        }
    }

    @Nullable
    public Cursor swapCursor(@Nullable Cursor newCursor) {
        if (newCursor == dataCursor) {
            return null;
        }

        final Cursor oldCursor = dataCursor;
        unregisterObservers(oldCursor);

        dataCursor = newCursor;
        if (dataCursor != null) {
            registerObservers(dataCursor);

            idColumnIndex = newCursor.getColumnIndexOrThrow("_id");
            dataValid = true;

            if (oldCursor != null) notifyItemRangeRemoved(0, oldCursor.getCount());
            notifyItemRangeInserted(0, newCursor.getCount());

        } else {
            idColumnIndex = -1;
            dataValid = false;

            notifyDataSetChanged();
        }

        return oldCursor;
    }

    protected void onContentChanged() {
        //TODO swapCursor
    }

//////// test
    protected void notifyDataSetInvalidated() {
        dataSetObservable.notifyInvalidated(); //TODO
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        dataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        dataSetObservable.unregisterObserver(observer);
    }
////////


    private class CursorAdapterDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            super.onChanged();

            dataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();

            dataValid = false;
            notifyDataSetInvalidated(); //notifyDataSetChanged();
        }
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


    public static abstract class BaseCursorItemViewHolder extends RecyclerView.ViewHolder {

        public BaseCursorItemViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bind(Cursor cursor);
    }

}
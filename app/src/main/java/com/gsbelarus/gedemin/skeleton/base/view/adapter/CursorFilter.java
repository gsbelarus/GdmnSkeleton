package com.gsbelarus.gedemin.skeleton.base.view.adapter;

import android.database.Cursor;
import android.widget.Filter;

public class CursorFilter extends Filter {

    public CursorFilterClient mCursorFilterClient;

    public CursorFilter(CursorFilterClient mCursorFilterClient) {
        this.mCursorFilterClient = mCursorFilterClient;
    }

    public interface CursorFilterClient {
        CharSequence convertToString(Cursor cursor);

        Cursor runQueryOnBackgroundThread(CharSequence constraint);

        Cursor getDataCursor();

        void updateCursor(Cursor cursor);
    }


    @Override
    public CharSequence convertResultToString(Object resultValue) {
        return mCursorFilterClient.convertToString((Cursor) resultValue);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        Cursor cursor = mCursorFilterClient.runQueryOnBackgroundThread(constraint);
        FilterResults results = new FilterResults();
        if (cursor != null) {
            results.count = cursor.getCount();
            results.values = cursor;
        } else {
            results.count = 0;
            results.values = null;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        Cursor oldCursor = mCursorFilterClient.getDataCursor();
        if (constraint != null && results.values != oldCursor) {
            mCursorFilterClient.updateCursor((Cursor) results.values);
        }
    }
}

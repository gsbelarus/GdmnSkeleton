package com.gsbelarus.gedemin.skeleton.base.view.adapter;

import android.database.Cursor;
import android.widget.Filter;


public class CursorFilter extends Filter {

    public interface CursorFilterClient {

        CharSequence convertToString(Cursor cursor);

        Cursor runQueryOnBackgroundThread(CharSequence constraint);

        Cursor getDataCursor();

        void setFiltratedCursor(Cursor cursor);
    }

    private CursorFilterClient cursorFilterClient;


    public CursorFilter(CursorFilterClient cursorFilterClient) {
        this.cursorFilterClient = cursorFilterClient;
    }

    @Override
    public CharSequence convertResultToString(Object resultValue) {
        return cursorFilterClient.convertToString((Cursor) resultValue);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        Cursor cursor = cursorFilterClient.runQueryOnBackgroundThread(constraint);
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
        Cursor oldCursor = cursorFilterClient.getDataCursor();
        if (constraint != null && results.values != oldCursor) {
            cursorFilterClient.setFiltratedCursor((Cursor) results.values);
        }
    }
}

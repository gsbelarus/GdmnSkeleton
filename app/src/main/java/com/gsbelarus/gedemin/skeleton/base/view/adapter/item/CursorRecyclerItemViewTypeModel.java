package com.gsbelarus.gedemin.skeleton.base.view.adapter.item;

import android.database.Cursor;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;


public class CursorRecyclerItemViewTypeModel {

    private final int viewTypeId;

    @LayoutRes
    private final int layoutResource;

    private  String[] originalFrom;
    @IdRes
    protected int[] to;
    private int[] from;


    public CursorRecyclerItemViewTypeModel(@LayoutRes int layoutResource, String[] originalFrom, int[] to) {
        this(ItemViewTypes.DEFAULT_VIEW_TYPE, layoutResource, originalFrom, to);
    }

    public CursorRecyclerItemViewTypeModel(int viewTypeId, @LayoutRes int layoutResource, String[] originalFrom, int[] to) {
        this.viewTypeId = viewTypeId;
        this.layoutResource = layoutResource;
        this.originalFrom = originalFrom;
        this.to = to;
    }

    public int[] getFrom(@NonNull Cursor cursor) {
        if (from != null) return from;

        findColumns(cursor);
        return from;
    }

    public String[] getOriginalFrom(@NonNull Cursor cursor) {
        if (originalFrom != null) return originalFrom;

        findColumns(cursor);
        return originalFrom;
    }

    private void findColumns(@NonNull Cursor cursor) {
        if(originalFrom==null)  {
            originalFrom = cursor.getColumnNames();
        }

        int[] columns = new int[originalFrom.length];

        for (int i = 0; i < originalFrom.length; i++) {
            String columnName = originalFrom[i];
            columns[i] = cursor.getColumnIndex(columnName);
        }

        from = columns;
    }

    // generated getters

    public int getViewTypeId() {
        return viewTypeId;
    }

    public int getLayoutResource() {
        return layoutResource;
    }

    public String[] getOriginalFrom() {
        return originalFrom;
    }

    public int[] getTo() {
        return to;
    }

}

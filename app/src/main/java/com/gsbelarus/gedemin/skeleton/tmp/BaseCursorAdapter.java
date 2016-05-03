package com.gsbelarus.gedemin.skeleton.tmp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


abstract public class BaseCursorAdapter extends CursorAdapter {

    protected final Context context;
    protected final LayoutInflater layoutInflater;

    public BaseCursorAdapter(Context context, Cursor c) {
        this(context, c, false);
    }

    public BaseCursorAdapter(Context context, Cursor c, boolean autoRequery) {  //Cursor c, int flags
        super(context, c, autoRequery);

        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public int getTotalCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public abstract View newView(Context context, Cursor cursor, ViewGroup parent);

    public abstract void bindView(View view, Context context, Cursor cursor);
}
package com.gsbelarus.gedemin.skeleton.base.data.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;


public abstract class BaseSimpleCursorLoader extends CursorLoader {

    private final ForceLoadContentObserver observer;


    public BaseSimpleCursorLoader(Context context) {
        super(context);

        observer = new ForceLoadContentObserver();
    }

    public BaseSimpleCursorLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);

        observer = new ForceLoadContentObserver();
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = loadCursor();

        if (cursor != null) {
            //cursor.getCount(); // ensure the cursor window is filled
            cursor.registerContentObserver(observer);
        }

        return cursor;
    }

    public abstract Cursor loadCursor();

}
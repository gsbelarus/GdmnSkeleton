package com.gsbelarus.gedemin.skeleton.base.data.loader;

import android.content.Context;
import android.database.Cursor;

import com.gsbelarus.gedemin.skeleton.base.data.BaseDatabaseManager;


public class BasicTableCursorLoader extends BaseSimpleCursorLoader {

    private final String tableName;
    private final BaseDatabaseManager databaseManager;

    public BasicTableCursorLoader(final Context context,
                                  final BaseDatabaseManager databaseManager,
                                  final String tableName,
                                  final String[] projection,
                                  final String selection,
                                  final String[] selectionArgs,
                                  final String sortOrder) {
        super(context, null, projection, selection, selectionArgs, sortOrder);

        this.databaseManager = databaseManager;
        this.tableName = tableName;
    }

    @Override
    public Cursor loadCursor() {
        return  databaseManager.select(tableName, getProjection(), getSelection(), getSelectionArgs(), getSortOrder());
    }

}
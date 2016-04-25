package com.gsbelarus.gedemin.skeleton.base;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import com.gsbelarus.gedemin.skeleton.base.db.BaseDatabaseManager;


public class CommonCursorLoader extends CursorLoader {

    protected String tableName;
    protected BaseDatabaseManager databaseManager;

    public CommonCursorLoader(Context context, BaseDatabaseManager databaseManager, String tableName, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, null, projection, selection, selectionArgs, sortOrder);

        this.databaseManager = databaseManager;
        this.tableName = tableName;
    }

    @Override
    public Cursor loadInBackground() {
        return databaseManager.select(tableName, getProjection(), getSelection(), getSelectionArgs(), getSortOrder());
    }

}
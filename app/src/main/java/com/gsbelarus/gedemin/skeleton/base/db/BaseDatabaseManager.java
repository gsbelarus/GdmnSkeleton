package com.gsbelarus.gedemin.skeleton.base.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


abstract public class BaseDatabaseManager implements DatabaseOpenHelper.DBOpenHelperCallback {

    protected int dbVersion = 0;
    protected String dbName = "";

    private final Context appContext;
    private DatabaseOpenHelper databaseOpenHelper = null;
    protected SQLiteDatabase db = null;

    //private static final Object lockObject = new Object();


    protected BaseDatabaseManager(Context сontext, String dbName, int dbVersion) {
        this.dbName = dbName;
        this.dbVersion = dbVersion;
        this.appContext = сontext.getApplicationContext();

        databaseOpenHelper = new DatabaseOpenHelper(appContext, dbName, dbVersion, this);
    }

    public boolean isOpen() {
        return (db != null && db.isOpen());
    }

    public synchronized void open() {                   //TODO check
        databaseOpenHelper.addConnection();

        if(!isOpen()) {
            //synchronized (lockObject) {
                db = databaseOpenHelper.getWritableDatabase();
            //}
        }
    }

    public synchronized boolean close() {
        databaseOpenHelper.removeConnection();

        if(databaseOpenHelper.getConnectCounter() == 0) {
            //synchronized (lockObject) {
                if (db.inTransaction()) db.endTransaction();
                databaseOpenHelper.close();
                db = null;
            //}
            return true;
        }
        return false;
    }


    public void beginTransaction() {
        db.beginTransaction();
    }

    public void transactionSuccessful() {
        db.setTransactionSuccessful();
    }

    public void endTransaction() {
        db.endTransaction();
    }


    @Nullable
    public Cursor select(String tableName, String[] columnNames, String selection, String[] selectionArgs, String order) {
        try {
            return db.query(tableName, columnNames, selection, selectionArgs, null, null, order);

        } catch (Exception e) {
            Log.e("BaseDatabaseManager", "Error selecting: " + SQLiteQueryBuilder.buildQueryString(false, tableName, columnNames, selection, null, null, order, null), e);
            return null;
        }
    }

    @Nullable
    public Long insert(String tableName, String nullColumnHack, ContentValues contentValues) {
        try {
            return db.insertOrThrow(tableName, nullColumnHack, contentValues);

        } catch (SQLException e) {  //TODO проброс
            Log.e("BaseDatabaseManager", "Error inserting " + tableName + ": " + contentValues, e);
//          close();  //TODO
            return null;
        }
    }

    @Nullable
    public Integer update(String tableName, ContentValues contentValues, String whereClause, String[] whereArgs) {
        try {
            return db.update(tableName, contentValues, whereClause, whereArgs);

        } catch (Exception e) {
            Log.e("BaseDatabaseManager", "Error updating " + tableName + ": " + contentValues, e);
            return null;
        }
    }

    @Nullable
    public Long replace(String tableName, String nullColumnHack, ContentValues initialValues) {
        try {
            return db.replaceOrThrow(tableName, nullColumnHack, initialValues);

        } catch (SQLException e) {
            Log.e("BaseDatabaseManager", "Error replacing " + tableName + ": " + initialValues, e);
            return null;
        }
    }

    @Nullable
    public Integer delete(String tableName, String whereClause, String[] whereArgs) {
        try {
            return db.delete(tableName, whereClause, whereArgs);

        } catch (SQLException e) {
            Log.e("BaseDatabaseManager", "Error deleting: DELETE FROM " + tableName + (!TextUtils.isEmpty(whereClause) ? " WHERE " + whereClause : ""), e);
            return null;
        }
    }

    public void deleteDatabase(Context appContext) {
        appContext.deleteDatabase(dbName);
    }


    ////////////// TODO


    public void dropAll() {
        dropAll(db);
    }

    private void dropAll(SQLiteDatabase db) {
        List<String> tables = new ArrayList<>();
        Cursor cursor = db.query("sqlite_master", null, "type = ?", new String[]{"table"}, null, null, null);

        while (cursor.moveToNext()) {
            String tableName = cursor.getString(1);
            if (!tableName.equals("android_metadata") && !tableName.equals("sqlite_sequence"))
                tables.add(tableName);
        }
        cursor.close();

        for (String tableName : tables) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }
    }

    public Map<String, String> getRow(String table, long id) {
        Map<String, String> rows = new HashMap<>();
        Cursor cursor = db.query(table, null, BaseColumns._ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                rows.put(cursor.getColumnName(i), cursor.getString(i));
            }
        }
        cursor.close();
        return rows;
    }

}
package com.gsbelarus.gedemin.skeleton.base.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteTransactionListener;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.gsbelarus.gedemin.skeleton.base.BaseApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseDatabaseManager {

    private final String TAG = this.getClass().getCanonicalName();

    //private static BaseDatabaseManager instance = null; //TODO singleton factory

    private static String CONTENT_URI_AUTHORITY = "gdmn";
    private static String CONTENT_URI_SCHEME = "content";

    protected int dbVersion;
    protected String dbName;

    private final Context appContext;
    private BasicDatabaseOpenHelper basicDatabaseOpenHelper = null;
    protected SQLiteDatabase db = null;


    protected BaseDatabaseManager(Context сontext, String dbName, int dbVersion) {
        this.dbName = dbName;
        this.dbVersion = dbVersion;
        this.appContext = сontext.getApplicationContext();
    }

    @NonNull
    protected abstract BasicDatabaseOpenHelper.Delegate getDbOpenHelperImpl();

    public boolean isOpen() {
        return (db != null && db.isOpen());
    }

    public synchronized void open() {                   //TODO check
        if (basicDatabaseOpenHelper == null) {
            basicDatabaseOpenHelper = new BasicDatabaseOpenHelper(appContext, dbName, dbVersion, getDbOpenHelperImpl());
        }
        basicDatabaseOpenHelper.addConnection();

        if(!isOpen()) {
            db = basicDatabaseOpenHelper.getWritableDatabase();
        }
    }

    public synchronized boolean close() {
        basicDatabaseOpenHelper.removeConnection();

        if(basicDatabaseOpenHelper.getConnectCounter() == 0) {
            if (db.inTransaction()) db.endTransaction();
            basicDatabaseOpenHelper.close();
            db = null;

            return true;
        }
        return false;
    }


    public void beginTransaction() {
        db.beginTransaction();
    }

    public boolean inTransaction() {
        return db.inTransaction();
    }

    public void setTransactionSuccessful() {
        db.setTransactionSuccessful();
    }

    public void beginTransactionNonExclusive() {
        db.beginTransactionNonExclusive();
    }

    public void beginTransactionWithListener(SQLiteTransactionListener transactionListener) {
        db.beginTransactionWithListener(transactionListener);
    }

    public void beginTransactionWithListenerNonExclusive(SQLiteTransactionListener transactionListener) {
        db.beginTransactionWithListenerNonExclusive(transactionListener);
    }

    public void endTransaction() {
        db.endTransaction();
    }

    public void commitTransaction() {
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void cancelTransaction() {
        db.endTransaction();
    }

    public int getVersion() {
        return db.getVersion();
    }

    public Cursor setNotifier(Cursor cursor) {
        cursor.setNotificationUri(appContext.getContentResolver(),
                new Uri.Builder()
                        .scheme(CONTENT_URI_SCHEME)
                        .authority(CONTENT_URI_AUTHORITY)
                        .build());
        return cursor;
    }

    public void notifyDataChanged() {
        appContext.getContentResolver().notifyChange(
                new Uri.Builder()
                        .scheme(CONTENT_URI_SCHEME)
                        .authority(CONTENT_URI_AUTHORITY)
                        .build(),
                null, false);
    }


    @Nullable
    public Cursor select(String tableName, String[] columnNames, String selection, String[] selectionArgs, String order) {
        try {
            return setNotifier(db.query(tableName, columnNames, selection, selectionArgs, null, null, order));
        } catch (Exception e) {
            Log.e(TAG, "Error selecting: " + SQLiteQueryBuilder.buildQueryString(false, tableName, columnNames, selection, null, null, order, null), e);

            // Tracking exception
            BaseApplication.getInstance().trackException(e);

            return null;
        }
    }

    @Nullable
    public Long insert(String tableName, String nullColumnHack, ContentValues contentValues) {
        try {
            return db.insertOrThrow(tableName, nullColumnHack, contentValues);

        } catch (SQLException e) {
            Log.e(TAG, "Error inserting " + tableName + ": " + contentValues, e);

            // Tracking exception
            BaseApplication.getInstance().trackException(e);

            return null;
        }
    }

    @Nullable
    public Integer update(String tableName, ContentValues contentValues, String whereClause, String[] whereArgs) {
        try {
            return db.update(tableName, contentValues, whereClause, whereArgs);

        } catch (Exception e) {
            Log.e(TAG, "Error updating " + tableName + ": " + contentValues, e);

            // Tracking exception
            BaseApplication.getInstance().trackException(e);

            return null;
        }
    }

    @Nullable
    public Long replace(String tableName, String nullColumnHack, ContentValues initialValues) {
        try {
            return db.replaceOrThrow(tableName, nullColumnHack, initialValues);

        } catch (SQLException e) {
            Log.e(TAG, "Error replacing " + tableName + ": " + initialValues, e);

            // Tracking exception
            BaseApplication.getInstance().trackException(e);

            return null;
        }
    }

    @Nullable
    public Integer delete(String tableName, String whereClause, String[] whereArgs) {
        try {
            return db.delete(tableName, whereClause, whereArgs);

        } catch (SQLException e) {
            Log.e(TAG, "Error deleting: DELETE FROM " + tableName + (!TextUtils.isEmpty(whereClause) ? " WHERE " + whereClause : ""), e);

            // Tracking exception
            BaseApplication.getInstance().trackException(e);

            return null;
        }
    }

    public void deleteDatabase(Context appContext) {
        appContext.deleteDatabase(dbName);
    }


    //////////////


    public void dropAll() {
        dropAll(db);
    }

    protected void dropAll(SQLiteDatabase db) {
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
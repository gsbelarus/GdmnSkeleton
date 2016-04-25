package com.gsbelarus.gedemin.skeleton.base.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;


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
            Log.e("BaseDatabaseManager", "exception: "+ e.getMessage());
            return null;
        }
    }


    // insert update delete


//
//    public void deleteDatabase() {
//        appContext.deleteDatabase(dbName);
//    }
}

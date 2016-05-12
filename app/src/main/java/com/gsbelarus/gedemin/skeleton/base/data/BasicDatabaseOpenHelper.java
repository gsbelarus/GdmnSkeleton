package com.gsbelarus.gedemin.skeleton.base.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

public class BasicDatabaseOpenHelper extends SQLiteOpenHelper {

    private DBOpenHelperCallback dbOpenHelperCallback = null;
    private AtomicInteger connectCounter = new AtomicInteger(0);

    public BasicDatabaseOpenHelper(Context context, String dbName, int dbVersion, DBOpenHelperCallback dbOpenHelperCallback) {
        super(context, dbName, null, dbVersion);

        this.dbOpenHelperCallback = dbOpenHelperCallback;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (dbOpenHelperCallback != null) {
            dbOpenHelperCallback.onCreateDatabase(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (dbOpenHelperCallback != null) {
            dbOpenHelperCallback.onUpgradeDatabase(db, oldVersion, newVersion);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO
        if (newVersion != 1) super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);

        if (dbOpenHelperCallback != null) {
            dbOpenHelperCallback.onConfigure(db);
        }
    }

    public void addConnection() {
        connectCounter.incrementAndGet();
    }

    public void removeConnection() {
        connectCounter.decrementAndGet();
    }

    public int getConnectCounter() {
        return connectCounter.get();
    }

    public interface DBOpenHelperCallback {
        void onCreateDatabase(SQLiteDatabase db);

        void onUpgradeDatabase(SQLiteDatabase db, int oldVersion, int newVersion);

        void onConfigure(SQLiteDatabase db);
    }

}

package com.gsbelarus.gedemin.skeleton.base.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;


public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public interface DBOpenHelperCallback {
        void onCreateDatabase(SQLiteDatabase db);
        void onUpgradeDatabase(SQLiteDatabase db);
        //todo ondelete
    }


    private DBOpenHelperCallback dbOpenHelperCallback = null;
    private AtomicInteger connectCounter = new AtomicInteger(0);


    public DatabaseOpenHelper(Context context, String dbName, int dbVersion, DBOpenHelperCallback dbOpenHelperCallback) {
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
            dbOpenHelperCallback.onUpgradeDatabase(db);
        }

        onCreate(db);
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

}

package com.gsbelarus.gedemin.skeleton.base.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.concurrent.atomic.AtomicInteger;


public final class BasicDatabaseOpenHelper extends SQLiteOpenHelper {

    private AtomicInteger connectCounter = new AtomicInteger(0);
    private final BaseDatabaseOpenHelperDelegate delegate;


    public BasicDatabaseOpenHelper(Context context, String dbName, int dbVersion, @NonNull BaseDatabaseOpenHelperDelegate delegate) {
        super(context, dbName, null, dbVersion);

        this.delegate = delegate;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        delegate.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        delegate.onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        delegate.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        delegate.onConfigure(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        delegate.onOpen(db);
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

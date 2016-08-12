package com.gsbelarus.gedemin.skeleton.base.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.concurrent.atomic.AtomicInteger;

public final class BasicDatabaseOpenHelper extends SQLiteOpenHelper {

    private AtomicInteger connectCounter = new AtomicInteger(0);
    private final Delegate dbOpenImpl;

    public BasicDatabaseOpenHelper(Context context, String dbName, int dbVersion, @NonNull Delegate dbOpenImpl) {
        super(context, dbName, null, dbVersion);

        this.dbOpenImpl = dbOpenImpl;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        dbOpenImpl.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dbOpenImpl.onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dbOpenImpl.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        dbOpenImpl.onConfigure(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        dbOpenImpl.onOpen(db);
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

    public abstract static class Delegate {

        public abstract void onCreate(SQLiteDatabase db);

        public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            throw new SQLiteException("Can't downgrade database from version " + oldVersion + " to " + newVersion);
        }

        public void onConfigure(SQLiteDatabase db) {}

        public void onOpen(SQLiteDatabase db) {}

        // final Object methods

        protected final Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        public final boolean equals(Object o) {
            return super.equals(o);
        }

        protected final void finalize() throws Throwable {
            super.finalize();
        }

        public final int hashCode() {
            return super.hashCode();
        }

        public final String toString() {
            return super.toString();
        }
    }
}

package com.gsbelarus.gedemin.skeleton.base.data;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;


public abstract class BaseDatabaseOpenHelperDelegate {

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

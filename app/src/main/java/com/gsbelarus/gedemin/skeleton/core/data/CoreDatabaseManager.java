package com.gsbelarus.gedemin.skeleton.core.data;

import android.accounts.Account;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gsbelarus.gedemin.skeleton.base.data.BaseDatabaseManager;
import com.gsbelarus.gedemin.skeleton.base.data.BasicDatabaseOpenHelper;
import com.gsbelarus.gedemin.skeleton.base.data.SQLiteDataType.SQLiteStorageTypes;
import com.gsbelarus.gedemin.skeleton.core.data.CoreContract.TableLogChanges;
import com.gsbelarus.gedemin.skeleton.core.data.CoreContract.TableSyncSchema;
import com.gsbelarus.gedemin.skeleton.core.data.CoreContract.TableSyncSchemaVersion;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CoreDatabaseManager extends BaseDatabaseManager {

    public interface Callback {
        void onCreateDatabase(CoreDatabaseManager coreDatabaseManager);

        void onUpgradeDatabase(CoreDatabaseManager coreDatabaseManager, int oldVersion, int newVersion);
    }

    private static final int DEFAULT_DATABASE_VERSION = 1;
    private static final String EMPTY_DATABASE_NAME = "empty";

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String TRIGGER_LOG_CHANGES_INSERT = "trigger_%s_insert";
    private static final String TRIGGER_LOG_CHANGES_UPDATE = "trigger_%s_update";
    private static final String TRIGGER_LOG_CHANGES_DELETE = "trigger_%s_delete";
    private static final String TRIGGER_LOG_CHANGES_DELETE_INSERTED = "trigger_%s_delete_inserted";
    private static final String TRIGGER_LOG_CHANGES_SYNC_INSERT = "trigger_%s_sync_insert";
    private static final String TRIGGER_LOG_CHANGES_SYNC_UPDATE = "trigger_%s_sync_update";
    private static final String TRIGGER_LOG_CHANGES_SYNC_DELETE = "trigger_%s_sync_delete";

    private static final String COMMA_SEP = ", ";

    private static final String CREATE_TABLE_LOG_CHANGES =
            "CREATE TABLE " + TableLogChanges.TABLE_NAME + " (" +
                    TableLogChanges._ID + SQLiteStorageTypes.INTEGER + "PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    TableLogChanges.COLUMN_TABLE_NAME + SQLiteStorageTypes.TEXT + "NOT NULL" + COMMA_SEP +
                    TableLogChanges.COLUMN_INTERNAL_ID + SQLiteStorageTypes.INTEGER + "NOT NULL UNIQUE" + COMMA_SEP +
                    TableLogChanges.COLUMN_EXTERNAL_ID + SQLiteStorageTypes.INTEGER + "NOT NULL" + COMMA_SEP +
                    TableLogChanges.COLUMN_CHANGE_TYPE + SQLiteStorageTypes.INTEGER + "NOT NULL" +
                    " )";
    private static final String CREATE_TABLE_LOG_SYNC =
            "CREATE TABLE " + TableSyncSchemaVersion.TABLE_NAME + " (" +
                    TableSyncSchemaVersion._ID + SQLiteStorageTypes.INTEGER + "PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    TableSyncSchemaVersion.COLUMN_VERSION_DB + SQLiteStorageTypes.INTEGER + "NOT NULL" + COMMA_SEP +
                    TableSyncSchemaVersion.COLUMN_SYNC_DATE + SQLiteStorageTypes.TEXT + "NOT NULL" +
                    " )";
    private static final String CREATE_TABLE_LOG_SYNC_TOKENS =
            "CREATE TABLE " + TableSyncSchema.TABLE_NAME + " (" +
                    TableSyncSchema._ID + SQLiteStorageTypes.INTEGER + "PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    TableSyncSchema.COLUMN_TABLE_NAME + SQLiteStorageTypes.TEXT + "NOT NULL" + COMMA_SEP +
                    TableSyncSchema.COLUMN_SYNC_TOKEN + SQLiteStorageTypes.TEXT + COMMA_SEP +
                    TableSyncSchema.COLUMN_SYNC_SCHEMA_VERSION_KEY + SQLiteStorageTypes.INTEGER + "NOT NULL" + COMMA_SEP +
                    "FOREIGN KEY(" + TableSyncSchema.COLUMN_SYNC_SCHEMA_VERSION_KEY + ") REFERENCES " + TableSyncSchemaVersion.TABLE_NAME + "(" + TableSyncSchemaVersion._ID + ")" +
                    " )";

    private static List<CoreDatabaseManager> instances = new ArrayList<>();

    private Account account;

    private BasicDatabaseOpenHelper.Delegate dbOpenImpl = new BasicDatabaseOpenHelper.Delegate() {

        @Override
        public void onCreate(SQLiteDatabase db) {
            Logger.d();
            db.execSQL(CREATE_TABLE_LOG_CHANGES);
            db.execSQL(CREATE_TABLE_LOG_SYNC);
            db.execSQL(CREATE_TABLE_LOG_SYNC_TOKENS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            throw new SQLiteException("Upgrade is not supported " + oldVersion + " to " + newVersion);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion != 1) super.onDowngrade(db, oldVersion, newVersion);
        }

        @Override
        public void onConfigure(SQLiteDatabase db) {
            super.onConfigure(db);

            db.enableWriteAheadLogging();
            if (!db.isReadOnly()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    db.setForeignKeyConstraintsEnabled(true);
                } else {
                    db.execSQL("PRAGMA foreign_keys = ON");
                }
            }
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);

            notifyVersionDB(db);
        }
    };

    private CoreDatabaseManager(Context context, Account account) {
        super(context, account.name + ".sqlite", DEFAULT_DATABASE_VERSION);
        this.account = account;
    }

    @NotNull
    public static synchronized CoreDatabaseManager getInstance(Context context, Account account) {
        Account emptyAccount = new Account(EMPTY_DATABASE_NAME, "empty_type");
        if (account == null) {
            account = emptyAccount;
        } else {
            deleteInstance(emptyAccount);
        }
        for (CoreDatabaseManager instance : instances) {
            if (instance.account.equals(account)) {
                return instance;
            }
        }
        CoreDatabaseManager instance = new CoreDatabaseManager(context, account);
        instances.add(instance);
        return instance;
    }

    public static synchronized void deleteInstance(Account account) {
        Iterator<CoreDatabaseManager> iterator = instances.iterator();
        while (iterator.hasNext()) {
            CoreDatabaseManager instance = iterator.next();
            if (instance.account.equals(account)) {
                iterator.remove();
                break;
            }
        }
    }

    public static String getDateTime(Date date) {
        return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(date);
    }

    @Nullable
    public static Date getDateTime(String date) {
        try {
            return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(date);
        } catch (Exception e) {
            Logger.d(e.getMessage());
        }
        return null;
    }

    @NonNull
    @Override
    protected BasicDatabaseOpenHelper.Delegate getDbOpenDelegate() {
        return dbOpenImpl;
    }

    private void notifyVersionDB(SQLiteDatabase db) {
        db.setVersion(getMaxSchemaVersion(db));
        Logger.d("local db version: " + db.getVersion());
    }

    private int getMaxSchemaVersion(SQLiteDatabase db) {
        int version = db.getVersion();
        try {
            Cursor cursor = db.query(TableSyncSchemaVersion.TABLE_NAME,
                    new String[]{"MAX(" + TableSyncSchemaVersion.COLUMN_VERSION_DB + ")"}, null, null, null, null, null);
            if (cursor.moveToNext()) {
                version = cursor.getInt(0);
            }
            cursor.close();
            if (version == 0) version = db.getVersion();
        } catch (SQLiteException e) {
            Logger.d(e.getMessage());
        }
        return version;
    }

    protected Map<String, String> setVersion(int version, @NonNull Callback callback) {
        Logger.d("local db version: " + getVersion(), "server db version: " + version);
        if (getVersion() < version) {
            if (getVersion() == 1) callback.onCreateDatabase(this);
            else callback.onUpgradeDatabase(this, getVersion(), version);
        }
        if (getVersion() > version) {
            throw new SQLiteException("Can't downgrade database from version " +
                    getVersion() + " to " + version);
        }
        Map<String, String> tokens = getLastTokens();
        ContentValues cv = new ContentValues();
        cv.put(TableSyncSchemaVersion.COLUMN_VERSION_DB, version);
        cv.put(TableSyncSchemaVersion.COLUMN_SYNC_DATE, getDateTime(new Date()));
        db.insertOrThrow(TableSyncSchemaVersion.TABLE_NAME, null, cv);
        notifyVersionDB(db);
        return tokens;
    }

    private Map<String, String> getLastTokens() {
        Map<String, String> values = new HashMap<>();
        Cursor cursor = db.rawQuery(
                "SELECT s." + TableSyncSchema.COLUMN_TABLE_NAME + ", s." + TableSyncSchema.COLUMN_SYNC_TOKEN +
                        " FROM " + TableSyncSchema.TABLE_NAME + " s " +
                        " JOIN (" +
                        "   SELECT " + TableSyncSchemaVersion._ID +
                        "   FROM " + TableSyncSchemaVersion.TABLE_NAME + " ORDER BY " + TableSyncSchemaVersion._ID + " DESC LIMIT 1" +
                        " ) v " +
                        " ON v." + TableSyncSchemaVersion._ID + " = s." + TableSyncSchema.COLUMN_SYNC_SCHEMA_VERSION_KEY,
                null);
        while (cursor.moveToNext()) {
            values.put(cursor.getString(0), cursor.getString(1));
        }
        cursor.close();
        return values;
    }

    protected void putToken(String tableName, String token) {
        Cursor cursor = db.query(TableSyncSchemaVersion.TABLE_NAME,
                new String[]{"MAX(" + BaseColumns._ID + ")"}, null, null, null, null, null);
        if (cursor.moveToNext()) {
            ContentValues cv = new ContentValues();
            cv.put(TableSyncSchema.COLUMN_SYNC_SCHEMA_VERSION_KEY, cursor.getLong(0));
            cv.put(TableSyncSchema.COLUMN_TABLE_NAME, tableName);
            cv.put(TableSyncSchema.COLUMN_SYNC_TOKEN, token);
            db.insertOrThrow(TableSyncSchema.TABLE_NAME, null, cv);
        }
        cursor.close();
    }

    public void recreateDatabase() {
        dropAll();
        db.setVersion(1);
        dbOpenImpl.onCreate(db);
    }

    public void createTable(String tableName, Map<String, String> columns, String externalId) throws IllegalArgumentException {
        String sql = "CREATE TABLE " + tableName + " (" +
                BaseColumns._ID + SQLiteStorageTypes.INTEGER + "PRIMARY KEY AUTOINCREMENT" + COMMA_SEP;

        int i = 0;
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            sql += entry.getKey() + " " + entry.getValue();
            if (entry.getKey().equals(externalId)) sql += " UNIQUE";
            if (i != columns.size() - 1) sql += COMMA_SEP;
            i++;
        }

        sql += ")";

        Logger.d(sql);
        db.execSQL(sql);

        createLogChangesTriggers(tableName, externalId);
    }

    public void createLogChangesTriggers(String tableName, String externalId) {
        String insertTrigger =
                "CREATE TRIGGER " + String.format(TRIGGER_LOG_CHANGES_INSERT, tableName) + " AFTER INSERT ON " + tableName +
                        " BEGIN" +
                        " INSERT INTO " + TableLogChanges.TABLE_NAME + " (" +
                        TableLogChanges.COLUMN_TABLE_NAME + COMMA_SEP +
                        TableLogChanges.COLUMN_INTERNAL_ID + COMMA_SEP +
                        TableLogChanges.COLUMN_EXTERNAL_ID + COMMA_SEP +
                        TableLogChanges.COLUMN_CHANGE_TYPE + ")" +
                        " VALUES (" +
                        "\"" + tableName + "\"" + COMMA_SEP +
                        "NEW." + BaseColumns._ID + COMMA_SEP +
                        "-1" + COMMA_SEP +
                        TableLogChanges.ChangeType.INSERT.ordinal() + ")" +
                        "; END";
        db.execSQL(insertTrigger);

        String updateTrigger =
                "CREATE TRIGGER " + String.format(TRIGGER_LOG_CHANGES_UPDATE, tableName) + " UPDATE ON " + tableName +
                        " BEGIN " +
                        " INSERT OR IGNORE INTO " + TableLogChanges.TABLE_NAME + " (" +
                        TableLogChanges.COLUMN_TABLE_NAME + COMMA_SEP +
                        TableLogChanges.COLUMN_INTERNAL_ID + COMMA_SEP +
                        TableLogChanges.COLUMN_EXTERNAL_ID + COMMA_SEP +
                        TableLogChanges.COLUMN_CHANGE_TYPE + ")" +
                        " VALUES (" +
                        "\"" + tableName + "\"" + COMMA_SEP +
                        "OLD." + BaseColumns._ID + COMMA_SEP +
                        "NEW." + externalId + COMMA_SEP +
                        TableLogChanges.ChangeType.UPDATE.ordinal() + ")" +
                        "; END";
        db.execSQL(updateTrigger);

        String selectClientInserts =
                "SELECT * FROM " + TableLogChanges.TABLE_NAME +
                        " WHERE " + TableLogChanges.COLUMN_INTERNAL_ID + " = OLD._id AND " +
                        TableLogChanges.COLUMN_CHANGE_TYPE + " = " + TableLogChanges.ChangeType.INSERT.ordinal();

        String deleteTrigger =
                "CREATE TRIGGER " + String.format(TRIGGER_LOG_CHANGES_DELETE, tableName) + " BEFORE DELETE ON " + tableName +
                        " WHEN NOT EXISTS " +
                        "   (" + selectClientInserts + ")" +
                        " BEGIN " +
                        " INSERT OR REPLACE INTO " + TableLogChanges.TABLE_NAME + " (" +
                        TableLogChanges.COLUMN_TABLE_NAME + COMMA_SEP +
                        TableLogChanges.COLUMN_INTERNAL_ID + COMMA_SEP +
                        TableLogChanges.COLUMN_EXTERNAL_ID + COMMA_SEP +
                        TableLogChanges.COLUMN_CHANGE_TYPE + ")" +
                        " VALUES (" +
                        "\"" + tableName + "\"" + COMMA_SEP +
                        "OLD." + BaseColumns._ID + COMMA_SEP +
                        "OLD." + externalId + COMMA_SEP +
                        TableLogChanges.ChangeType.DELETE.ordinal() + ")" +
                        "; END";
        db.execSQL(deleteTrigger);

        String deleteInsertedTrigger =
                "CREATE TRIGGER " + String.format(TRIGGER_LOG_CHANGES_DELETE_INSERTED, tableName) + " AFTER DELETE ON " + tableName +
                        " WHEN EXISTS " +
                        "   (" + selectClientInserts + ")" +
                        " BEGIN " +
                        " DELETE FROM " + TableLogChanges.TABLE_NAME +
                        " WHERE " + TableLogChanges.COLUMN_INTERNAL_ID + " = OLD._id" +
                        "; END";
        db.execSQL(deleteInsertedTrigger);
    }

    public void dropLogChangesTriggers(String tableName) {
        db.execSQL("DROP TRIGGER " + String.format(TRIGGER_LOG_CHANGES_INSERT, tableName));
        db.execSQL("DROP TRIGGER " + String.format(TRIGGER_LOG_CHANGES_UPDATE, tableName));
        db.execSQL("DROP TRIGGER " + String.format(TRIGGER_LOG_CHANGES_DELETE, tableName));
        db.execSQL("DROP TRIGGER " + String.format(TRIGGER_LOG_CHANGES_DELETE_INSERTED, tableName));
    }

    public void createLogChangesSyncTriggers(String tableName, String externalId) {
        String syncInsertTrigger =
                "CREATE TRIGGER " + String.format(TRIGGER_LOG_CHANGES_SYNC_INSERT, tableName) + " INSERT ON " + tableName +
                        " BEGIN " +
                        " DELETE FROM " + TableLogChanges.TABLE_NAME +
                        " WHERE " + TableLogChanges.COLUMN_EXTERNAL_ID + " = NEW." + externalId +
                        "; END";
        db.execSQL(syncInsertTrigger);

        String syncUpdateTrigger =
                "CREATE TRIGGER " + String.format(TRIGGER_LOG_CHANGES_SYNC_UPDATE, tableName) + " UPDATE ON " + tableName +
                        " BEGIN " +
                        " DELETE FROM " + TableLogChanges.TABLE_NAME +
                        " WHERE " + TableLogChanges.COLUMN_EXTERNAL_ID + " = NEW." + externalId +
                        "; END";
        db.execSQL(syncUpdateTrigger);

        String syncDeleteTrigger =
                "CREATE TRIGGER " + String.format(TRIGGER_LOG_CHANGES_SYNC_DELETE, tableName) + " DELETE ON " + tableName +
                        " BEGIN " +
                        " DELETE FROM " + TableLogChanges.TABLE_NAME +
                        " WHERE " + TableLogChanges.COLUMN_EXTERNAL_ID + " = OLD." + externalId +
                        "; END";
        db.execSQL(syncDeleteTrigger);
    }

    public void dropLogChangesSyncTriggers(String tableName) {
        db.execSQL("DROP TRIGGER " + String.format(TRIGGER_LOG_CHANGES_SYNC_INSERT, tableName));
        db.execSQL("DROP TRIGGER " + String.format(TRIGGER_LOG_CHANGES_SYNC_UPDATE, tableName));
        db.execSQL("DROP TRIGGER " + String.format(TRIGGER_LOG_CHANGES_SYNC_DELETE, tableName));
    }

    public Map<String, List<Long>> getDeletedRowsId() {
        Map<String, List<Long>> tables = new HashMap<>();

        Cursor cursor = db.query(
                TableLogChanges.TABLE_NAME,
                new String[]{TableLogChanges.COLUMN_TABLE_NAME, TableLogChanges.COLUMN_EXTERNAL_ID},
                TableLogChanges.COLUMN_CHANGE_TYPE + " = ?",
                new String[]{String.valueOf(TableLogChanges.ChangeType.DELETE.ordinal())},
                null,
                null,
                TableLogChanges.COLUMN_TABLE_NAME);

        while (cursor.moveToNext()) {
            String tableName = cursor.getString(cursor.getColumnIndex(TableLogChanges.COLUMN_TABLE_NAME));
            int indexExternalId = cursor.getColumnIndex(TableLogChanges.COLUMN_EXTERNAL_ID);
            if (!tables.containsKey(tableName)) {
                tables.put(tableName, new ArrayList<Long>());
            }
            tables.get(tableName).add(cursor.getLong(indexExternalId));
        }

        cursor.close();
        return tables;
    }

    public Map<String, List<Map<String, String>>> getInsertedRows() {
        return getChangedRows(TableLogChanges.ChangeType.INSERT);
    }

    public Map<String, List<Map<String, String>>> getUpdatedRows() {
        return getChangedRows(TableLogChanges.ChangeType.UPDATE);
    }

    private Map<String, List<Map<String, String>>> getChangedRows(TableLogChanges.ChangeType changeType) {
        Map<String, List<Map<String, String>>> changedRows = new HashMap<>();

        Cursor cursor = db.query(
                TableLogChanges.TABLE_NAME,
                new String[]{TableLogChanges.COLUMN_TABLE_NAME, TableLogChanges.COLUMN_INTERNAL_ID},
                TableLogChanges.COLUMN_CHANGE_TYPE + " = ?",
                new String[]{String.valueOf(changeType.ordinal())},
                null,
                null,
                TableLogChanges.COLUMN_TABLE_NAME);

        while (cursor.moveToNext()) {
            String tableName = cursor.getString(cursor.getColumnIndex(TableLogChanges.COLUMN_TABLE_NAME));
            long id = cursor.getLong(cursor.getColumnIndex(TableLogChanges.COLUMN_INTERNAL_ID));
            if (!changedRows.containsKey(tableName)) {
                changedRows.put(tableName, new ArrayList<Map<String, String>>());
            }
            changedRows.get(tableName).add(getRow(tableName, id));
        }

        cursor.close();
        return changedRows;
    }
}

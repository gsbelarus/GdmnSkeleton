package com.gsbelarus.gedemin.skeleton.core.data;

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
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CoreDatabaseManager extends BaseDatabaseManager {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final int DEFAULT_DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "skeleton_db.sqlite";

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

    private static CoreDatabaseManager instance = null;

    private BasicDatabaseOpenHelper.Delegate dbOpenHelperImpl = new BasicDatabaseOpenHelper.Delegate() {
        @Override
        public void onCreate(SQLiteDatabase db) {
            Logger.d();
            db.execSQL(CREATE_TABLE_LOG_CHANGES);
            db.execSQL(CREATE_TABLE_LOG_SYNC);
            db.execSQL(CREATE_TABLE_LOG_SYNC_TOKENS);

//            Column column0 = new Column(BaseColumns._ID, ColumnType.INTEGER, ColumnConstraint.PRIMARY_KEY);
//            Column column1 = new Column("column1_BIGINT", ColumnType.INTEGER);
//            Column column2 = new Column("column2_CHAR_32767", ColumnType.TEXT); //from 1 to 32,767 bytes
//            Column column3 = new Column("column3_DATE", ColumnType.REAL); //01.01.0001 AD to 31.12.9999
//            Column column4 = new Column("column4_DECIMAL_18_18", ColumnType.NUMERIC); //~15 digits
//            Column column5 = new Column("column5_FLOAT", ColumnType.REAL); //~7 digits
//            Column column6 = new Column("column6_INTEGER", ColumnType.INTEGER);//-2,147,483,648 up to 2,147,483,647
//            Column column7 = new Column("column7_NUMERIC_18_18", ColumnType.NUMERIC);
//            Column column8 = new Column("column8_SMALLINT", ColumnType.INTEGER); //-32,768 to 32,767
//            Column column9 = new Column("column9_TIME", ColumnType.INTEGER); //0:00 to 23:59:59.9999
//            Column column10 = new Column("column10_TIMESTAMP", ColumnType.REAL); //01.01.0001 - 31.12.9999
//            Column column11 = new Column("column11_VARCHAR_32765", ColumnType.TEXT); //32,765 bytes
//
//            String createQuery = SQLiteQueryBuilder.create()
//                    .table("table1")
//                    .column(column0)
//                    .column(column1)
//                    .column(column2)
//                    .column(column3)
//                    .column(column4)
//                    .column(column5)
//                    .column(column6)
//                    .column(column7)
//                    .column(column8)
//                    .column(column9)
//                    .column(column10)
//                    .column(column11)
//                    .toString();
//
//            db.execSQL(createQuery);
//
//            // insert
//
//            InsertColumns insertColumns = Statements.insert().
//                    into("table1")
//                    .columns("column1_BIGINT",
//                            "column2_CHAR_32767",
//                            "column3_DATE",
//                            "column4_DECIMAL_18_18",
//                            "column5_FLOAT",
//                            "column6_INTEGER",
//                            "column7_NUMERIC_18_18",
//                            "column8_SMALLINT",
//                            "column9_TIME",
//                            "column10_TIMESTAMP",
//                            "column11_VARCHAR_32765");
//
//            List<FinishedSqlPart> insertQueries = new ArrayList<>();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
//            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
//
//            try {
//                insertQueries.add(insertColumns.values(BigInteger.valueOf(1223372036854775807l), "CHAR text 1", dateFormat.parse("01.01.0001"), 1234.5678, 1.23, -2147483648, 1000.5, -32768, java.sql.Time.valueOf("00:00:00"), dateTimeFormat.parse("01.01.0001 00:00:00"), "VARCHAR text 1"));
//                insertQueries.add(insertColumns.values(BigInteger.valueOf(2223372036854775807l), "CHAR text 2", dateFormat.parse("02.01.2016"), 2000.5678, 2.23, 200000000, 2000.5, 2000, java.sql.Time.valueOf("02:02:02"), dateTimeFormat.parse("02.01.2016 02:02:02"), "VARCHAR text 2"));
//                insertQueries.add(insertColumns.values(BigInteger.valueOf(3223372036854775807l), "CHAR text 3", dateFormat.parse("03.01.2016"), 3000.5678, 3.23, 300000000, 3000.5, 3000, java.sql.Time.valueOf("03:03:03"), dateTimeFormat.parse("03.01.2016 03:03:03"), "VARCHAR text 3"));
//                insertQueries.add(insertColumns.values(BigInteger.valueOf(4223372036854775807l), "CHAR text 4", dateFormat.parse("04.01.2016"), 4000.5678, 4.23, 400000000, 4000.5, 4000, java.sql.Time.valueOf("04:04:04"), dateTimeFormat.parse("04.01.2016 04:04:04"), "VARCHAR text 4"));
//                insertQueries.add(insertColumns.values(BigInteger.valueOf(5223372036854775807l), "CHAR text 5", dateFormat.parse("05.01.2016"), 5000.5678, 5.23, 500000000, 5000.5, 5000, java.sql.Time.valueOf("05:05:05"), dateTimeFormat.parse("05.01.2016 05:05:05"), "VARCHAR text 5"));
//                insertQueries.add(insertColumns.values(BigInteger.valueOf(6223372036854775807l), "CHAR text 6", dateFormat.parse("06.01.2016"), 6000.5678, 6.23, 600000000, 6000.5, 6000, java.sql.Time.valueOf("06:06:06"), dateTimeFormat.parse("06.01.2016 06:06:06"), "VARCHAR text 6"));
//                insertQueries.add(insertColumns.values(BigInteger.valueOf(7223372036854775807l), "CHAR text 7", dateFormat.parse("07.01.2016"), 7000.5678, 7.23, 700000000, 7000.5, 7000, java.sql.Time.valueOf("07:07:07"), dateTimeFormat.parse("07.01.2016 07:07:07"), "VARCHAR text 7"));
//                insertQueries.add(insertColumns.values(BigInteger.valueOf(8223372036854775807l), "CHAR text 8", dateFormat.parse("08.01.2016"), 8000.5678, 8.23, 800000000, 8000.5, 8000, java.sql.Time.valueOf("08:08:08"), dateTimeFormat.parse("08.01.2016 08:08:08"), "VARCHAR text 8"));
//                insertQueries.add(insertColumns.values(BigInteger.valueOf(9223372036854775807l), "CHAR text 9", dateFormat.parse("31.12.9999"), 9000.5678, 9.23, 2147483647, 9000.5, 32767, java.sql.Time.valueOf("23:59:59"), dateTimeFormat.parse("31.12.9999 23:59:59"), "VARCHAR text 9"));
//                insertQueries.add(insertColumns.values(BigInteger.valueOf(5223372036854775807l), "CHAR text 10", dateFormat.parse("05.01.2016"), 5000.5678, 5.23, 500000000, 5000.5, 5000, java.sql.Time.valueOf("05:05:05"), dateTimeFormat.parse("05.01.2016 05:05:05"), "VARCHAR text 10"));
//                insertQueries.add(insertColumns.values(BigInteger.valueOf(6223372036854775807l), "CHAR text 11", dateFormat.parse("06.01.2016"), 6000.5678, 6.23, 600000000, 6000.5, 6000, java.sql.Time.valueOf("06:06:06"), dateTimeFormat.parse("06.01.2016 06:06:06"), "VARCHAR text 11"));
//                insertQueries.add(insertColumns.values(BigInteger.valueOf(7223372036854775807l), "CHAR text 12", dateFormat.parse("07.01.2016"), 7000.5678, 7.23, 700000000, 7000.5, 7000, java.sql.Time.valueOf("07:07:07"), dateTimeFormat.parse("07.01.2016 07:07:07"), "VARCHAR text 12"));
//                insertQueries.add(insertColumns.values(BigInteger.valueOf(8223372036854775807l), "CHAR text 13", dateFormat.parse("08.01.2016"), 8000.5678, 8.23, 800000000, 8000.5, 8000, java.sql.Time.valueOf("08:08:08"), dateTimeFormat.parse("08.01.2016 08:08:08"), "VARCHAR text 13"));
//                insertQueries.add(insertColumns.values(BigInteger.valueOf(9223372036854775807l), "CHAR text 14", dateFormat.parse("31.12.9999"), 9000.5678, 9.23, 2147483647, 9000.5, 32767, java.sql.Time.valueOf("23:59:59"), dateTimeFormat.parse("31.12.9999 23:59:59"), "VARCHAR text 14"));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            for (FinishedSqlPart insertQuery : insertQueries) {
//                db.execSQL(Statements.converter().toSql(insertQuery), Statements.converter().retrieveArguments(insertQuery));
//            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            throw new SQLiteException("Upgrade is not supported " +
                    oldVersion + " to " + newVersion);
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

    private CoreDatabaseManager(Context context) {
        super(context, DATABASE_NAME, DEFAULT_DATABASE_VERSION);
    }

    @NotNull
    public static synchronized CoreDatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new CoreDatabaseManager(context);
        }
        return instance;
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
    protected BasicDatabaseOpenHelper.Delegate getDbOpenHelperImpl() {
        return dbOpenHelperImpl;
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

    protected Map<String, String> setVersion(int version, @NonNull Callback callback) {     //TODO protected
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
        dbOpenHelperImpl.onCreate(db);
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

    public interface Callback {
        void onCreateDatabase(CoreDatabaseManager coreDatabaseManager);

        void onUpgradeDatabase(CoreDatabaseManager coreDatabaseManager, int oldVersion, int newVersion);
    }
}

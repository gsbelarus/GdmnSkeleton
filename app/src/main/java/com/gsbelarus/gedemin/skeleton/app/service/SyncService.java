package com.gsbelarus.gedemin.skeleton.app.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.gsbelarus.gedemin.skeleton.app.App;
import com.gsbelarus.gedemin.skeleton.app.view.activity.LoginActivity;
import com.gsbelarus.gedemin.skeleton.core.data.CoreDatabaseManager;
import com.gsbelarus.gedemin.skeleton.core.data.CoreSyncService;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;

public class SyncService extends CoreSyncService {

    @Override
    protected String getUrl(Account account, Bundle extras) {
        AccountManager accountManager = AccountManager.get(getApplicationContext());
        return accountManager.getUserData(account, LoginActivity.TAG_SERVER_URL);
    }

    @Override
    protected boolean isDemoDatabase(Account account, Bundle extras) {
        Logger.d(App.getDemoSyncAccount(getApplicationContext()).equals(account));
        return App.getDemoSyncAccount(getApplicationContext()).equals(account);
    }

    @NonNull
    @Override
    protected String getNamespace() {
        return "ODataDemo";
    }

    @Override
    protected void onHandleRow(String tableName, ContentValues contentValues) {
        super.onHandleRow(tableName, contentValues);
//        Logger.d(tableName, contentValues.keySet());
    }

    @Override
    protected Notification getStartSyncNotification() {
        return super.getStartSyncNotification();
    }

    @Override
    protected Notification getErrorSyncNotification(String errorMessage) {
        return super.getErrorSyncNotification(errorMessage);
    }

    @Override
    public void onCreateDemoDatabase(CoreDatabaseManager coreDatabaseManager) {
        super.onCreateDemoDatabase(coreDatabaseManager);

//        Column column0 = new Column(BaseColumns._ID, ColumnType.INTEGER, ColumnConstraint.PRIMARY_KEY);
//        Column column1 = new Column("column1_BIGINT", ColumnType.INTEGER);
//        Column column2 = new Column("column2_CHAR_32767", ColumnType.TEXT); //from 1 to 32,767 bytes
//        Column column3 = new Column("column3_DATE", ColumnType.REAL); //01.01.0001 AD to 31.12.9999
//        Column column4 = new Column("column4_DECIMAL_18_18", ColumnType.NUMERIC); //~15 digits
//        Column column5 = new Column("column5_FLOAT", ColumnType.REAL); //~7 digits
//        Column column6 = new Column("column6_INTEGER", ColumnType.INTEGER);//-2,147,483,648 up to 2,147,483,647
//        Column column7 = new Column("column7_NUMERIC_18_18", ColumnType.NUMERIC);
//        Column column8 = new Column("column8_SMALLINT", ColumnType.INTEGER); //-32,768 to 32,767
//        Column column9 = new Column("column9_TIME", ColumnType.INTEGER); //0:00 to 23:59:59.9999
//        Column column10 = new Column("column10_TIMESTAMP", ColumnType.REAL); //01.01.0001 - 31.12.9999
//        Column column11 = new Column("column11_VARCHAR_32765", ColumnType.TEXT); //32,765 bytes
//
//        String createQuery = SQLiteQueryBuilder.create()
//                .table("table1")
//                .column(column0)
//                .column(column1)
//                .column(column2)
//                .column(column3)
//                .column(column4)
//                .column(column5)
//                .column(column6)
//                .column(column7)
//                .column(column8)
//                .column(column9)
//                .column(column10)
//                .column(column11)
//                .toString();
//
//        coreDatabaseManager.execSQL(createQuery);
//
//        // insert
//
//        InsertColumns insertColumns = Statements.insert().
//                into("table1")
//                .columns("column1_BIGINT",
//                        "column2_CHAR_32767",
//                        "column3_DATE",
//                        "column4_DECIMAL_18_18",
//                        "column5_FLOAT",
//                        "column6_INTEGER",
//                        "column7_NUMERIC_18_18",
//                        "column8_SMALLINT",
//                        "column9_TIME",
//                        "column10_TIMESTAMP",
//                        "column11_VARCHAR_32765");
//
//        List<FinishedSqlPart> insertQueries = new ArrayList<>();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
//        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss", Locale.getDefault());
//
//        try {
//            insertQueries.add(insertColumns.values(BigInteger.valueOf(1223372036854775807l), "CHAR text 1", dateFormat.parse("01.01.0001"), 1234.5678, 1.23, -2147483648, 1000.5, -32768, java.sql.Time.valueOf("00:00:00"), dateTimeFormat.parse("01.01.0001 00:00:00"), "VARCHAR text 1"));
//            insertQueries.add(insertColumns.values(BigInteger.valueOf(2223372036854775807l), "CHAR text 2", dateFormat.parse("02.01.2016"), 2000.5678, 2.23, 200000000, 2000.5, 2000, java.sql.Time.valueOf("02:02:02"), dateTimeFormat.parse("02.01.2016 02:02:02"), "VARCHAR text 2"));
//            insertQueries.add(insertColumns.values(BigInteger.valueOf(3223372036854775807l), "CHAR text 3", dateFormat.parse("03.01.2016"), 3000.5678, 3.23, 300000000, 3000.5, 3000, java.sql.Time.valueOf("03:03:03"), dateTimeFormat.parse("03.01.2016 03:03:03"), "VARCHAR text 3"));
//            insertQueries.add(insertColumns.values(BigInteger.valueOf(4223372036854775807l), "CHAR text 4", dateFormat.parse("04.01.2016"), 4000.5678, 4.23, 400000000, 4000.5, 4000, java.sql.Time.valueOf("04:04:04"), dateTimeFormat.parse("04.01.2016 04:04:04"), "VARCHAR text 4"));
//            insertQueries.add(insertColumns.values(BigInteger.valueOf(5223372036854775807l), "CHAR text 5", dateFormat.parse("05.01.2016"), 5000.5678, 5.23, 500000000, 5000.5, 5000, java.sql.Time.valueOf("05:05:05"), dateTimeFormat.parse("05.01.2016 05:05:05"), "VARCHAR text 5"));
//            insertQueries.add(insertColumns.values(BigInteger.valueOf(6223372036854775807l), "CHAR text 6", dateFormat.parse("06.01.2016"), 6000.5678, 6.23, 600000000, 6000.5, 6000, java.sql.Time.valueOf("06:06:06"), dateTimeFormat.parse("06.01.2016 06:06:06"), "VARCHAR text 6"));
//            insertQueries.add(insertColumns.values(BigInteger.valueOf(7223372036854775807l), "CHAR text 7", dateFormat.parse("07.01.2016"), 7000.5678, 7.23, 700000000, 7000.5, 7000, java.sql.Time.valueOf("07:07:07"), dateTimeFormat.parse("07.01.2016 07:07:07"), "VARCHAR text 7"));
//            insertQueries.add(insertColumns.values(BigInteger.valueOf(8223372036854775807l), "CHAR text 8", dateFormat.parse("08.01.2016"), 8000.5678, 8.23, 800000000, 8000.5, 8000, java.sql.Time.valueOf("08:08:08"), dateTimeFormat.parse("08.01.2016 08:08:08"), "VARCHAR text 8"));
//            insertQueries.add(insertColumns.values(BigInteger.valueOf(9223372036854775807l), "CHAR text 9", dateFormat.parse("31.12.9999"), 9000.5678, 9.23, 2147483647, 9000.5, 32767, java.sql.Time.valueOf("23:59:59"), dateTimeFormat.parse("31.12.9999 23:59:59"), "VARCHAR text 9"));
//            insertQueries.add(insertColumns.values(BigInteger.valueOf(5223372036854775807l), "CHAR text 10", dateFormat.parse("05.01.2016"), 5000.5678, 5.23, 500000000, 5000.5, 5000, java.sql.Time.valueOf("05:05:05"), dateTimeFormat.parse("05.01.2016 05:05:05"), "VARCHAR text 10"));
//            insertQueries.add(insertColumns.values(BigInteger.valueOf(6223372036854775807l), "CHAR text 11", dateFormat.parse("06.01.2016"), 6000.5678, 6.23, 600000000, 6000.5, 6000, java.sql.Time.valueOf("06:06:06"), dateTimeFormat.parse("06.01.2016 06:06:06"), "VARCHAR text 11"));
//            insertQueries.add(insertColumns.values(BigInteger.valueOf(7223372036854775807l), "CHAR text 12", dateFormat.parse("07.01.2016"), 7000.5678, 7.23, 700000000, 7000.5, 7000, java.sql.Time.valueOf("07:07:07"), dateTimeFormat.parse("07.01.2016 07:07:07"), "VARCHAR text 12"));
//            insertQueries.add(insertColumns.values(BigInteger.valueOf(8223372036854775807l), "CHAR text 13", dateFormat.parse("08.01.2016"), 8000.5678, 8.23, 800000000, 8000.5, 8000, java.sql.Time.valueOf("08:08:08"), dateTimeFormat.parse("08.01.2016 08:08:08"), "VARCHAR text 13"));
//            insertQueries.add(insertColumns.values(BigInteger.valueOf(9223372036854775807l), "CHAR text 14", dateFormat.parse("31.12.9999"), 9000.5678, 9.23, 2147483647, 9000.5, 32767, java.sql.Time.valueOf("23:59:59"), dateTimeFormat.parse("31.12.9999 23:59:59"), "VARCHAR text 14"));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        for (FinishedSqlPart insertQuery : insertQueries) {
//            coreDatabaseManager.execSQL(Statements.converter().toSql(insertQuery), Statements.converter().retrieveArguments(insertQuery));
//        }
    }

    @Override
    public void onCreateDatabase(CoreDatabaseManager coreDatabaseManager) {
        super.onCreateDatabase(coreDatabaseManager);
    }

    @Override
    public void onUpgradeDatabase(CoreDatabaseManager coreDatabaseManager, int oldVersion, int newVersion) {
        super.onUpgradeDatabase(coreDatabaseManager, oldVersion, newVersion);
    }
}

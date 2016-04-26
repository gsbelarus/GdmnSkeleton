package com.gsbelarus.gedemin.skeleton.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.BaseColumns;

import com.alexfu.sqlitequerybuilder.api.Column;
import com.alexfu.sqlitequerybuilder.api.ColumnConstraint;
import com.alexfu.sqlitequerybuilder.api.ColumnType;
import com.alexfu.sqlitequerybuilder.api.SQLiteQueryBuilder;
import com.gsbelarus.gedemin.skeleton.base.db.BaseDatabaseManager;
import com.gsbelarus.gedemin.skeleton.base.db.DatabaseOpenHelper;
import com.nhaarman.sqlitebuilder.FinishedSqlPart;
import com.nhaarman.sqlitebuilder.impl.Statements;

import org.jetbrains.annotations.NotNull;


public class DatabaseManager extends BaseDatabaseManager implements DatabaseOpenHelper.DBOpenHelperCallback {

    private static DatabaseManager instance = null;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "skeleton_db.sqlite";


    @NotNull
    public static synchronized DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    private DatabaseManager(Context context) {
        super(context, DATABASE_NAME, DATABASE_VERSION);
    }

    @Override
    public void onCreateDatabase(SQLiteDatabase db) {

        Column column0  = new Column(BaseColumns._ID,          ColumnType.INTEGER, ColumnConstraint.PRIMARY_KEY);
        Column column1  = new Column("column1_BIGINT",         ColumnType.INTEGER);
        Column column2  = new Column("column2_CHAR_32767",     ColumnType.TEXT); //from 1 to 32,767 bytes
        Column column3  = new Column("column3_DATE",           ColumnType.REAL); //01.01.0001 AD to 31.12.9999
        Column column4  = new Column("column4_DECIMAL_18_18",  ColumnType.NUMERIC); //~15 digits //TODO !уточннить
        Column column5  = new Column("column5_FLOAT",          ColumnType.REAL); //~7 digits
        Column column6  = new Column("column6_INTEGER",        ColumnType.INTEGER);//-2,147,483,648 up to 2,147,483,647
        Column column7  = new Column("column7_NUMERIC_18_18",  ColumnType.NUMERIC); //TODO !уточннить
        Column column8  = new Column("column8_SMALLINT",       ColumnType.INTEGER); //-32,768 to 32,767
        Column column9  = new Column("column9_TIME",           ColumnType.INTEGER); //0:00 to 23:59:59.9999
        Column column10 = new Column("column10_TIMESTAMP",     ColumnType.REAL); //01.01.0001 - 31.12.9999
        Column column11 = new Column("column11_VARCHAR_32765", ColumnType.TEXT); //32,765 bytes

        String createQuery = SQLiteQueryBuilder.create()
                .table("table1")
                    .column(column0)
                    .column(column1)
                    .column(column2)
                    .column(column3)
                    .column(column4)
                    .column(column5)
                    .column(column6)
                    .column(column7)
                    .column(column8)
                    .column(column9)
                    .column(column10)
                    .column(column11)
                .toString();

        db.execSQL(createQuery);

        FinishedSqlPart insertQuery = Statements.insert().
                into("table1")
                    .columns("column6_INTEGER")
                    .values(12345);

        db.execSQL(Statements.converter().toSql(insertQuery), new Object[] {12345});
        //TODO нагенерить данных
    }

    @Override
    public void onUpgradeDatabase(SQLiteDatabase db) {
        if (db.getVersion() != DATABASE_VERSION) {
            onCreateDatabase(db);
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

}

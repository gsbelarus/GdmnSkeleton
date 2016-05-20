package com.gsbelarus.gedemin.skeleton.core;

import android.provider.BaseColumns;

public class CoreContract {

    public static abstract class TableLogChanges implements BaseColumns {
        public static final String TABLE_NAME = "log_changes";

        public static final String COLUMN_TABLE_NAME = "table_name";
        public static final String COLUMN_INTERNAL_ID = "internal_id";
        public static final String COLUMN_EXTERNAL_ID = "external_id";
        public static final String COLUMN_CHANGE_TYPE = "change_type";

        public enum ChangeType {INSERT, UPDATE, DELETE}
    }

    public static abstract class TableLogSync implements BaseColumns {
        public static final String TABLE_NAME = "log_sync";

        public static final String COLUMN_VERSION_DB = "version_db";
        public static final String COLUMN_SYNC_DATE = "sync_date";
    }
}

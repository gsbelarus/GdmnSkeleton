package com.gsbelarus.gedemin.skeleton.core.data;

import android.provider.BaseColumns;


public class CoreContract {

    public static final String TEST_TABLE = "Categories";
    public static final String TEST_TABLE_NULLHACK_COLUMN = "Name";

    public static abstract class TableLogChanges implements BaseColumns {
        public static final String TABLE_NAME = "log_changes";

        public static final String COLUMN_TABLE_NAME = "table_name";
        public static final String COLUMN_INTERNAL_ID = "internal_id";
        public static final String COLUMN_EXTERNAL_ID = "external_id";
        public static final String COLUMN_CHANGE_TYPE = "change_type";

        public enum ChangeType {INSERT, UPDATE, DELETE}
    }

    public static abstract class TableSyncSchemaVersion implements BaseColumns {
        public static final String TABLE_NAME = "sync_schema_version";

        public static final String COLUMN_VERSION_DB = "version_db";
        public static final String COLUMN_SYNC_DATE = "sync_date";
    }

    public static abstract class TableSyncSchema implements BaseColumns {
        public static final String TABLE_NAME = "sync_schema";

        public static final String COLUMN_TABLE_NAME = "table_name";
        public static final String COLUMN_SYNC_TOKEN = "sync_token";
        public static final String COLUMN_SYNC_SCHEMA_VERSION_KEY = "sync_schema_version_key";
    }
}

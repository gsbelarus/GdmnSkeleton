package com.gsbelarus.gedemin.skeleton.base.db;


public class SQLiteDataType {

    public enum SQLiteStorageTypes {

        TEXT, INTEGER, BLOB, REAL, NUMERIC;

        @Override
        public String toString() {
            return " " + name() + " ";
        }
    }

    public enum SQLiteDataTypes {

        NULL, INTEGER, FLOAT, STRING, BLOB; // соответственно значениям Cursor.FIELD_TYPE_

        public String getDataTypeString() {

            switch (values()[ordinal()]) {
                case STRING:
                    return SQLiteStorageTypes.TEXT.name();
                case FLOAT:
                    return SQLiteStorageTypes.REAL.name();
                case INTEGER:
                case NULL:
                case BLOB:
                    return name();
                default:
                    return "[unknown type]"; // TODO exception
            }
        }
    }

    public static String getDataTypeString(int columnType) {
        return SQLiteDataTypes.values()[columnType].getDataTypeString();
    }

}

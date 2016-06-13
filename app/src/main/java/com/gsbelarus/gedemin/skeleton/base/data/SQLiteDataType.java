package com.gsbelarus.gedemin.skeleton.base.data;

import com.gsbelarus.gedemin.skeleton.core.UnsupportedDataTypeException;

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

        public String getStorageDataTypeString() {

            switch (this) {
                case STRING:
                    return SQLiteStorageTypes.TEXT.name();
                case FLOAT:
                    return SQLiteStorageTypes.REAL.name();
                case INTEGER:
                case NULL:
                case BLOB:
                    return name();
                default:
                    throw new UnsupportedDataTypeException(String.valueOf(ordinal()));
            }
        }
    }


    public static String getDataTypeString(int columnType) {
        return SQLiteDataTypes.values()[columnType].getStorageDataTypeString();
    }

}

package com.gsbelarus.gedemin.skeleton.view;


import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;

import com.gsbelarus.gedemin.skeleton.base.db.SQLiteDataType;

import java.util.concurrent.atomic.AtomicInteger;

public class Utils {

    private static final AtomicInteger nextGeneratedViewId = new AtomicInteger(1);

    @NonNull
    public static CharSequence getFieldValueString(String columnName, Cursor dataCursor) {
//        if(dataCursor.)

        SpannableString nullString = new SpannableString("[null]");
        nullString.setSpan(new StyleSpan(Typeface.ITALIC), 0, nullString.length(), 0);

        CharSequence value = "";
        int columnIndex = dataCursor.getColumnIndex(columnName);
        switch (dataCursor.getType(columnIndex)) {
            case Cursor.FIELD_TYPE_STRING:
                value = dataCursor.getString(columnIndex);
                break;
            case Cursor.FIELD_TYPE_INTEGER:
                value = String.valueOf(dataCursor.getInt(columnIndex));
                break;
            case Cursor.FIELD_TYPE_FLOAT:
                value = String.valueOf(dataCursor.getFloat(columnIndex));
                break;
            case Cursor.FIELD_TYPE_NULL:
                value = nullString;
                break;
            case Cursor.FIELD_TYPE_BLOB:
                value =  String.format("(%d bytes)", dataCursor.getBlob(columnIndex).length);
                break;
            default:
                //throw new AssertionError("Unknown type: " + dataCursor.getType(columnIndex));
                break;
        }

        if (dataCursor.isNull(columnIndex)) {
            value = nullString;
        }

        return value;
    }

    public static int dpToPixel(float dp, Context context) {
        return (int) (dp * ((float)  context.getResources().
                getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int generateViewId() {
        int result;
        int newValue;
        do {
            result = nextGeneratedViewId.get();
            newValue = result + 1;
            if (newValue > ViewCompat.MEASURED_SIZE_MASK) {
                newValue = 1;
            }
        } while (!nextGeneratedViewId.compareAndSet(result, newValue));

        return result;
    }

}

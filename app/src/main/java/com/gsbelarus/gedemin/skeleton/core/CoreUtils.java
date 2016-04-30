package com.gsbelarus.gedemin.skeleton.core;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;

import java.util.concurrent.atomic.AtomicInteger;

public class CoreUtils {

    private static final AtomicInteger nextGeneratedViewId = new AtomicInteger(1);

    @NonNull
    public static CharSequence getFieldValueString(int columnIndex, Cursor dataCursor) {
        //TODO check columnName (columnIndex = -1)
        CharSequence value = "";
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
                SpannableString nullString = new SpannableString("[null]");
                nullString.setSpan(new StyleSpan(Typeface.ITALIC), 0, nullString.length(), 0);
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
            value = "";
        }

        return value;
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

    public static int dpToPixel(float dp, Context context) {
        return (int) (dp * ((float)  context.getResources().
                getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}

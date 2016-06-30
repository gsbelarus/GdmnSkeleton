package com.gsbelarus.gedemin.skeleton.base;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class BasicUtils {

    public static void bindViews(@Nullable Cursor cursor, final String[] originalFrom, final int[] to, View parentView) {
        if (cursor == null) return;

        int[] from = new int[originalFrom.length];
        for (int i = 0; i < originalFrom.length; i++) {
            from[i] = cursor.getColumnIndex(originalFrom[i]);
        }

        bindViews(cursor, from, to, parentView);
    }

    public static void bindViews(@Nullable Cursor cursor, final int[] from, final int[] to, View parentView) {
        if (cursor == null) return;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View valueView = parentView.findViewById(to[i]);
            if (valueView != null) {
                String text = cursor.getString(from[i]);
                if (text == null) {
                    text = "";
                }

                if (valueView instanceof TextView) {
                    bindTextView((TextView) valueView, text);
                } else if (valueView instanceof ImageView) {
                    bindViewImage((ImageView) valueView, text);
                } else {
                    throw new IllegalStateException(valueView.getClass().getName() + " is not a view that can be bounds");
                }
            }
        }
    }

    private static void bindTextView(TextView textView, CharSequence value) {
        textView.setText(value);
    }

    private static void bindViewImage(ImageView imageView, String value) {
        try {
            imageView.setImageResource(Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
            imageView.setImageURI(Uri.parse(value));

            // Tracking exception
            BaseApplication.getInstance().trackException(nfe);
        }
    }

}

package com.gsbelarus.gedemin.skeleton.core;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gsbelarus.gedemin.skeleton.R;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class CoreViewHelper {

    @IdRes
    private int[] to;
    private Map<View, View> valueViewLabelViewMap; //TODO LinkedHashMap


    public CoreViewHelper() {
        valueViewLabelViewMap = new LinkedHashMap<>();
    }

    public View generateCoreDetailView(ViewGroup parent, int columnCount) {
        return generateCoreView(R.layout.core_detail_item, parent, columnCount);
    }

    public LinearLayout generateCoreItemView(ViewGroup parent, int columnCount) {
        return generateCoreView(R.layout.core_recycler_item, parent, columnCount);
    }

    public LinearLayout generateCoreView(@LayoutRes int rowLayoutRes, ViewGroup parent, int columnCount) {
        valueViewLabelViewMap.clear();

        to = new int[columnCount];

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext()); //TODO
        LinearLayout itemView = (LinearLayout) layoutInflater.inflate(rowLayoutRes, parent, false);
        Context context = parent.getContext();

        itemView.removeAllViews();

        LinearLayout dynamicLinear = new LinearLayout(context);
        dynamicLinear.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < columnCount; i++) {
            View rowView = layoutInflater.inflate(R.layout.core_recycler_item_tv, dynamicLinear, false);

            int viewId = CoreUtils.generateViewId();
            rowView.setId(viewId);
            to[i] = viewId;

            dynamicLinear.addView(rowView);

            View valueView = rowView.findViewById(R.id.core_recycler_item_value);
            View labelView = rowView.findViewById(R.id.core_recycler_item_label);
            valueViewLabelViewMap.put(valueView, labelView);
        }

        itemView.addView(dynamicLinear);
        return itemView;
    }

    //TODO переделать
    public int[] getTo() {
        return to;
    }

    public Map<View, View> getValueViewLabelViewMap() {
        return valueViewLabelViewMap;
    }


    public static void bindViews(@Nullable Cursor cursor, final String[] originalFrom, Map<View, View> toValueViewLabelViewMap) {
        if (cursor == null) return;

        int[] from = new int[originalFrom.length];
        for (int i = 0; i < originalFrom.length; i++) {
            from[i] = cursor.getColumnIndex(originalFrom[i]);
        }

        bindViews(cursor, from, toValueViewLabelViewMap);
    }

    public static void bindViews(@Nullable Cursor cursor, final int[] from, Map<View, View> toValueViewLabelViewMap) {
        if (cursor == null) return;

        int i = 0;
        for (Map.Entry<View, View> entry : toValueViewLabelViewMap.entrySet()) {
            View labelView = entry.getValue();
            View valueView = entry.getKey();
            int columnIndex = from[i];

            if (labelView != null) {
                if (labelView instanceof TextView) {
                    bindLabelTextView((TextView) labelView,  cursor.getColumnName(columnIndex));
                } else {
                    throw new IllegalStateException(valueView.getClass().getName() + " is not a view that can be bounds by this CoreCursorRecyclerViewAdapter");
                }
            }

            if (valueView instanceof TextView) {
                bindTextView((TextView) valueView, CoreUtils.getFieldValueString(columnIndex, cursor));

            } else if (valueView instanceof ImageView) {
                bindImageView((ImageView) valueView, cursor.getBlob(columnIndex));

            } else {
                throw new IllegalStateException(valueView.getClass().getName() + " is not a view that can be bounds by this CoreCursorRecyclerViewAdapter");
            }

            i++;
        }
    }

    private static void bindLabelTextView(TextView labelView, CharSequence value) {
        labelView.setText(String.format("%s:", value));
    }

    private static void bindTextView(TextView textView, CharSequence value) {
        textView.setText(value);
    }

    private static void bindImageView(ImageView imageView, byte[] value) {
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(value, 0, value.length));
    }

}

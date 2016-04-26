package com.gsbelarus.gedemin.skeleton.view.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.BaseCursorRecyclerViewAdapter;
import com.gsbelarus.gedemin.skeleton.base.db.SQLiteDataType;
import com.gsbelarus.gedemin.skeleton.view.Utils;

import java.util.HashMap;
import java.util.Map;


public class CoreCursorRecyclerViewAdapter extends BaseCursorRecyclerViewAdapter {

    protected LayoutInflater layoutInflater;
    private final Context context;

    private HashMap<Integer, String> rowIdCursorColName;


    public CoreCursorRecyclerViewAdapter(@Nullable Cursor dataCursor, Context context) {
        super(dataCursor);
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowIdCursorColName = new HashMap<>();
    }

    @Override
    public BaseCursorItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = createItemView(parent, viewType);
        return new CoreCursorItemViewHolder(itemView, rowIdCursorColName);
    }

    private View createItemView(ViewGroup parent, int viewType) {
        LinearLayout itemView = (LinearLayout) layoutInflater.inflate(R.layout.core_recycler_item, parent, false);
        LinearLayout dynamicLinear = new LinearLayout(context);
        dynamicLinear.setOrientation(LinearLayout.VERTICAL);

        //TODO check cursor

        int columnAmount = getDataCursor().getColumnCount();
        for(int i = 0; i < columnAmount; i++) {
            View rowView;
            int viewId = Utils.generateViewId();
            if (getDataCursor().getType(i) != Cursor.FIELD_TYPE_BLOB) {
                rowView = layoutInflater.inflate(R.layout.core_recycler_item_tv, dynamicLinear, false);
            } else {
                rowView = layoutInflater.inflate(R.layout.core_recycler_item_img, dynamicLinear, false);
            }
            rowView.setId(viewId);

            rowIdCursorColName.put(viewId, getDataCursor().getColumnName(i));
            dynamicLinear.addView(rowView);
        }

        itemView.addView(dynamicLinear);
        return itemView;
    }


    public static class CoreCursorItemViewHolder extends BaseCursorItemViewHolder {

        private HashMap<View, String> rowViewCursorColNameMap; //TODO column Index

        public CoreCursorItemViewHolder(View itemView, HashMap<Integer, String> itemIdCursorIndex) {
            super(itemView);
            rowViewCursorColNameMap = new HashMap<>();

            for(Map.Entry<Integer, String> entry : itemIdCursorIndex.entrySet()) {
                rowViewCursorColNameMap.put(itemView.findViewById(entry.getKey()), entry.getValue());
            }
        }

        @Override
        public void bind(Cursor cursor) {
            for (Map.Entry<View, String> entry : rowViewCursorColNameMap.entrySet()) {
                String columnType = SQLiteDataType.getDataTypeString(cursor.getType(cursor.getColumnIndex(entry.getValue()))) + ": "; //TODO tmp
                View rowView = entry.getKey();

                if (rowView != null) { //TODO NULL!!!!

                    if (rowView instanceof TextView) {
                        ((TextView) rowView).setText(columnType + Utils.getFieldValueString(entry.getValue(), cursor));

                    } else if (rowView instanceof ImageView) {
                        byte[] value = cursor.getBlob(cursor.getColumnIndex(entry.getValue()));
                        ((ImageView) rowView).setImageBitmap(BitmapFactory.decodeByteArray(value, 0, value.length));

                    } else {
                        throw new IllegalStateException(rowView.getClass().getName() + " is not a " +
                                " view that can be bounds by this CoreCursorRecyclerViewAdapter");
                    }
                }
            }
        }
    }

}
package com.gsbelarus.gedemin.skeleton.core;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.viewhandler.CursorRecyclerAdapterViewHandler;

import java.util.HashMap;
import java.util.Map;


public class CoreCursorRecyclerAdapterViewHandler extends CursorRecyclerAdapterViewHandler {

    public CoreCursorRecyclerAdapterViewHandler(CoreCursorRecyclerItemViewTypeModel... cursorViewTypeModelMap) {
        super(cursorViewTypeModelMap);
    }

    @Override
    public CoreCursorItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = createItemView(parent, viewType); //inflater.inflate(itemViewTypeModel.getLayoutResource(), parent, false);
        return new CoreCursorItemViewHolder(view, getViewTypeModel(viewType).getTo());
    }

    private View createItemView(ViewGroup parent, int viewType) {
        CoreCursorRecyclerItemViewTypeModel itemViewTypeModel = getViewTypeModel(viewType);

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        LinearLayout itemView = (LinearLayout) layoutInflater.inflate(R.layout.core_recycler_item, parent, false);
        LinearLayout dynamicLinear = new LinearLayout(parent.getContext());
        dynamicLinear.setOrientation(LinearLayout.VERTICAL);

        int[] to = new int[getViewTypeModel(viewType).getOriginalFrom().length];
        final int rowCount =  getViewTypeModel(viewType).getOriginalFrom().length;
        for (int i = 0; i < rowCount; i++) {
            View rowView = layoutInflater.inflate(R.layout.core_recycler_item_tv, dynamicLinear, false);

            int viewId = CoreUtils.generateViewId();
            rowView.setId(viewId);
            to[i] = viewId;

            dynamicLinear.addView(rowView);
        }

        itemViewTypeModel.setTo(to);
        itemView.addView(dynamicLinear);
        return itemView;
    }

    @Override
    protected CoreCursorRecyclerItemViewTypeModel getViewTypeModel(int viewType) {
        return (CoreCursorRecyclerItemViewTypeModel) super.getViewTypeModel(viewType);
    }


    public class CoreCursorItemViewHolder extends BasicCursorItemViewHolder {

        Map<View, View>  valueViewLabelViewMap;

        public CoreCursorItemViewHolder(View itemView, final int[] to) {
            super(itemView);

            valueViewLabelViewMap = new HashMap<>();
            for (int itemTo : to) {
                View rowView = itemView.findViewById(itemTo);
                if (rowView != null) { //TODO NULL!!!!
                    View valueView = rowView.findViewById(R.id.core_recycler_item_value);
                    View labelView = itemView.findViewById(R.id.core_recycler_item_label);
                    valueViewLabelViewMap.put(valueView, labelView);
                }
            }
        }

        @Override
        public void bindView(@Nullable Cursor cursor, final int[] from, final int[] to) {
            if (cursor == null) return;

            int i = 0;
            for (Map.Entry<View, View> entry : valueViewLabelViewMap.entrySet()) {
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

        protected void bindLabelTextView(TextView labelView, CharSequence value) {
            labelView.setText(String.format("%s:", value));
        }

        protected void bindImageView(ImageView imageView, byte[] value) {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(value, 0, value.length));
        }
    }

}

package com.gsbelarus.gedemin.skeleton.core;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.viewhandler.CursorRecyclerAdapterViewHandler;

import java.util.LinkedHashMap;
import java.util.Map;


public class CoreCursorRecyclerAdapterViewHandler extends CursorRecyclerAdapterViewHandler {

    private int fieldsCount;

    public CoreCursorRecyclerAdapterViewHandler(CoreCursorRecyclerItemViewTypeModel... cursorViewTypeModelMap) {
        super(cursorViewTypeModelMap);
    }

    @Override
    public CoreCursorItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //TODO ItemViewTypes.DEFAULT_VIEW_TYPE
        CoreCursorRecyclerItemViewTypeModel itemViewTypeModel = (CoreCursorRecyclerItemViewTypeModel) getViewTypeModel(viewType);

        //inflater.inflate(itemViewTypeModel.getLayoutResource(), parent, false);
        LinkedHashMap<View, View> valueViewLabelViewMap = CoreUtils.includeCoreView(
                R.layout.core_recycler_item, parent, fieldsCount, CoreUtils.CoreViewType.LABELED_DATA_VIEW, null, null);
        View itemView = parent.getChildAt(parent.getChildCount()-1);
        parent.removeViewAt(parent.getChildCount()-1);

        //itemViewTypeModel.setTo(coreViewHelper.getTo());

        return new CoreCursorItemViewHolder(itemView,  new LinkedHashMap<>(valueViewLabelViewMap));
    }

    public void setFieldsCount(int fieldsCount) { //TODO tmp
        this.fieldsCount = fieldsCount;
    }


    public class CoreCursorItemViewHolder extends BasicCursorItemViewHolder {

        Map<View, View>  valueViewLabelViewMap;

        public CoreCursorItemViewHolder(View itemView, Map<View, View> toValueViewLabelViewMap) {
            super(itemView);

            valueViewLabelViewMap = toValueViewLabelViewMap;
        }

        @Override
        public void bindView(@Nullable Cursor cursor, final int[] from, @Nullable final int[] to) {
            CoreUtils.bindViews(cursor, from, valueViewLabelViewMap);
        }
    }

}

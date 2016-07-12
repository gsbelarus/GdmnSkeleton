package com.gsbelarus.gedemin.skeleton.core.view;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.item.ItemViewTypes;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.viewhandler.CursorRecyclerAdapterViewHandler;
import com.gsbelarus.gedemin.skeleton.core.util.CoreUtils;

import java.util.LinkedHashMap;
import java.util.Map;


public class CoreCursorRecyclerAdapterViewHandler extends CursorRecyclerAdapterViewHandler {

    private int fieldsCount;
    @Nullable
    private View.OnClickListener onEmptyRecyclerItemBtnClickListener;

    public CoreCursorRecyclerAdapterViewHandler(CoreCursorRecyclerItemViewTypeModel... cursorViewTypeModelMap) {
        super(cursorViewTypeModelMap);
    }

    @Override
    public BasicCursorItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ItemViewTypes.EMPTY_VIEW_TYPE) {
            return super.onCreateViewHolder(parent, viewType);
        }

        CoreCursorRecyclerItemViewTypeModel itemViewTypeModel = (CoreCursorRecyclerItemViewTypeModel) getViewTypeModel(viewType);
        LinkedHashMap<View, View> valueViewLabelViewMap = CoreUtils.includeCoreView(
                itemViewTypeModel.getLayoutResource(), parent, fieldsCount, CoreUtils.CoreViewType.LABELED_DATA_VIEW, null, null);
        View itemView = parent.getChildAt(parent.getChildCount()-1);
        parent.removeViewAt(parent.getChildCount()-1);

        //itemViewTypeModel.setTo(coreViewHelper.getTo());
        return new CoreCursorItemViewHolder(itemView,  new LinkedHashMap<>(valueViewLabelViewMap), viewType == ItemViewTypes.EMPTY_VIEW_TYPE ? onEmptyRecyclerItemBtnClickListener : null); //TODO
    }

    public void setFieldsCount(int fieldsCount) { //TODO tmp
        this.fieldsCount = fieldsCount;
    }

    public void setOnEmptyRecyclerItemBtnClickListener(@Nullable View.OnClickListener onEmptyRecyclerItemBtnClickListener) {
        this.onEmptyRecyclerItemBtnClickListener = onEmptyRecyclerItemBtnClickListener;
    }

    public class CoreCursorItemViewHolder extends BasicCursorItemViewHolder {

        private Map<View, View>  valueViewLabelViewMap;

        public CoreCursorItemViewHolder(View itemView, Map<View, View> toValueViewLabelViewMap, @Nullable View.OnClickListener onEmptyRecyclerItemBtnClickListener) {
            super(itemView);

            valueViewLabelViewMap = toValueViewLabelViewMap;

            if (onEmptyRecyclerItemBtnClickListener != null) {
                itemView.findViewById(R.id.empty_item_btn).setOnClickListener(onEmptyRecyclerItemBtnClickListener);
            }
        }

        @Override
        public void bindView(@Nullable Cursor cursor, final int[] from, @Nullable final int[] to) {
            CoreUtils.bindViews(cursor, from, valueViewLabelViewMap);
        }
    }

}

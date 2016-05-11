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
import com.gsbelarus.gedemin.skeleton.base.view.adapter.item.ItemViewTypes;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.viewhandler.CursorRecyclerAdapterViewHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class CoreCursorRecyclerAdapterViewHandler extends CursorRecyclerAdapterViewHandler {

    private CoreViewHelper coreViewHelper;


    public CoreCursorRecyclerAdapterViewHandler(CoreCursorRecyclerItemViewTypeModel... cursorViewTypeModelMap) {
        super(cursorViewTypeModelMap);

        coreViewHelper = new CoreViewHelper();
    }

    @Override
    public CoreCursorItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //TODO ItemViewTypes.DEFAULT_VIEW_TYPE
        View view = createItemView(parent, viewType); //inflater.inflate(itemViewTypeModel.getLayoutResource(), parent, false);
        return new CoreCursorItemViewHolder(view,  new LinkedHashMap<>(coreViewHelper.getValueViewLabelViewMap()));
    }

    private View createItemView(ViewGroup parent, int viewType) {
        CoreCursorRecyclerItemViewTypeModel itemViewTypeModel = (CoreCursorRecyclerItemViewTypeModel) getViewTypeModel(viewType);

        View itemView = coreViewHelper.generateCoreItemView(parent, itemViewTypeModel.getOriginalFrom().length);

        itemViewTypeModel.setTo(coreViewHelper.getTo());
        return itemView;
    }


    public class CoreCursorItemViewHolder extends BasicCursorItemViewHolder {

        Map<View, View>  valueViewLabelViewMap;

        public CoreCursorItemViewHolder(View itemView, Map<View, View> toValueViewLabelViewMap) {
            super(itemView);

            valueViewLabelViewMap = toValueViewLabelViewMap;
        }

        @Override
        public void bindView(@Nullable Cursor cursor, final int[] from, final int[] to) {
            CoreViewHelper.bindViews(cursor, from, valueViewLabelViewMap);
        }
    }

}

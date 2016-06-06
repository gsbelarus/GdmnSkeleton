package com.gsbelarus.gedemin.skeleton.core;


import android.support.annotation.LayoutRes;

import com.gsbelarus.gedemin.skeleton.base.view.adapter.item.CursorRecyclerItemViewTypeModel;

public class CoreCursorRecyclerItemViewTypeModel extends CursorRecyclerItemViewTypeModel {

    public CoreCursorRecyclerItemViewTypeModel(@LayoutRes int layoutResource){
        super(layoutResource, null, null);
   }

    public CoreCursorRecyclerItemViewTypeModel(@LayoutRes int layoutResource, String[] originalFrom) {
        super(layoutResource, originalFrom, null);
    }

    public CoreCursorRecyclerItemViewTypeModel(int viewTypeId, @LayoutRes int layoutResource, String[] originalFrom) {
        super(viewTypeId, layoutResource, originalFrom, null);
    }

}
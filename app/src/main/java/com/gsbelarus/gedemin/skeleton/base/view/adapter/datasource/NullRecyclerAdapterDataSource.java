package com.gsbelarus.gedemin.skeleton.base.view.adapter.datasource;

import android.support.annotation.Nullable;

/**
 *  использовать как NullObject
 *  */
public final class NullRecyclerAdapterDataSource<ITEM_T> implements RecyclerAdapterDataSource<ITEM_T> {

    @Override
    public int getItemCount() {
        return 0;
    }

    @Nullable
    @Override
    public ITEM_T getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewType(int position) {
        return 0;
    }

}
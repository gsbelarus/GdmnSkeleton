package com.gsbelarus.gedemin.skeleton.base.view.adapter.datasource;

import android.support.annotation.Nullable;


public interface RecyclerAdapterDataSource<ITEM_T> {

    int getItemCount();

    @Nullable
    ITEM_T getItem(int position);

    long getItemId(int position);

    int getViewType(int position);
}
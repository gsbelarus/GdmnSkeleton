package com.gsbelarus.gedemin.skeleton.base.view.adapter.viewhandler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 *  использовать как NullObject
 *  */

public class NullRecyclerAdapterViewHandler<VH_T extends RecyclerView.ViewHolder, ITEM_T>
        implements RecyclerAdapterViewHandler<VH_T, ITEM_T> {

    @Override
    public VH_T onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull VH_T holder, @Nullable ITEM_T dataItem, int viewType) {}
}
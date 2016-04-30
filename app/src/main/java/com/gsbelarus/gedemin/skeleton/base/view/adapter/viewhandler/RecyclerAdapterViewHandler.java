package com.gsbelarus.gedemin.skeleton.base.view.adapter.viewhandler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;


public interface RecyclerAdapterViewHandler<VH_T extends RecyclerView.ViewHolder, ITEM_T> {

    VH_T onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    void onBindViewHolder(@NonNull VH_T holder, @Nullable ITEM_T dataItem, int viewType);
}

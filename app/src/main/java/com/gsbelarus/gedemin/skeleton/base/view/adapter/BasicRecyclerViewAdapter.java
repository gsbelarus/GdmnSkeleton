package com.gsbelarus.gedemin.skeleton.base.view.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.gsbelarus.gedemin.skeleton.base.view.adapter.datasource.NullRecyclerAdapterDataSource;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.datasource.RecyclerAdapterDataSource;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.listener.OnRecyclerItemClickListener;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.viewhandler.NullRecyclerAdapterViewHandler;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.viewhandler.RecyclerAdapterViewHandler;


// TODO 1.empty view 2.onItemLongClickListener

public class BasicRecyclerViewAdapter<VH_T extends RecyclerView.ViewHolder, ITEM_T> extends RecyclerView.Adapter<VH_T> {

    public final String TAG = this.getClass().getCanonicalName();


    @NonNull
    private RecyclerAdapterDataSource<ITEM_T> adapterDataSource = new NullRecyclerAdapterDataSource<>();

    @NonNull
    private RecyclerAdapterViewHandler<VH_T, ITEM_T> adapterViewHandler = new NullRecyclerAdapterViewHandler<>();

    @Nullable
    private OnRecyclerItemClickListener onRecyclerItemClickListener;


    @Override
    public VH_T onCreateViewHolder(ViewGroup parent, int viewType) {
        VH_T viewHolder = adapterViewHandler.onCreateViewHolder(parent, viewType);

        onViewHolderCreated(viewHolder, viewType);

        return viewHolder;
    }

    public void onViewHolderCreated(final VH_T viewHolder, final int viewType) {
        viewHolder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onRecyclerItemClickListener != null) {
                            onRecyclerItemClickListener.onClick(view, viewHolder.getAdapterPosition(), viewType);
                        }
                    }
                }
        );
    }

    @Override
    public void onBindViewHolder(VH_T holder, int position) {
        ITEM_T item = adapterDataSource.getItem(position);
        int viewType = adapterDataSource.getViewType(position);

        adapterViewHandler.onBindViewHolder(holder, item, viewType);

        onViewHolderBound(holder, item, position, viewType);
    }

    public void onViewHolderBound(VH_T viewHolder, ITEM_T item, int position, int viewType) {}

    @Override
    public final int getItemViewType(int position) {
        // TODO getRecyclerAdapterDataSource().getVisibleDataCount() == 0 ? EMPTY VIEW
        return adapterDataSource.getViewType(position);
    }

    @Override
    public int getItemCount() {
        return adapterDataSource.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        return adapterDataSource.getItemId(position);
    }


    // accessors

    @NonNull
    public RecyclerAdapterDataSource<ITEM_T> getAdapterDataSource() {
        return adapterDataSource;
    }

    public void setAdapterDataSource(@NonNull RecyclerAdapterDataSource<ITEM_T> adapterDataSource) {
        this.adapterDataSource = adapterDataSource;
    }

    @Nullable
    public OnRecyclerItemClickListener getOnRecyclerItemClickListener() {
        return onRecyclerItemClickListener;
    }

    public void setOnRecyclerItemClickListener(@Nullable OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    @NonNull
    public RecyclerAdapterViewHandler<VH_T, ITEM_T> getAdapterViewHandler() {
        return adapterViewHandler;
    }

    public void setAdapterViewHandler(@NonNull RecyclerAdapterViewHandler<VH_T, ITEM_T> adapterViewHandler) {
        this.adapterViewHandler = adapterViewHandler;
    }
}

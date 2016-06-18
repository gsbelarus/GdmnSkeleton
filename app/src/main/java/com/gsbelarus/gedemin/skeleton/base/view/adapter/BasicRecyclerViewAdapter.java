package com.gsbelarus.gedemin.skeleton.base.view.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.gsbelarus.gedemin.skeleton.base.view.adapter.datasource.NullRecyclerAdapterDataSource;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.datasource.RecyclerAdapterDataSource;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.item.ItemViewTypes;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.listener.OnRecyclerItemClickListener;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.viewhandler.NullRecyclerAdapterViewHandler;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.viewhandler.RecyclerAdapterViewHandler;


// TODO 1.empty view 2.onItemLongClickListener

public class BasicRecyclerViewAdapter<VH_T extends RecyclerView.ViewHolder, ITEM_T> extends RecyclerView.Adapter<VH_T> {

    public final String TAG = this.getClass().getCanonicalName();

    private boolean showEmptyLayout;
//    private ITEM_T noDataValue;

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

        if (viewType != ItemViewTypes.EMPTY_VIEW_TYPE) {
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
    }

    @Override
    public void onBindViewHolder(VH_T holder, int position) {
        ITEM_T item = null;
        int viewType;
        //TODO
        if (!(adapterDataSource.getItemCount() == 0 && isShowEmptyLayout())) {
            item = adapterDataSource.getItem(position);
            viewType = adapterDataSource.getViewType(position);
        } else {
            viewType = ItemViewTypes.EMPTY_VIEW_TYPE;
        }

        adapterViewHandler.onBindViewHolder(holder, item, viewType);

        onViewHolderBound(holder, item, position, viewType);
    }

//    @Nullable
//    protected DATA getData(int position) {
//        if (getRecyclerAdapterDataSource().getVisibleDataCount() == 0 && hasNoDataLayout()) {
//            if (!hasNoDataValue()) return null;
//
//            if (noDataValue instanceof Cursor) {
//                ((Cursor) noDataValue).moveToPosition(position);
//            } else if (noDataValue instanceof List) {
//                return ((List<DATA>) noDataValue).get(position);
//            }
//            return noDataValue;
//        }
//        return getRecyclerAdapterDataSource().getVisibleData(position);
//    }

    public void onViewHolderBound(VH_T viewHolder, ITEM_T item, int position, int viewType) {}

    @Override
    public final int getItemViewType(int position) { //TODO
        if (adapterDataSource.getItemCount() == 0 && isShowEmptyLayout()) {
            return ItemViewTypes.EMPTY_VIEW_TYPE;
        }

        return adapterDataSource.getViewType(position);
    }

    @Override
    public int getItemCount() {
        int itemCount = adapterDataSource.getItemCount();
        return (itemCount <= 0 && isShowEmptyLayout()) ? 1 : itemCount;
    }

    @Override
    public long getItemId(int position) { //TODO
        return adapterDataSource.getItemId(position);
    }

//    public synchronized DATA swapNoDataValue(DATA noDataValue) {
//        DATA oldValue = this.noDataValue;
//        this.noDataValue = noDataValue;
//        return oldValue;
//    }

    // accessors

    public boolean isShowEmptyLayout() {
        return showEmptyLayout;
    }

    public void setShowEmptyLayout(boolean showEmptyLayout) {
        this.showEmptyLayout = showEmptyLayout;
    }

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

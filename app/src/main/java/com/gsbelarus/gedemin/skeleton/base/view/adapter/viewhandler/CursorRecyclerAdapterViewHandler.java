package com.gsbelarus.gedemin.skeleton.base.view.adapter.viewhandler;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gsbelarus.gedemin.skeleton.base.BasicUtils;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.item.CursorRecyclerItemViewTypeModel;

import java.util.HashMap;
import java.util.Map;


public class CursorRecyclerAdapterViewHandler
        implements RecyclerAdapterViewHandler<CursorRecyclerAdapterViewHandler.BasicCursorItemViewHolder, Cursor> {   //TODO <V extends ViewType, VH>

    private Map<Integer, CursorRecyclerItemViewTypeModel> viewTypeModelMap = new HashMap<>(); //TODO SparseArray


    public CursorRecyclerAdapterViewHandler(CursorRecyclerItemViewTypeModel... cursorViewTypeModelMap) {

        for (CursorRecyclerItemViewTypeModel itemViewTypeModel : cursorViewTypeModelMap) {
            viewTypeModelMap.put(itemViewTypeModel.getViewTypeId(), itemViewTypeModel);
        }
    }

    @Override
    public BasicCursorItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CursorRecyclerItemViewTypeModel itemViewTypeModel = getViewTypeModel(viewType);

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(itemViewTypeModel.getLayoutResource(), parent, false);
        return new BasicCursorItemViewHolder(view);
    }

    protected CursorRecyclerItemViewTypeModel getViewTypeModel(int viewType) {
        return viewTypeModelMap.get(viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull BasicCursorItemViewHolder holder, @Nullable Cursor dataItem, int viewType) {
//        if (dataItem == null) {
//            throw new IllegalStateException("couldn't move cursor to position ");
//        }
        CursorRecyclerItemViewTypeModel itemViewTypeModel = getViewTypeModel(viewType);

        if (dataItem != null) {
            holder.bindView(dataItem, itemViewTypeModel.getFrom(dataItem), itemViewTypeModel.getTo());
        }
    }


    public class BasicCursorItemViewHolder extends RecyclerView.ViewHolder {

        public BasicCursorItemViewHolder(View itemView) {
            super(itemView);
        }

        public void bindView(@Nullable Cursor cursor, final int[] from, final int[] to) {
            BasicUtils.bindViews(cursor, from, to, itemView);
        }
    }

}
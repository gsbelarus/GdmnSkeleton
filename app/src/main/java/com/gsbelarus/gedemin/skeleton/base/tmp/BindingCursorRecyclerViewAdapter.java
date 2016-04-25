package com.gsbelarus.gedemin.skeleton.base.tmp;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.BaseCursorRecyclerViewAdapter;


public class BindingCursorRecyclerViewAdapter extends BaseCursorRecyclerViewAdapter {

    protected LayoutInflater layoutInflater;
    private final Context appContext;
//    private final AttachedActivity attachedActivity;

    @LayoutRes
    protected int getLayoutId() {
        return R.layout.recycler_item;
    }


    public BindingCursorRecyclerViewAdapter(@Nullable Cursor dataCursor, @NonNull Context appContext) {
        super(dataCursor);

        this.appContext = appContext;
    }

    @Override
    public BaseCursorItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        final ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, getLayoutId(), parent, false);

        return new BindingItemViewHolder(binding);
    }

    public Context getAppContext() {
        return appContext;
    }


    public static class BindingItemViewHolder extends BaseCursorItemViewHolder {

        protected final ViewDataBinding binding;

        public BindingItemViewHolder(final ViewDataBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        @Override
        public void bind(Cursor cursor) {
            //TODO
            // viewModel.setCursor(cursor);
            //binding.executePendingBindings();
        }
    }
}

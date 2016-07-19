package com.gsbelarus.gedemin.skeleton.core.view.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.gsbelarus.gedemin.skeleton.R;

public class EmptyRecyclerView extends RecyclerView {

    @Nullable
    View emptyView;
    private @LayoutRes int mEmptyLayout;

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Log.d("qwerty", "attrs = " + attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EmptyRecyclerView);

        mEmptyLayout = a.getResourceId(R.styleable.EmptyRecyclerView_empty_layout, R.layout.core_empty_recycler_view);

        a.recycle();
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    void checkIfEmpty() {
        if (emptyView != null && getAdapter() != null) {
            emptyView.setVisibility(getAdapter().getItemCount() > 0 ? GONE : VISIBLE);
        }
    }

    final AdapterDataObserver observer = new AdapterDataObserver() {

        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmpty();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            checkIfEmpty();
        }
    };

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
    }

    public void setEmptyView(@Nullable ViewGroup rootView) {

//        TODO added R.id.frame_layout in xml
//        FrameLayout frameLayout = (FrameLayout) rootView.findViewById(R.id.frame_layout);
//        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
//        layoutInflater.inflate(mEmptyLayout, frameLayout, true);
//        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.empty_rec_view);

//        this.emptyView = linearLayout;
        checkIfEmpty();
    }
}
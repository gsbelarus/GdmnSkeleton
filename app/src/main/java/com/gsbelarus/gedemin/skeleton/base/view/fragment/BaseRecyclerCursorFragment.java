package com.gsbelarus.gedemin.skeleton.base.view.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.gsbelarus.gedemin.skeleton.base.view.adapter.BasicCursorRecyclerViewAdapter;
import com.gsbelarus.gedemin.skeleton.base.view.fragment.viewstate.BasicRecyclerFragmentState;


public abstract class BaseRecyclerCursorFragment<FRAGMENTSTATE_T extends BasicRecyclerFragmentState> extends BaseCursorFragment<FRAGMENTSTATE_T> {

    /**
     * Сonfiguration
     */
    @IdRes
    protected abstract int getRecyclerResId();


    private RecyclerView.LayoutManager layoutManager;


    protected abstract BasicCursorRecyclerViewAdapter getAdapter();
    protected abstract RecyclerView.LayoutManager createLayoutManager();


    @Override
    protected void onCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) rootView.findViewById(getRecyclerResId());
        setupRecyclerView(rv);
    }

    protected void setupRecyclerView(RecyclerView recyclerView) {
        layoutManager = createLayoutManager();
        if (getSavedFragmentState() != null && getSavedFragmentState().layoutManagerState != null) {
            layoutManager.onRestoreInstanceState(getSavedFragmentState().layoutManagerState);
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(getAdapter());
    }

    @Override
    protected void setDataCursor(@Nullable Cursor cursor) {
        getAdapter().swapCursor(cursor);
    }

    @Override
    protected FRAGMENTSTATE_T newInstanceState() {
        return (FRAGMENTSTATE_T) new BasicRecyclerFragmentState(this);
    }

    // accessors

    public RecyclerView.LayoutManager getLayoutManager() {
        return layoutManager;
    }


//    protected static class BasicRecyclerFragmentState extends BasicState {
//
//        private final Parcelable layoutManagerState;
//
//
//        protected BasicRecyclerFragmentState(BaseRecyclerCursorFragment fragment) {
//            super(fragment);
//            layoutManagerState = fragment.layoutManager.onSaveInstanceState();
//        }
//
//        public BasicRecyclerFragmentState(Parcel in) {
//            super(in);
//            layoutManagerState = in.readParcelable(RecyclerView.LayoutManager.class.getClassLoader());
//        }
//
//        @Override
//        public void writeToParcel(Parcel dest, int flags) {
//            super.writeToParcel(dest, flags);
//            dest.writeParcelable(layoutManagerState, flags);
//        }
//
//        public static Creator<BasicRecyclerFragmentState> CREATOR =
//                new Creator<BasicRecyclerFragmentState>() {
//                    @Override
//                    public BasicRecyclerFragmentState createFromParcel(Parcel source) {
//                        return new BasicRecyclerFragmentState(source);
//                    }
//
//                    @Override
//                    public BasicRecyclerFragmentState[] newArray(int size) {
//                        return new BasicRecyclerFragmentState[size];
//                    }
//                };
//
//    }

}

package com.gsbelarus.gedemin.skeleton.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.BaseCursorRecyclerFragment;
import com.gsbelarus.gedemin.skeleton.base.BaseCursorRecyclerViewAdapter;
import com.gsbelarus.gedemin.skeleton.base.CommonCursorLoader;
import com.gsbelarus.gedemin.skeleton.view.adapter.CoreCursorRecyclerViewAdapter;


public class MainFragment extends BaseCursorRecyclerFragment {

    /**
     * Сonfiguration
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_main;
    }

    CoreCursorRecyclerViewAdapter cursorAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cursorAdapter = new CoreCursorRecyclerViewAdapter(null);
    }

    @Override
    protected void doOnCreateView(View rootView, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        setupRecyclerView(rv);
    }

    @Override
    protected BaseCursorRecyclerViewAdapter getAdapter() {
        return cursorAdapter;
    }

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getContext());
    }


    @Override
    public CommonCursorLoader onCreateLoader(int id, Bundle args) {
        return new CommonCursorLoader(getContext(), databaseManager, "table1", null, null, null, null);
    }

}

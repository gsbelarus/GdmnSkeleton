package com.gsbelarus.gedemin.skeleton.app.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.app.view.component.decorator.DividerItemDecoration;
import com.gsbelarus.gedemin.skeleton.base.data.loader.BasicCursorLoader;
import com.gsbelarus.gedemin.skeleton.base.view.BaseCursorRecyclerFragment;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.BasicCursorRecyclerViewAdapter;
import com.gsbelarus.gedemin.skeleton.core.CoreCursorRecyclerAdapterViewHandler;
import com.gsbelarus.gedemin.skeleton.core.CoreCursorRecyclerItemViewTypeModel;
import com.gsbelarus.gedemin.skeleton.core.CoreDatabaseManager;


public class MainFragment extends BaseCursorRecyclerFragment {

    /**
     * Сonfiguration
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_main;
    }

    BasicCursorRecyclerViewAdapter cursorAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CoreCursorRecyclerItemViewTypeModel itemViewTypeModel = new CoreCursorRecyclerItemViewTypeModel(R.layout.core_recycler_item, new String[]{"_id"});

        cursorAdapter = new BasicCursorRecyclerViewAdapter(null, itemViewTypeModel.getLayoutResource(), null, null); //TODO
        CoreCursorRecyclerAdapterViewHandler viewHandler = new CoreCursorRecyclerAdapterViewHandler(itemViewTypeModel);
        cursorAdapter.setAdapterViewHandler(viewHandler);
    }

    @Override
    protected void doOnCreateView(View rootView, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        setupRecyclerView(rv);
    }

    @Override
    protected void setupRecyclerView(RecyclerView recyclerView) {
        super.setupRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected BasicCursorRecyclerViewAdapter getAdapter() {
        return cursorAdapter;
    }

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    @Override
    protected CoreDatabaseManager createDatabaseManager() {
        return CoreDatabaseManager.getInstance(getContext());
    }


    @Override
    public BasicCursorLoader onCreateLoader(int id, Bundle args) {
        return new BasicCursorLoader(getContext(), getDatabaseManager(), "table1", null, null, null, null);
    }

}

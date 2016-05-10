package com.gsbelarus.gedemin.skeleton.app.view.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.app.view.activity.DetailActivity;
import com.gsbelarus.gedemin.skeleton.app.view.component.decorator.DividerItemDecoration;
import com.gsbelarus.gedemin.skeleton.base.data.loader.BasicTableCursorLoader;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.listener.OnRecyclerItemClickListener;
import com.gsbelarus.gedemin.skeleton.base.view.fragment.BaseRecyclerCursorFragment;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.BasicCursorRecyclerViewAdapter;
import com.gsbelarus.gedemin.skeleton.core.CoreCursorRecyclerAdapterViewHandler;
import com.gsbelarus.gedemin.skeleton.core.CoreCursorRecyclerItemViewTypeModel;
import com.gsbelarus.gedemin.skeleton.core.CoreDatabaseManager;


public class MainRecyclerCursorFragment extends BaseRecyclerCursorFragment {

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

        CoreCursorRecyclerItemViewTypeModel itemViewTypeModel = new CoreCursorRecyclerItemViewTypeModel(
                R.layout.core_recycler_item,
                new String[]{BaseColumns._ID, "column2_CHAR_32767"});

        cursorAdapter = new BasicCursorRecyclerViewAdapter(null, itemViewTypeModel.getLayoutResource(), null, null); //TODO
        CoreCursorRecyclerAdapterViewHandler viewHandler = new CoreCursorRecyclerAdapterViewHandler(itemViewTypeModel);
        cursorAdapter.setAdapterViewHandler(viewHandler);
        cursorAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onClick(View view, int position, int viewType) {
                long id = getAdapter().getAdapterDataSource().getItemId(position); //TODO
                startActivity(DetailActivity.newStartIntent(getActivity(), id));
            }
        });
    }

    @Override
    protected void handleFragmentArguments(@NonNull Bundle arguments) {}

    @Override
    protected void handleSavedInstanceState(@NonNull Bundle savedInstanceState) {}

    @Override
    protected void handleIntentExtras(@NonNull Bundle extras) {}

    @Override
    protected void doOnCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {
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
    protected Cursor getDataCursor() {
        return getAdapter().getDataCursor();
    }


    @Override
    public BasicTableCursorLoader onCreateLoader(int id, Bundle args) {
        return new BasicTableCursorLoader(getContext(), getDatabaseManager(), "table1", null, null, null, null);
    }

}

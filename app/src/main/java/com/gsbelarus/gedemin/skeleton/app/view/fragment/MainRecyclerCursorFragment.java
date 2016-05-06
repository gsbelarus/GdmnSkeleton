package com.gsbelarus.gedemin.skeleton.app.view.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FilterQueryProvider;
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


public class MainRecyclerCursorFragment extends BaseRecyclerCursorFragment implements BasicCursorRecyclerViewAdapter.Callback {

    private SearchView searchView;
    private CoreDatabaseManager coreDatabaseManager;

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

        setHasOptionsMenu(true);

        coreDatabaseManager = createDatabaseManager();

        CoreCursorRecyclerItemViewTypeModel itemViewTypeModel = new CoreCursorRecyclerItemViewTypeModel(
                R.layout.core_recycler_item,
                new String[]{BaseColumns._ID, "column2_CHAR_32767"});

        cursorAdapter = new BasicCursorRecyclerViewAdapter(null, itemViewTypeModel.getLayoutResource(), null, null, this); //TODO
        CoreCursorRecyclerAdapterViewHandler viewHandler = new CoreCursorRecyclerAdapterViewHandler(itemViewTypeModel);
        cursorAdapter.setAdapterViewHandler(viewHandler);
        cursorAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onClick(View view, int position, int viewType) {
                long id = getAdapter().getAdapterDataSource().getItemId(position); //TODO
                startActivity(DetailActivity.newStartIntent(getActivity(), id));
            }
        });
        cursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (!constraint.toString().isEmpty())
                    return coreDatabaseManager.select("table1", null, "_id LIKE ?", new String[]{"%" + constraint.toString() + "%"}, null);
                else return coreDatabaseManager.select("table1", null, null, null, null);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setMaxWidth(10000); // TODO: 05.05.2016 searchview width

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (cursorAdapter != null) {
                    cursorAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });
    }

    @Override
    public void updateDataCursor(@Nullable Cursor cursor) {
        swapCursor(cursor);
    }
}

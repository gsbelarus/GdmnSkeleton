package com.gsbelarus.gedemin.skeleton.app.view.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FilterQueryProvider;
import android.view.ViewGroup;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.app.view.RequestCode;
import com.gsbelarus.gedemin.skeleton.app.view.activity.DetailActivity;
import com.gsbelarus.gedemin.skeleton.app.view.activity.EditActivity;
import com.gsbelarus.gedemin.skeleton.app.view.component.DividerItemDecoration;
import com.gsbelarus.gedemin.skeleton.base.data.loader.BasicTableCursorLoader;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.listener.OnRecyclerItemClickListener;
import com.gsbelarus.gedemin.skeleton.base.view.fragment.BaseRecyclerCursorFragment;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.BasicCursorRecyclerViewAdapter;
import com.gsbelarus.gedemin.skeleton.core.CoreCursorRecyclerAdapterViewHandler;
import com.gsbelarus.gedemin.skeleton.core.CoreCursorRecyclerItemViewTypeModel;
import com.gsbelarus.gedemin.skeleton.core.CoreDatabaseManager;


public class MainRecyclerCursorFragment extends BaseRecyclerCursorFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;

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

        CoreCursorRecyclerItemViewTypeModel itemViewTypeModel = new CoreCursorRecyclerItemViewTypeModel(
                R.layout.core_recycler_item,
                new String[]{BaseColumns._ID, "column2_CHAR_32767"});

        cursorAdapter = new BasicCursorRecyclerViewAdapter(itemViewTypeModel.getLayoutResource(), null, null); //TODO
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
                    return getDatabaseManager().select("table1", null, "_id LIKE ?", new String[]{"%" + constraint.toString() + "%"}, null);
                else return getDatabaseManager().select("table1", null, null, null, null);
            }
        });
    }

    @Override
    protected void doOnCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        setupRecyclerView(rv);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        fab.setOnClickListener(this);

        initRefreshLayout(rootView);
    }

    protected final void initRefreshLayout(ViewGroup rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getIntArray(R.array.swipe_refresh));
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        //TODO
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;

        if (v.getId() == R.id.fab_add) {
            getDatabaseManager().beginTransaction();
            Long dataId = getDatabaseManager().insert("table1", "column1_BIGINT", new ContentValues());
            if (dataId != null) startActivityForResult(EditActivity.newStartIntent(getActivity(), dataId), RequestCode.REQUEST_CODE_EDIT_CHANGED);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestCode.REQUEST_CODE_EDIT_CHANGED) {
            if (resultCode == Activity.RESULT_OK) {
                getDatabaseManager().commitTransaction();
            } else {
                getDatabaseManager().cancelTransaction();
            }
        }
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

}

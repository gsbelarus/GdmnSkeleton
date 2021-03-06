package com.gsbelarus.gedemin.skeleton.core.view.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FilterQueryProvider;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.BasicAccountHelper;
import com.gsbelarus.gedemin.skeleton.base.data.loader.BasicTableCursorLoader;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.BasicCursorRecyclerViewAdapter;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.item.ItemViewTypes;
import com.gsbelarus.gedemin.skeleton.base.view.fragment.BaseRecyclerCursorFragment;
import com.gsbelarus.gedemin.skeleton.core.data.CoreContract;
import com.gsbelarus.gedemin.skeleton.core.data.CoreDatabaseManager;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;
import com.gsbelarus.gedemin.skeleton.core.view.CoreCursorRecyclerAdapterViewHandler;
import com.gsbelarus.gedemin.skeleton.core.view.CoreCursorRecyclerItemViewTypeModel;
import com.gsbelarus.gedemin.skeleton.app.view.activity.AuthActivity;
import com.gsbelarus.gedemin.skeleton.core.view.component.DividerItemDecoration;
import com.gsbelarus.gedemin.skeleton.core.view.fragment.viewstate.CoreRecyclerFragmentState;

import java.util.Arrays;


public class CoreSearchableRecyclerCursorFragment extends BaseRecyclerCursorFragment<CoreRecyclerFragmentState> {

    /**
     * Configuration
     */

    @Override
    protected int getLayoutResource() { //TODO
        return R.layout.fragment_main;
    } //TODO add core

    @Override
    protected int getRecyclerResId() {
        return R.id.recycler_view;
    }


    private BasicCursorRecyclerViewAdapter cursorAdapter;
    private CoreCursorRecyclerItemViewTypeModel itemViewTypeModel;

    private SearchView searchView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        cursorAdapter = new BasicCursorRecyclerViewAdapter();
        cursorAdapter.setShowEmptyLayout(true);

        itemViewTypeModel =
                new CoreCursorRecyclerItemViewTypeModel(ItemViewTypes.DEFAULT_VIEW_TYPE, R.layout.core_recycler_item);
        CoreCursorRecyclerItemViewTypeModel emptyItemViewTypeModel =
                new CoreCursorRecyclerItemViewTypeModel(ItemViewTypes.EMPTY_VIEW_TYPE, R.layout.core_recycler_empty_item);

        CoreCursorRecyclerAdapterViewHandler viewHandler = new CoreCursorRecyclerAdapterViewHandler(itemViewTypeModel, emptyItemViewTypeModel);
        viewHandler.setOnEmptyRecyclerItemBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.d("OnEmptyRecyclerItemBtnClickListener");

                startActivity(new Intent(getContext(), AuthActivity.class));

            }
        });
        cursorAdapter.setAdapterViewHandler(viewHandler);
    }

    @Override
    protected void onCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {
        super.onCreateView(rootView, savedInstanceState);

        rootView.setFocusableInTouchMode(true);
        rootView.setOnKeyListener(
                new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            return pressBackHandle();
                        }
                        return false;
                    }
                }
        );
    }

    @Override
    protected void setupRecyclerView(RecyclerView recyclerView) {
        super.setupRecyclerView(recyclerView);

//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                getDatabaseManager().delete("Categories",
//                        BaseColumns._ID + " = ?",
//                        new String[]{String.valueOf(getAdapter().getItemId(viewHolder.getAdapterPosition()))});
//                getDatabaseManager().notifyDataChanged();
//            }
//        }).attachToRecyclerView(recyclerView);
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
        return CoreDatabaseManager.getInstance(getContext(), BasicAccountHelper.getSelectedAccount(getContext()));
    }

    @Override
    protected Cursor getDataCursor() {
        return getAdapter().getDataCursor();
    }

    @Override
    public BasicTableCursorLoader onCreateLoader(int id, Bundle args) { //TODO
        return new BasicTableCursorLoader(getContext(), getDatabaseManager(), CoreContract.TEST_TABLE, null, null, null, null);
    }

    @Override
    protected void bindViewOnCursorLoaded() {
        super.bindViewOnCursorLoaded();

        if (getDataCursor() != null) {
            String[] from = itemViewTypeModel.getOriginalFrom(getDataCursor());
            StringBuilder stringBuilder = new StringBuilder();
            for (String column : from) {
                stringBuilder.append(column + " " + "LIKE ?");
                if (!column.equals(from[from.length - 1]))
                    stringBuilder.append(" OR ");
            }
            final String selection = stringBuilder.toString();

            final String[] selectionArgs = new String[from.length];

            cursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
                @Override
                public Cursor runQuery(CharSequence constraint) {
                    if (!constraint.toString().isEmpty()) {
                        Arrays.fill(selectionArgs, "%" + constraint.toString() + "%");

                        return getDatabaseManager().select(CoreContract.TEST_TABLE, null, selection, selectionArgs, null);
                    } else {
                        return getDatabaseManager().select(CoreContract.TEST_TABLE, null, null, null, null);
                    }
                }
            });
            ((CoreCursorRecyclerAdapterViewHandler) getAdapter().getAdapterViewHandler()).setFieldsCount(getDataCursor().getColumnCount());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.core_search, menu);
        MenuItem menuItem = menu.findItem(R.id.core_action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setMaxWidth(10000); //  hack
        searchView.setImeOptions(EditorInfo.IME_FLAG_NO_FULLSCREEN);
        searchView.setLayoutParams(new SearchView.LayoutParams(SearchView.LayoutParams.MATCH_PARENT, SearchView.LayoutParams.MATCH_PARENT));
        searchView.requestLayout();

        //убираем иконку из hint
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        SpannableStringBuilder ssb = new SpannableStringBuilder("");
        ssb.append("Поиск...");
        searchAutoComplete.setHint(ssb);
        //        try {
        //            // меняем курсор
        //            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
        //            f.setAccessible(true);
        //            f.set(searchAutoComplete, R.drawable.edittext_cursor_white);
        //        } catch (Exception ignored) {
        //        }
        // убираем полосу снизу
        View searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchPlate.setBackgroundColor(Color.TRANSPARENT);
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

        if (getSavedFragmentState() != null && getSavedFragmentState().savedSearchFilterQuery != null){
            searchView.setQuery(getSavedFragmentState().savedSearchFilterQuery, true);
            searchView.setIconified(false);
        }
    }

    private boolean pressBackHandle() {
        if (searchView != null && !searchView.isIconified()) {
            searchView.setQuery(null, true);
            searchView.setIconified(true);
            return true;
        }
        return false;
    }

    @Override
    protected CoreRecyclerFragmentState newInstanceState() {
        return new CoreRecyclerFragmentState(this);
    }

    public String getSearchQuery() { //TODO tmp
        return searchView != null && !searchView.isIconified() ? searchView.getQuery().toString() : null;
    }
}

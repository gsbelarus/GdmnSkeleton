package com.gsbelarus.gedemin.skeleton.core.view.fragment;

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
import com.gsbelarus.gedemin.skeleton.base.data.loader.BasicTableCursorLoader;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.BasicCursorRecyclerViewAdapter;
import com.gsbelarus.gedemin.skeleton.base.view.fragment.BaseRecyclerCursorFragment;
import com.gsbelarus.gedemin.skeleton.core.data.CoreContract;
import com.gsbelarus.gedemin.skeleton.core.data.CoreDatabaseManager;
import com.gsbelarus.gedemin.skeleton.core.view.CoreCursorRecyclerAdapterViewHandler;
import com.gsbelarus.gedemin.skeleton.core.view.CoreCursorRecyclerItemViewTypeModel;
import com.gsbelarus.gedemin.skeleton.core.view.component.DividerItemDecoration;
import com.gsbelarus.gedemin.skeleton.core.view.component.EmptyRecyclerView;

import java.util.Arrays;


public class CoreSearchableRecyclerCursorFragment extends BaseRecyclerCursorFragment {

    /**
     * Сonfiguration
     */
    @Override
    protected int getLayoutResource() { //TODO
        return R.layout.fragment_main;
    } //TODO add core


    private BasicCursorRecyclerViewAdapter cursorAdapter;
    private CoreCursorRecyclerItemViewTypeModel itemViewTypeModel;

    private SearchView searchView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        itemViewTypeModel = new CoreCursorRecyclerItemViewTypeModel(
                R.layout.core_recycler_item);
//                new String[]{BaseColumns._ID, "column2_CHAR_32767"});

        cursorAdapter = new BasicCursorRecyclerViewAdapter(itemViewTypeModel.getLayoutResource(), null, null); //TODO
        CoreCursorRecyclerAdapterViewHandler viewHandler = new CoreCursorRecyclerAdapterViewHandler(itemViewTypeModel);
        cursorAdapter.setAdapterViewHandler(viewHandler);
    }

    @Override
    protected void doOnCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {

        EmptyRecyclerView emptyRecyclerView = (EmptyRecyclerView) rootView.findViewById(R.id.recycler_view);
        setupRecyclerView(emptyRecyclerView, rootView);

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
    protected void setupRecyclerView(EmptyRecyclerView recyclerView, ViewGroup rootView) {
        super.setupRecyclerView(recyclerView, rootView);

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
        recyclerView.setEmptyView(rootView);
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
    public BasicTableCursorLoader onCreateLoader(int id, Bundle args) { //TODO
        return new BasicTableCursorLoader(getContext(), getDatabaseManager(), CoreContract.TEST_TABLE, null, null, null, null);
    }

    @Override
    protected void bindViewOnCursorLoaded() { //TODO  move in swap
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
    }

    private boolean pressBackHandle() {
        if (searchView != null && !searchView.isIconified()) {
            searchView.setQuery("", true);
            searchView.setIconified(true);
            return true;
        }
        return false;
    }

}

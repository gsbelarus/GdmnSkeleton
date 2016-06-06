package com.gsbelarus.gedemin.skeleton.core;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.app.view.activity.EditActivity;
import com.gsbelarus.gedemin.skeleton.base.data.BaseDatabaseManager;
import com.gsbelarus.gedemin.skeleton.base.data.loader.BasicTableCursorLoader;
import com.gsbelarus.gedemin.skeleton.base.view.fragment.BaseDetailCursorFragment;

import java.util.LinkedHashMap;


public class CoreDetailCursorFragment extends BaseDetailCursorFragment {

    private Snackbar snackOnDelete;
    private boolean canRemove;

    @Override
    protected int getLayoutResource() {
        return R.layout.core_fragment;
    }


    protected LinkedHashMap<View, View> toValueViewLabelViewMap; //TODO SparseIntArray, ArrayMap, SimpleArrayMap


    public static CoreDetailCursorFragment newInstance(long dataId) {
        return newInstance(CoreDetailCursorFragment.class, dataId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setOriginalFrom(new String[] {
                BaseColumns._ID,
                "column1_BIGINT",
                "column2_CHAR_32767",
                "column3_DATE",
                "column4_DECIMAL_18_18",
                "column5_FLOAT",
                "column6_INTEGER",
                "column7_NUMERIC_18_18",
                "column8_SMALLINT",
                "column9_TIME",
                "column10_TIMESTAMP",
                "column11_VARCHAR_32765"}); //TODO tmp

        setHasOptionsMenu(true);
    }

    @Override
    protected void doOnCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {
        toValueViewLabelViewMap = CoreUtils.includeCoreDetailView(rootView, getOriginalFrom().length);
    }

    @Override
    protected void bindViewOnCursorLoaded() {
        CoreUtils.bindViews(getDataCursor(), getOriginalFrom(), toValueViewLabelViewMap);
    }

    @Override
    protected BaseDatabaseManager createDatabaseManager() {
        return CoreDatabaseManager.getInstance(getContext());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) { //TODO
        return new BasicTableCursorLoader(getContext(), getDatabaseManager(), "table1", null, BaseColumns._ID + " = ?", new String[] {String.valueOf(getDataId())}, null);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.core_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.core_action_edit:
                startActivity(EditActivity.newStartIntent(getActivity(), getDataId())); //TODO
                break;
            case R.id.core_action_delete:
                snackOnDelete = Snackbar.make(getView(), "Запись удалена" + ".", Snackbar.LENGTH_LONG);
                snackOnDelete.setAction("Отменить", new View.OnClickListener() {
                            public void onClick(View v) {
                                getDatabaseManager().cancelTransaction();
                                canRemove = false;
                            }
                        });

                snackOnDelete.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                        getDatabaseManager().beginTransaction();
                        getDatabaseManager().delete("table1", BaseColumns._ID + " = ?", new String[]{String.valueOf(getDataId())});

                        canRemove = true;
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        if (canRemove) {
                            getDatabaseManager().commitTransaction();
                            canRemove = false;

                            getDatabaseManager().notifyDataChanged();
                            getActivity().finish();
                        }
                    }
                });
                snackOnDelete.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (snackOnDelete != null) snackOnDelete.dismiss();
    }

}

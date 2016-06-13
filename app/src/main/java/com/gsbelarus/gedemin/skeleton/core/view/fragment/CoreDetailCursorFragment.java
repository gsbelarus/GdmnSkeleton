package com.gsbelarus.gedemin.skeleton.core.view.fragment;

import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
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
import com.gsbelarus.gedemin.skeleton.base.view.fragment.BaseFragment;
import com.gsbelarus.gedemin.skeleton.core.data.CoreContract;
import com.gsbelarus.gedemin.skeleton.core.data.CoreDatabaseManager;
import com.gsbelarus.gedemin.skeleton.core.util.CoreUtils;
import com.gsbelarus.gedemin.skeleton.core.view.fragment.viewstate.CoreDetailFragmentState;

import java.util.LinkedHashMap;


public class CoreDetailCursorFragment<FRAGMENTSTATE_T extends CoreDetailFragmentState> extends BaseDetailCursorFragment<FRAGMENTSTATE_T> {

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

        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getSavedFragmentState() != null) {
            canRemove = getSavedFragmentState().canRemove;
            if (canRemove) {
                canRemove = false;
                showDeleteSnack();
            }
        }
    }

    @Override
    protected void bindViewOnCursorLoaded() {
        if (getOriginalFrom() == null)
            setOriginalFrom(getDataCursor().getColumnNames());

        toValueViewLabelViewMap = CoreUtils.includeCoreDetailView((ViewGroup) getView(), getOriginalFrom().length);

        CoreUtils.bindViews(getDataCursor(), getOriginalFrom(), toValueViewLabelViewMap);
    }

    @Override
    protected BaseDatabaseManager createDatabaseManager() {
        return CoreDatabaseManager.getInstance(getContext());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) { //TODO
        return new BasicTableCursorLoader(getContext(), getDatabaseManager(), CoreContract.TEST_TABLE, null, BaseColumns._ID + " = ?", new String[] {String.valueOf(getDataId())}, null);
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
                showDeleteSnack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteSnack() {
        snackOnDelete = Snackbar.make(getView(), "Запись удалена" + ".", Snackbar.LENGTH_LONG);
        snackOnDelete.setAction("Отменить", new View.OnClickListener() {
            public void onClick(View v) {
                getDatabaseManager().cancelTransaction();
                canRemove = false;
            }
        });

        snackOnDelete.setCallback(new Snackbar.Callback() {
            @Override
            public void onShown(Snackbar snackbar) {
                super.onShown(snackbar);

                getDatabaseManager().beginTransaction();
                getDatabaseManager().delete(CoreContract.TEST_TABLE, BaseColumns._ID + " = ?", new String[]{String.valueOf(getDataId())});

                canRemove = true;
            }

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);

                if (event != Snackbar.Callback.DISMISS_EVENT_MANUAL && event != Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE) { //TODO  tmp DISMISS_EVENT_CONSECUTIVE
                    if (canRemove) {
                        getDatabaseManager().commitTransaction();
                        canRemove = false;

                        getDatabaseManager().notifyDataChanged();
                        getActivity().finish();
                    }
                } else { /* при смене ориентации DISMISS_EVENT_MANUAL. нигде вручную НЕ ВЫЗЫВАТЬ dismiss - не удалится запись! */
                    if (canRemove) {
                        if (getDatabaseManager().inTransaction()) getDatabaseManager().cancelTransaction();
                    }
                }
            }
        });

        snackOnDelete.show();
    }

    @Override
    protected FRAGMENTSTATE_T newInstanceState() {
        return (FRAGMENTSTATE_T) new CoreDetailFragmentState(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //TODO orientation
    }

    // accessors tmp

    public boolean isCanRemove() {
        return canRemove;
    }

}

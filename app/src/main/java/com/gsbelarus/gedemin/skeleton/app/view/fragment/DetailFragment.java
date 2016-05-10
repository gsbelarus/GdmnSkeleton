package com.gsbelarus.gedemin.skeleton.app.view.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.ViewGroup;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.data.BaseDatabaseManager;
import com.gsbelarus.gedemin.skeleton.base.data.loader.BasicTableCursorLoader;
import com.gsbelarus.gedemin.skeleton.base.view.fragment.BaseCursorFragment;
import com.gsbelarus.gedemin.skeleton.core.CoreDatabaseManager;
import com.gsbelarus.gedemin.skeleton.core.CoreViewHelper;

import java.util.Map;


public class DetailFragment extends BaseCursorFragment {

    public static final String ARGUMENT_KEY_DATA_ID = "data_id";

    private Cursor dataCursor;
    private long dataId;
    private String[] originalFrom;
    private Map<View, View> valueViewLabelViewMap; //TODO LinkedHashMap

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_detail;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        originalFrom = new String[] {
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
                "column11_VARCHAR_32765"}; //TODO
    }

    @Override
    protected void handleFragmentArguments(@NonNull Bundle arguments) {
        if (arguments.containsKey(ARGUMENT_KEY_DATA_ID)) {
            dataId = arguments.getLong(ARGUMENT_KEY_DATA_ID);
        }
    }

    @Override
    protected void handleSavedInstanceState(@NonNull Bundle savedInstanceState) {}

    @Override
    protected void handleIntentExtras(@NonNull Bundle extras) {}

    @Override
    protected void doOnCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {
        CoreViewHelper coreViewHelper = new CoreViewHelper();
        View detailView = coreViewHelper.generateCoreDetailView(rootView, originalFrom.length);

        valueViewLabelViewMap = coreViewHelper.getValueViewLabelViewMap();
        rootView.addView(detailView);
    }

    @Override
    protected BaseDatabaseManager createDatabaseManager() {
        return CoreDatabaseManager.getInstance(getContext());
    }

    @Override
    protected Cursor getDataCursor() {
        if (dataCursor != null) dataCursor.moveToFirst(); //TODO
        return dataCursor;
    }

    @Override
    protected void setDataCursor(@Nullable Cursor cursor) {
        dataCursor = cursor;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new BasicTableCursorLoader(getContext(), getDatabaseManager(), "table1", null, BaseColumns._ID + " = ?", new String[] {String.valueOf(dataId)}, null);
    }

    @Override
    protected void bindViewOnCursorLoaded() {

        CoreViewHelper.bindViews(getDataCursor(), originalFrom, valueViewLabelViewMap);
    }


    public static <T extends Fragment> T newInstance(long dataId) {
        Bundle argsBundle = new Bundle();
        argsBundle.putLong(ARGUMENT_KEY_DATA_ID, dataId);

        return (T) newInstance(DetailFragment.class, argsBundle);
    }
}

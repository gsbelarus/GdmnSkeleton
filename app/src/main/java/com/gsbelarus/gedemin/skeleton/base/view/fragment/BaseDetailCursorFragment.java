package com.gsbelarus.gedemin.skeleton.base.view.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gsbelarus.gedemin.skeleton.base.BasicUtils;


abstract public class BaseDetailCursorFragment extends BaseCursorFragment {

    public static final String ARGUMENT_KEY_DATA_ID = "data_id";

    private Cursor dataCursor;
    private long dataId;

    private String[] originalFrom;
    private int[] to;


    public static <T extends BaseDetailCursorFragment> T newInstance(Class<T> cl, long dataId) { //TODO originalFrom, to
        Bundle argsBundle = new Bundle();
        argsBundle.putLong(ARGUMENT_KEY_DATA_ID, dataId);

        return (T) newInstance(cl, argsBundle);
    }

    @Override
    protected void handleFragmentArguments(@NonNull Bundle arguments) {
        if (arguments.containsKey(ARGUMENT_KEY_DATA_ID)) {
            dataId = arguments.getLong(ARGUMENT_KEY_DATA_ID);
        }
    }

    @Override
    protected void handleSavedInstanceState(@NonNull Bundle savedInstanceState) {
        //TODO
    }

    @Override
    protected Cursor getDataCursor() {
        if (dataCursor != null) dataCursor.moveToFirst(); //TODO ?
        return dataCursor;
    }

    @Override
    protected void setDataCursor(@Nullable Cursor cursor) {
        dataCursor = cursor;
    }

    @Override
    protected void bindViewOnCursorLoaded() {
        BasicUtils.bindViews(getDataCursor(), originalFrom, to, getView());
    }


    // accessors

    public long getDataId() {
        return dataId;
    }

    public String[] getOriginalFrom() {
        return originalFrom;
    }

    public void setOriginalFrom(String[] originalFrom) {
        this.originalFrom = originalFrom;
    }

    public int[] getTo() {
        return to;
    }

    public void setTo(int[] to) {
        this.to = to;
    }

}
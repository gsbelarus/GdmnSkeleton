package com.gsbelarus.gedemin.skeleton.core;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.gsbelarus.gedemin.skeleton.R;


public class CoreEditCursorFragment extends CoreDetailCursorFragment {

    private boolean dataChanged;
    private boolean dataSaved; // было хотя бы одино сохранение
    private View.OnKeyListener onKeyBackListener;
    private MenuItem saveMenuItem;


    public static CoreEditCursorFragment newInstance(long dataId) { // called from reflection
        return newInstance(CoreEditCursorFragment.class, dataId);
    }

    @Override
    protected void doOnCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {
        onKeyBackListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    pressBackHandle();
                    return true;
                }
                return false;
            }
        };

        //
    }

    @Override
    protected void bindViewOnCursorLoaded() {
        TextWatcher valueChangedTextWatcher = new TextWatcherAdapter() {

            private String oldValue;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                super.beforeTextChanged(s, start, count, after);

                oldValue = s.toString();
            }

            @Override
            public void onTextChanged(String newValue) {
                if (!newValue.equals(oldValue)) setDataChanged(true);
            }

        };


        if (getOriginalFrom() == null)
            setOriginalFrom(getDataCursor().getColumnNames());

        toValueViewLabelViewMap = CoreUtils.includeCoreEditView((ViewGroup) getView(), getOriginalFrom().length, onKeyBackListener, valueChangedTextWatcher);

        CoreUtils.bindViews(getDataCursor(), getOriginalFrom(), toValueViewLabelViewMap);

        setDataChanged(false); // т.к. valueChangedTextWatcher сработал
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.core_edit, menu);

        saveMenuItem = menu.findItem(R.id.core_action_save);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.core_action_save:
                saveData();
                break;
            case android.R.id.home:
                pressBackHandle();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveData() {
        getDatabaseManager().update(CoreContract.TEST_TABLE, getContentValues(), BaseColumns._ID + " = ?", new String[] {String.valueOf(getDataId())}); //TODO
        getDatabaseManager().notifyDataChanged();

        setDataChanged(false);
        dataSaved = true;
    }

    private ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        View[] valueViews = toValueViewLabelViewMap.keySet().toArray(new View[toValueViewLabelViewMap.keySet().size()]);

        for (int i = 0; i < valueViews.length; i++) { //TODO
            contentValues.put(getDataCursor().getColumnName(i), String.valueOf(((EditText)valueViews[i]).getText()));
        }

        return contentValues;
    }

    public void onResume() {
        super.onResume();

        getView().setFocusable(true);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(onKeyBackListener);
    }

    @Override
    public void onStop() {
        super.onStop();

//        getView().setFocusable(false);
//        getView().setFocusableInTouchMode(false);
//        //getView().requestFocus();
//        getView().setOnKeyListener(null);
    }

    private void pressBackHandle() {

        if (dataChanged) {
            //TODO не пересоздавать
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());//, R.style.AppCompatAlertDialogStyle);
            builder.setMessage("Отменить несохраненные изменения и завершить редактирование?")
                    .setPositiveButton("Отменить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finishWithResult();
                        }
                    })
                    .setNegativeButton("Продолжить редактирование", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        } else {
            finishWithResult();
        }
    }

    private void finishWithResult() {
        getActivity().setResult(dataSaved ? Activity.RESULT_OK : Activity.RESULT_CANCELED); // RESULT_CANCELED - rollback create row
        getActivity().finish();
    }

    private void setDataChanged(boolean dataChanged) {
        this.dataChanged = dataChanged;

        if (saveMenuItem != null) {
            saveMenuItem.setEnabled(dataChanged);
        }
    }

}
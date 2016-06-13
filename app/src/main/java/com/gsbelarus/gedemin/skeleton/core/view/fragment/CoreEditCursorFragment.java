package com.gsbelarus.gedemin.skeleton.core.view.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.core.view.TextWatcherAdapter;
import com.gsbelarus.gedemin.skeleton.core.data.CoreContract;
import com.gsbelarus.gedemin.skeleton.core.util.CoreUtils;
import com.gsbelarus.gedemin.skeleton.core.view.fragment.viewstate.CoreEditFragmentState;

//TODO save focus, edittext values, check tarnsact restore
public class CoreEditCursorFragment extends CoreDetailCursorFragment<CoreEditFragmentState> {

    private boolean dataChanged;
    private boolean dataSaved; // было хотя бы одно сохранение

    private boolean confirmDlgShowing;


    private View.OnKeyListener onKeyBackListener;
    private MenuItem saveMenuItem;


    public static CoreEditCursorFragment newInstance(long dataId) { // called from reflection
        return newInstance(CoreEditCursorFragment.class, dataId);
    }

    @Override
    protected void onCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {
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

        if (getSavedFragmentState() != null) {
            dataChanged = getSavedFragmentState().dataChanged;
            dataSaved = getSavedFragmentState().dataSaved;
            confirmDlgShowing = getSavedFragmentState().confirmDlgShowing;
        }
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

        boolean oldDataChanged = dataChanged;

        CoreUtils.bindViews(getDataCursor(), getOriginalFrom(), toValueViewLabelViewMap);

        setDataChanged(oldDataChanged); // т.к. valueChangedTextWatcher сработал

        //restoreInputFocus();
    }

    private void restoreInputFocus() { //TODO id
        if (getSavedFragmentState() != null) {

            final EditText et = (EditText)toValueViewLabelViewMap.keySet().iterator().next();


            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (!et.hasFocus()) {
                et.requestFocus();
            }

            et.post(new Runnable() {
                @Override
                public void run() {
                    imm.showSoftInput(et, InputMethodManager.SHOW_FORCED);
                }
            });
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (confirmDlgShowing) confirmDlgShow();
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
        View[] valueViews = toValueViewLabelViewMap.keySet().<View>toArray(new View[toValueViewLabelViewMap.keySet().size()]);

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
            confirmDlgShow();
        } else {
            finishWithResult();
        }
    }

    private void confirmDlgShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());//, R.style.AppCompatAlertDialogStyle);

        AlertDialog dialog  = builder.setMessage("Отменить несохраненные изменения и завершить редактирование?")
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
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        confirmDlgShowing = false;
                    }
                }).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                confirmDlgShowing = true;
            }
        });

        dialog.show();
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

    @Override
    protected CoreEditFragmentState newInstanceState() {
        return new CoreEditFragmentState(this);
    }

    // accessors tmp

    public boolean isDataChanged() {
        return dataChanged;
    }

    public boolean isDataSaved() {
        return dataSaved;
    }

    public boolean isConfirmDlgShowing() {
        return confirmDlgShowing;
    }
}
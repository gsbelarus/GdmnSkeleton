package com.gsbelarus.gedemin.skeleton.core;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gsbelarus.gedemin.skeleton.R;


public class CoreEditCursorFragment extends CoreDetailCursorFragment {

    View.OnKeyListener onKeyBackListener;

    @Override
    protected int getLayoutResource() {
        return R.layout.core_fragment;
    }


    public static CoreEditCursorFragment newInstance(long dataId) {
        return newInstance(CoreEditCursorFragment.class, dataId);
    }

    @Override
    protected void doOnCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {
        onKeyBackListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    pressBackHadle();
                    return true;
                }
                return false;
            }
        };
        valueViewLabelViewMap = CoreUtils.includeCoreEditView(rootView, getOriginalFrom().length, onKeyBackListener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.core_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.core_action_save:
                //TODO
                break;
            case android.R.id.home:
                pressBackHadle();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void pressBackHadle() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());//, R/.style.AppCompatAlertDialogStyle);
        builder.setMessage("Отменить изменения и завершить редактирование?")
                .setPositiveButton("Отменить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .setNegativeButton("Продолжить редактирование", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

}
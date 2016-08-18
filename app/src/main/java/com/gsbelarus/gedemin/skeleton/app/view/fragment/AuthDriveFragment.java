package com.gsbelarus.gedemin.skeleton.app.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.view.fragment.BaseFragment;
import com.gsbelarus.gedemin.skeleton.core.util.AuthDriveHelper;

public class AuthDriveFragment extends BaseFragment implements
        View.OnClickListener {

    private AuthDriveHelper authDriveHelper;

    @Override
    protected int getLayoutResource() {
        return R.layout.app_fragment_auth_drive;
    }

    @Override
    protected void onCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {
        super.onCreateView(rootView, savedInstanceState);

        rootView.findViewById(R.id.google_drive_open_file).setOnClickListener(this);
        rootView.findViewById(R.id.google_drive_create_file).setOnClickListener(this);

        authDriveHelper = new AuthDriveHelper(getActivity());

        authDriveHelper.createAPIClient();
    }

    @Override
    public void onResume() {
        super.onResume();

        authDriveHelper.connectAPIClient();
    }

    @Override
    public void onStop() {
        super.onStop();

        authDriveHelper.disconnectAPIClient();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_drive_open_file:
                authDriveHelper.onClickOpenFile();
                break;
            case R.id.google_drive_create_file:
                authDriveHelper.onClickCreateFile();
                break;
            default:
                break;
        }
    }
}

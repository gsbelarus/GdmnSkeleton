package com.gsbelarus.gedemin.skeleton.core.view.fragment.viewstate;

import android.os.Parcel;

import com.gsbelarus.gedemin.skeleton.core.view.fragment.CoreEditCursorFragment;


public class CoreEditFragmentState extends CoreDetailFragmentState {

    public boolean dataChanged;
    public boolean dataSaved;
    public boolean confirmDlgShowing;


    public CoreEditFragmentState(CoreEditCursorFragment fragment) {
        super(fragment);
        dataChanged = fragment.isDataChanged();
        dataSaved = fragment.isDataSaved();
        confirmDlgShowing = fragment.isConfirmDlgShowing();
    }

    public CoreEditFragmentState(Parcel in) {
        super(in);
        dataChanged = (in.readByte() != 0);
        dataSaved = (in.readByte() != 0);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (dataChanged ? 1 : 0));
        dest.writeByte((byte) (dataSaved ? 1 : 0));
    }

    public static Creator<CoreEditFragmentState> CREATOR =
            new Creator<CoreEditFragmentState>() {
                @Override
                public CoreEditFragmentState createFromParcel(Parcel source) {
                    return new CoreEditFragmentState(source);
                }

                @Override
                public CoreEditFragmentState[] newArray(int size) {
                    return new CoreEditFragmentState[size];
                }
            };
}

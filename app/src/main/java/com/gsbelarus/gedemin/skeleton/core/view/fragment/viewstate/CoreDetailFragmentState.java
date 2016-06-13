package com.gsbelarus.gedemin.skeleton.core.view.fragment.viewstate;

import android.os.Parcel;

import com.gsbelarus.gedemin.skeleton.base.view.fragment.BaseFragment;
import com.gsbelarus.gedemin.skeleton.core.view.fragment.CoreDetailCursorFragment;


public class CoreDetailFragmentState extends BaseFragment.BasicState {

    public final boolean canRemove;


    public CoreDetailFragmentState(CoreDetailCursorFragment fragment) {
        super(fragment);
        canRemove = fragment.isCanRemove();
    }

    public CoreDetailFragmentState(Parcel in) {
        super(in);
        canRemove = (in.readByte() != 0);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (canRemove ? 1 : 0));
    }

    public static Creator<CoreDetailFragmentState> CREATOR =
            new Creator<CoreDetailFragmentState>() {
                @Override
                public CoreDetailFragmentState createFromParcel(Parcel source) {
                    return new CoreDetailFragmentState(source);
                }

                @Override
                public CoreDetailFragmentState[] newArray(int size) {
                    return new CoreDetailFragmentState[size];
                }
            };
}
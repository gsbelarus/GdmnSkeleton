package com.gsbelarus.gedemin.skeleton.base.view.fragment.viewstate;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;

import com.gsbelarus.gedemin.skeleton.base.view.fragment.BaseFragment;
import com.gsbelarus.gedemin.skeleton.base.view.fragment.BaseRecyclerCursorFragment;


public class BasicRecyclerFragmentState extends BaseFragment.BasicState {

    public final Parcelable layoutManagerState; //TODO tmp


    public BasicRecyclerFragmentState(BaseRecyclerCursorFragment fragment) {
        super(fragment);
        layoutManagerState = fragment.getLayoutManager().onSaveInstanceState();
    }

    public BasicRecyclerFragmentState(Parcel in) {
        super(in);
        layoutManagerState = in.readParcelable(RecyclerView.LayoutManager.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(layoutManagerState, flags);
    }

    public static Creator<BasicRecyclerFragmentState> CREATOR =
            new Creator<BasicRecyclerFragmentState>() {
                @Override
                public BasicRecyclerFragmentState createFromParcel(Parcel source) {
                    return new BasicRecyclerFragmentState(source);
                }

                @Override
                public BasicRecyclerFragmentState[] newArray(int size) {
                    return new BasicRecyclerFragmentState[size];
                }
            };

}
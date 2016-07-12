package com.gsbelarus.gedemin.skeleton.core.view.fragment.viewstate;

import android.os.Parcel;

import com.gsbelarus.gedemin.skeleton.base.view.fragment.viewstate.BasicRecyclerFragmentState;
import com.gsbelarus.gedemin.skeleton.core.view.fragment.CoreSearchableRecyclerCursorFragment;


public class CoreRecyclerFragmentState extends BasicRecyclerFragmentState {

    public String savedSearchFilterQuery;

    public CoreRecyclerFragmentState(CoreSearchableRecyclerCursorFragment fragment) {
        super(fragment);
        savedSearchFilterQuery = fragment.getSearchQuery();
    }

    public CoreRecyclerFragmentState(Parcel in) {
        super(in);
        savedSearchFilterQuery = in.readString();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(savedSearchFilterQuery);
    }

    public static Creator<CoreRecyclerFragmentState> CREATOR =
            new Creator<CoreRecyclerFragmentState>() {
                @Override
                public CoreRecyclerFragmentState createFromParcel(Parcel source) {
                    return new CoreRecyclerFragmentState(source);
                }

                @Override
                public CoreRecyclerFragmentState[] newArray(int size) {
                    return new CoreRecyclerFragmentState[size];
                }
            };
}

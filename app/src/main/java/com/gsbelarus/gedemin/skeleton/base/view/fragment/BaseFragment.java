package com.gsbelarus.gedemin.skeleton.base.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gsbelarus.gedemin.skeleton.base.BaseApplication;


abstract public class BaseFragment<FRAGMENTSTATE_T extends BaseFragment.BasicState> extends Fragment {

    protected final String TAG = this.getClass().getCanonicalName();

    /**
     * Сonfiguration
     */
    @LayoutRes
    protected abstract int getLayoutResource();


    private static final String EXTRA_KEY_FRAGMENT_STATE = "fragmentState";

    private Context appContext;
    @Nullable
    private FRAGMENTSTATE_T savedFragmentState;


    public static <T extends Fragment> T newInstance(Class<T> cl, Bundle argsBundle) {
        T fragment = null;
        try {
            fragment = cl.newInstance();
            fragment.setArguments(argsBundle);

        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();

            // Tracking exception
            BaseApplication.getInstance().trackException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();

            // Tracking exception
            BaseApplication.getInstance().trackException(e);
        }


        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appContext = getActivity().getApplicationContext();

        // restore saved state
        if(savedInstanceState != null) {
            savedFragmentState = savedInstanceState.getParcelable(EXTRA_KEY_FRAGMENT_STATE);
        }

        // handle fragment arguments
        Bundle arguments = getArguments();
        if (arguments != null) handleFragmentArguments(arguments);

        // handle intent extras
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null) handleIntentExtras(extras);
    }

    protected void handleFragmentArguments(@NonNull Bundle arguments) {}
    protected void handleIntentExtras(@NonNull Bundle extras) {}

    @Nullable
    @Override
    final public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(getLayoutResource(), container, false);

        onCreateView(rootView, savedInstanceState);

        return rootView;
    }

    protected void onCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {};

//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//
//        if (savedInstanceState != null) {
//            FRAGMENTSTATE_T fragmentState = savedInstanceState.getParcelable(EXTRA_KEY_FRAGMENT_STATE);
//
//            if (fragmentState != null) handleSavedInstanceState(fragmentState);
//        }
//    }
//
//    protected void handleSavedInstanceState(@NonNull FRAGMENTSTATE_T savedInstanceState) {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_KEY_FRAGMENT_STATE, newInstanceState());
    }

    protected FRAGMENTSTATE_T newInstanceState() {
        return (FRAGMENTSTATE_T) new BasicState(this);
    }

    // accessors

    @Nullable
    protected FRAGMENTSTATE_T getSavedFragmentState() {
        return savedFragmentState;
    }

    protected Context getAppContext() {
        return appContext;
    }


    public static class BasicState implements Parcelable {

        protected BasicState(BaseFragment fragment) {} //TODO use viewModel

        public BasicState(Parcel in) {}

        @CallSuper
        @Override
        public void writeToParcel(Parcel dest, int flags) {}

        @Override
        public int describeContents() {
            return 0;
        }

        public static Creator<BasicState> CREATOR = new Creator<BasicState>() {
            @Override
            public BasicState createFromParcel(Parcel source) {
                return new BasicState(source);
            }

            @Override
            public BasicState[] newArray(int size) {
                return new BasicState[size];
            }
        };
    }

}

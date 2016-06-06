package com.gsbelarus.gedemin.skeleton.base.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


abstract public class BaseFragment extends Fragment {

    public final String TAG = this.getClass().getCanonicalName();

    /**
     * Сonfiguration
     */
    @LayoutRes
    protected abstract int getLayoutResource();

    protected Context appContext;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appContext = getActivity().getApplicationContext();

        // handle fragment arguments
        Bundle arguments = getArguments();
        if (arguments != null) handleFragmentArguments(arguments);

        // restore saved state
        if(savedInstanceState != null) handleSavedInstanceState(savedInstanceState);

        // handle intent extras
        Bundle extras = getActivity().getIntent().getExtras(); //TODO onAttach
        if(extras != null) handleIntentExtras(extras);
    }

    protected void handleFragmentArguments(@NonNull Bundle arguments) {};
    protected void handleSavedInstanceState(@NonNull Bundle savedInstanceState) {};
    protected void handleIntentExtras(@NonNull Bundle extras) {};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(getLayoutResource(), container, false);

        doOnCreateView(rootView, savedInstanceState);

        return rootView;
    }

    protected abstract void doOnCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState);


    public static <T extends Fragment> T newInstance(Class<T> cl, Bundle argsBundle) {
        T fragment = null;
        try {
            fragment = cl.newInstance();
            fragment.setArguments(argsBundle);

        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return fragment;
    }

}

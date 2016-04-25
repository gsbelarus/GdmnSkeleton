package com.gsbelarus.gedemin.skeleton.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutResource(), container, false);

        doOnCreateView(rootView, savedInstanceState);

        return rootView;
    }

    protected abstract void doOnCreateView(View rootView, @Nullable Bundle savedInstanceState);

    @SuppressWarnings("TryWithIdenticalCatches")
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

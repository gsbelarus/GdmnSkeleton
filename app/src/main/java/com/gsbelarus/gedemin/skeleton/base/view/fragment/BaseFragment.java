package com.gsbelarus.gedemin.skeleton.base.view.fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

abstract public class BaseFragment extends Fragment {

    public final String TAG = this.getClass().getCanonicalName();

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

    /**
     * Сonfiguration
     */

    @LayoutRes
    protected abstract int getLayoutResource();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(getLayoutResource(), container, false);

        onCreateView(rootView, savedInstanceState);

        return rootView;
    }

    protected void onCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {

    }
}

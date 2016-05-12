package com.gsbelarus.gedemin.skeleton.tmp;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gsbelarus.gedemin.skeleton.base.view.fragment.BaseFragment;


public abstract class BaseBindingFragment extends BaseFragment {

    protected ViewDataBinding fragmentDataBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentDataBinding = DataBindingUtil.inflate(inflater, getLayoutResource(), container, false);

        doOnCreateView(savedInstanceState);

        return fragmentDataBinding.getRoot();
    }

    protected abstract void doOnCreateView(@Nullable Bundle savedInstanceState);

    @Override
    protected void onCreateView(ViewGroup rootView, @Nullable Bundle savedInstanceState) {}
}

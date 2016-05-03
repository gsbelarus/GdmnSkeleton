package com.gsbelarus.gedemin.skeleton.tmp;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;


import com.gsbelarus.gedemin.skeleton.base.view.BaseActivity;

abstract public class BaseBindingActivity<T_ActivityDataBinding extends ViewDataBinding> extends BaseActivity {

    protected T_ActivityDataBinding activityDataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDataBinding();

        initViews();

        activityDataBinding.executePendingBindings();
    }

    protected void initDataBinding() {
        activityDataBinding = DataBindingUtil.setContentView(this, getLayoutResource());
    }

    protected abstract void initViews();

}

package com.gsbelarus.gedemin.skeleton.app.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.gsbelarus.gedemin.skeleton.core.view.fragment.CoreDetailCursorFragment;
import com.gsbelarus.gedemin.skeleton.core.view.fragment.CoreEditCursorFragment;


public class EditActivity extends DetailActivity {

    public static <T extends AppCompatActivity> Intent newStartIntent(Context context, long dataId) {
        Bundle extrasBundle = new Bundle();
        extrasBundle.putLong(CoreDetailCursorFragment.ARGUMENT_KEY_DATA_ID, dataId);

        return newStartIntent(context, EditActivity.class, extrasBundle);
    }

    @Override
    protected void initFragment(Bundle savedInstanceState) {
        initFragment(savedInstanceState, CoreEditCursorFragment.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) return false;

        return super.onOptionsItemSelected(item);
    }
}

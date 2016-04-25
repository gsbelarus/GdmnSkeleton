package com.gsbelarus.gedemin.skeleton.view.adapter;

import android.database.Cursor;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gsbelarus.gedemin.skeleton.base.BaseCursorRecyclerViewAdapter;


public class CoreCursorRecyclerViewAdapter extends BaseCursorRecyclerViewAdapter {

    protected LayoutInflater layoutInflater;


    public CoreCursorRecyclerViewAdapter(@Nullable Cursor dataCursor) {
        super(dataCursor);
    }

    @Override
    public BaseCursorItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        View itemView = layoutInflater.inflate(android.R.layout.two_line_list_item, parent, false);

        return new CoreCursorItemViewHolder(itemView);
    }


    public static class CoreCursorItemViewHolder extends BaseCursorItemViewHolder {

        private TextView tv1;
        private TextView tv2;

        public CoreCursorItemViewHolder(View itemView) {
            super(itemView);
            tv1 = (TextView) itemView.findViewById(android.R.id.text1);
            tv2 = (TextView) itemView.findViewById(android.R.id.text2);
        }

        @Override
        public void bind(Cursor cursor) {
            tv1.setText("_id: " + cursor.getString(cursor.getColumnIndex("_id")));
            tv2.setText("column6_INTEGER: " + cursor.getString(cursor.getColumnIndex("column6_INTEGER")));
        }
    }

}
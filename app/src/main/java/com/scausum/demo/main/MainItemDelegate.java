package com.scausum.demo.main;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.fubaisum.adapterdelegate.AbsAdapterDelegate;
import com.fubaisum.adapterdelegate.RecyclerViewHolder;
import com.fubaisum.ptrlayout.demo.R;

public class MainItemDelegate extends AbsAdapterDelegate<MainItem> {

    public MainItemDelegate(Activity activity) {
        super(activity, R.layout.item_main);
    }

    @Override
    protected boolean isForViewType(MainItem item) {
        return true;
    }

    @Override
    protected RecyclerViewHolder onCreateViewHolder(View itemView) {
        return new RecyclerViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(RecyclerViewHolder holder, MainItem item) {

        TextView tvLabel = holder.getView(R.id.tv_main_item_label);
        tvLabel.setText(item.labelRes);
    }
}
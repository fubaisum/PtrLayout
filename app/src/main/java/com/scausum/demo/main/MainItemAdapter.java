package com.scausum.demo.main;

import android.app.Activity;
import android.view.View;

import com.fubaisum.adapterdelegate.AbsAdapterDelegate;
import com.fubaisum.adapterdelegate.AbsDelegationAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sum on 8/9/16.
 */
class MainItemAdapter extends AbsDelegationAdapter<MainItem>
        implements AbsAdapterDelegate.OnItemClickListener {

    public MainItemAdapter(Activity activity, List<MainItem> items) {
        this.items = items != null ? items : new ArrayList<MainItem>();

        MainItemDelegate delegate = new MainItemDelegate(activity);
        this.addDelegate(delegate);
        delegate.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (position < 0 || position >= getItemCount()) {
            return;
        }
        MainItem item = items.get(position);
        if (item.onClickListener != null) {
            item.onClickListener.onClick(null);
        }
    }

}


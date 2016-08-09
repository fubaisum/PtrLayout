package com.fubaisum.demo.main;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.fubaisum.adapterdelegate.AbsAdapterDelegate;
import com.fubaisum.adapterdelegate.AbsDelegationAdapter;
import com.fubaisum.adapterdelegate.RecyclerViewHolder;
import com.fubaisum.ptrlayout.demo.R;

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
        item.onClickListener.onClick(null);
    }

    private static class MainItemDelegate extends AbsAdapterDelegate<MainItem> {

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


}


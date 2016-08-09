package com.fubaisum.demo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fubaisum.demo.base.PtrLayoutFragment;
import com.fubaisum.ptrlayout.demo.R;

/**
 * Created by sum on 8/9/16.
 */
public class WithListView extends PtrLayoutFragment {

    @Override
    protected void onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup contentContainer, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_with_listview, contentContainer);
    }

    @Override
    public void onRefresh() {
        ptrLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLayout.finishRefresh();
            }
        }, 1500);
    }

    @Override
    public void onLoading() {
        ptrLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLayout.finishLoading();
            }
        }, 1500);
    }
}

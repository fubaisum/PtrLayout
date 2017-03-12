package com.scausum.demo.main;

import android.view.View;

/**
 * Created by sum on 8/9/16.
 */
class MainItem {

    public int labelRes;
    public View.OnClickListener onClickListener;

    public MainItem(int labelRes, View.OnClickListener onClickListener) {
        this.labelRes = labelRes;
        this.onClickListener = onClickListener;
    }
}

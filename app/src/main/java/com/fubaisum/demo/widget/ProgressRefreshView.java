package com.fubaisum.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.fubaisum.ptrlayout.RefreshView;

/**
 * Created by sum on 8/6/16.
 */
public class ProgressRefreshView extends View implements RefreshView{

    public ProgressRefreshView(Context context) {
        super(context);
    }

    public ProgressRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onPullingDown(int offset) {

    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onRefreshFinished() {

    }
}

package com.fubaisum.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.fubaisum.ptrlayout.LoadingView;

/**
 * Created by sum on 8/6/16.
 */
public class ProgressLoadingView extends View implements LoadingView {

    public ProgressLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressLoadingView(Context context) {
        super(context);
    }

    public ProgressLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onPullingUp(int offset) {

    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onLoadingFinished() {

    }
}

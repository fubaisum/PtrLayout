package com.fubaisum.ptrlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/**
 * Created by sum on 8/6/16.
 */
public class PtrLayout extends FrameLayout {

    private int targetViewId;
    private int refreshViewId;
    private int loadingViewId;
    private View targetView;
    private RefreshView refreshView;
    private View loadingView;

    private boolean isRefreshing;
    private boolean isLoading;

    private int mTouchSlop;

    public PtrLayout(Context context) {
        super(context);
        initialize(context, null);
    }

    public PtrLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public PtrLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    public void initialize(Context context, AttributeSet attrs) {

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PtrLayout);
        targetViewId = a.getResourceId(R.styleable.PtrLayout_target_view, 0);
        refreshViewId = a.getResourceId(R.styleable.PtrLayout_refresh_view, 0);
        loadingViewId = a.getResourceId(R.styleable.PtrLayout_loading_view, 0);
        a.recycle();

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.e("ptr", "onFinishInflate");
        // Find target view
        if (targetViewId == 0) {
            throw new NullPointerException("The target view can not be null.");
        } else {
            targetView = findViewById(targetViewId);
        }
        // Find refresh view
        if (refreshViewId != 0) {
            View view = findViewById(refreshViewId);
            if (view instanceof RefreshView) {
                refreshView = (RefreshView) view;
            }
        }
        // Find loading view
        if (loadingViewId != 0) {
            View view = findViewById(loadingViewId);
            if (view instanceof LoadingView) {
                loadingView = view;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.e("ptr", "onLayout");
        super.onLayout(changed, left, top, right, bottom);
    }

    public void onRefreshAnimationFinished() {

    }

    public void onLoadingAnimationFinished() {

    }
}

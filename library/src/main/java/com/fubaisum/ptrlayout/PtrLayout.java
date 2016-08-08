package com.fubaisum.ptrlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/**
 * Created by sum on 8/6/16.
 */
public class PtrLayout extends FrameLayout {

    private static final String LOG_TAG = PtrLayout.class.getSimpleName();

    private int targetViewId;
    private int refreshViewId;
    private int loadingViewId;
    private View targetView;
    private RefreshView refreshView;
    private LoadingView loadingView;

    private boolean isRefreshing;
    private boolean isLoading;

    private int mTouchSlop;
    private static final int INVALID_POINTER = -1;
    private int mActivePointerId;
    private float mInitialDownY;

    private OnRefreshListener onRefreshListener;
    private OnLoadingListener onLoadingListener;

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

    private void initialize(Context context, AttributeSet attrs) {

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
                loadingView = (LoadingView) view;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isEnabled() || isRefreshing || isLoading) {
            return true;
        }

        final int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                final float initialDownY = getMotionEventY(event, mActivePointerId);
                if (initialDownY == -1) {
                    return false;
                }
                mInitialDownY = initialDownY;
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }
                final float crrTouchY = getMotionEventY(event, mActivePointerId);
                if (crrTouchY == -1) {
                    return false;
                }
                final float yDiff = crrTouchY - mInitialDownY;
                // Is pulling up for refreshing?
                if (yDiff > mTouchSlop && !canTargetViewScrollUp()) {
                    if (null != refreshView) {
                        isRefreshing = true;
                        return true;
                    }
                }
                // Is pulling down for loading?
                if (yDiff < -mTouchSlop && !canTargetViewScrollDown()) {
                    if (null != loadingView) {
                        isLoading = true;
                        return true;
                    }
                }
            }
            break;
            case MotionEventCompat.ACTION_POINTER_UP: {
                onSecondaryPointerUp(event);
            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER;
            }
            break;
        }

        return super.onInterceptTouchEvent(event);
    }

    /**
     * @return Whether it is possible for the target view of this layout to
     * scroll up. Override this if the target view is a custom view.
     */
    private boolean canTargetViewScrollUp() {
        return ViewCompat.canScrollVertically(targetView, -1);
    }

    /**
     * @return Whether it is possible for the target view of this layout to
     * scroll down. Override this if the target view is a custom view.
     */
    private boolean canTargetViewScrollDown() {
        return ViewCompat.canScrollVertically(targetView, 1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        int pointerIndex = -1;
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }
                final float crrTouchY = MotionEventCompat.getY(event, pointerIndex);
                final float yDiff = crrTouchY - mInitialDownY;
                // If is refreshing
                if (yDiff > mTouchSlop && isRefreshing) {
                    refreshView.onPullingDown(yDiff);
                }
                // If is loading
                if (yDiff < -mTouchSlop && isLoading) {
                    loadingView.onPullingUp(yDiff);
                }
                return true;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                pointerIndex = MotionEventCompat.getActionIndex(event);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = MotionEventCompat.getPointerId(event, pointerIndex);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_UP: {
                onSecondaryPointerUp(event);
                break;
            }
            case MotionEvent.ACTION_UP: {
                pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }
                mActivePointerId = INVALID_POINTER;
                if (isRefreshing) {
                    refreshView.onRelease();
                }
                if (isLoading) {
                    loadingView.onRelease();
                }
                return true;
            }
            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER;
                if (isRefreshing) {
                    refreshView.onRelease();
                }
                if (isLoading) {
                    loadingView.onRelease();
                }
                return true;
            }
        }

        return super.onTouchEvent(event);
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    public void notifyRefreshAnimationStart() {
        if (onRefreshListener != null) {
            onRefreshListener.onRefresh();
        }
    }

    public void notifyRefreshAnimationStop() {
        isRefreshing = false;
    }

    public void notifyLoadingAnimationStart() {
        if (onLoadingListener != null) {
            onLoadingListener.onLoading();
        }
    }

    public void notifyLoadingAnimationStop() {
        isLoading = false;
    }

    public View getTargetView() {
        return targetView;
    }

    public void finishRefresh() {
        if (refreshView != null) {
            refreshView.onRefreshFinished();
        }
    }

    public void finishLoading() {
        if (loadingView != null) {
            loadingView.onLoadingFinished();
        }
    }

    /**
     * 回调接口：刷新
     */
    public interface OnRefreshListener {
        void onRefresh();
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    /**
     * 回调接口：加载
     */
    public interface OnLoadingListener {
        void onLoading();
    }

    public void setOnLoadingListener(OnLoadingListener onLoadingListener) {
        this.onLoadingListener = onLoadingListener;
    }
}

package com.scausum.ptrlayout;

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

import com.fubaisum.ptrlayout.R;

/**
 * Created by sum on 8/6/16.
 */
public class PtrLayout extends FrameLayout {

    private static final String LOG_TAG = PtrLayout.class.getSimpleName();

    private int contentViewId;
    private int anchorViewId;
    private int refreshViewId;
    private int loadingViewId;
    private View contentView;// The view maybe scroll follow RefreshView or LoadingView.
    private View anchorView;// The view decide that the PtrLayout can start refreshing or loading.
    private RefreshView refreshView;
    private LoadingView loadingView;

    private boolean canNonScrollableChildRefresh;
    private boolean canNonScrollableChildLoading;
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
        contentViewId = a.getResourceId(R.styleable.PtrLayout_ptr_content_view, 0);
        anchorViewId = a.getResourceId(R.styleable.PtrLayout_ptr_anchor_view, 0);
        refreshViewId = a.getResourceId(R.styleable.PtrLayout_ptr_refresh_view, 0);
        loadingViewId = a.getResourceId(R.styleable.PtrLayout_ptr_loading_view, 0);
        a.recycle();

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // Find content view
        if (contentViewId != 0) {
            contentView = findViewById(contentViewId);
        }
        // Find anchor view
        if (anchorViewId != 0) {
            anchorView = findViewById(anchorViewId);
        }
        // Find refresh view
        if (refreshViewId != 0) {
            View view = findViewById(refreshViewId);
            if (view instanceof RefreshView) {
                refreshView = (RefreshView) view;
            } else {
                throw new IllegalArgumentException(
                        "The refresh view must implement RefreshView interface.");
            }
        }
        // Find loading view
        if (loadingViewId != 0) {
            View view = findViewById(loadingViewId);
            if (view instanceof LoadingView) {
                loadingView = (LoadingView) view;
            } else {
                throw new IllegalArgumentException(
                        "The loading view must implement LoadingView interface.");
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        final int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (isRefreshing || isLoading) {
                    return true;
                }

                mActivePointerId = event.getPointerId(0);
                final float initialDownY = getMotionEventY(event, mActivePointerId);
                if (initialDownY == -1) {
                    return false;
                }
                mInitialDownY = initialDownY;
                super.dispatchTouchEvent(event);
                return true;
            }
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
                // refreshing
                if (yDiff > mTouchSlop && !isLoading && refreshView != null) {
                    if (isRefreshing) {
                        refreshView.onPullingDown(yDiff);
                        return true;
                    } else {
                        if ((anchorView != null && !canAnchorViewScrollUp()) || canNonScrollableChildRefresh) {
                            isRefreshing = true;
                            return true;
                        }
                    }
                }
                // loading
                if (yDiff < -mTouchSlop && !isRefreshing && loadingView != null) {
                    if (isLoading) {
                        loadingView.onPullingUp(yDiff);
                        return true;
                    } else {
                        if ((anchorView != null && !canAnchorViewScrollDown()) || canNonScrollableChildLoading) {
                            isLoading = true;
                            return true;
                        }
                    }
                }

                // Can't move child when is refreshing or loading
                if (isRefreshing || isLoading) {
                    return true;
                }

                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                int pointerIndex = MotionEventCompat.getActionIndex(event);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }

                mActivePointerId = event.getPointerId(pointerIndex);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_UP: {
                onSecondaryPointerUp(event);
                break;
            }
            case MotionEvent.ACTION_UP: {
                int pointerIndex = event.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }
                mActivePointerId = INVALID_POINTER;
                if (isRefreshing) {
                    refreshView.onRelease();
                    // send cancel event to children
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    super.dispatchTouchEvent(event);
                    return true;
                }
                if (isLoading) {
                    loadingView.onRelease();
                    // send cancel event to children
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    super.dispatchTouchEvent(event);
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER;
                if (isRefreshing) {
                    refreshView.onRelease();
                }
                if (isLoading) {
                    loadingView.onRelease();
                }
                break;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * @return Whether it is possible for the target view of this layout to
     * scroll up. Override this if the target view is a custom view.
     */
    private boolean canAnchorViewScrollUp() {
        return ViewCompat.canScrollVertically(anchorView, -1);
    }

    /**
     * @return Whether it is possible for the target view of this layout to
     * scroll down. Override this if the target view is a custom view.
     */
    private boolean canAnchorViewScrollDown() {
        return ViewCompat.canScrollVertically(anchorView, 1);
    }

    private float getMotionEventY(MotionEvent event, int activePointerId) {
        final int index = event.findPointerIndex(activePointerId);
        return index < 0 ? -1 : event.getY(index);
    }

    private void onSecondaryPointerUp(MotionEvent event) {
        final int pointerIndex = MotionEventCompat.getActionIndex(event);
        final int pointerId = event.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = event.getPointerId(newPointerIndex);
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

    public View getContentView() {
        return contentView;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public View getAnchorView() {
        return anchorView;
    }

    public void setAnchorView(View anchorView) {
        this.anchorView = anchorView;
    }

    public void autoRefresh() {
        if (refreshView != null) {
            isRefreshing = true;
            refreshView.autoRefresh();
        }
    }

    public void autoLoading() {
        if (loadingView != null) {
            isLoading = true;
            loadingView.autoLoading();
        }
    }

    public void setCanNonScrollableChildRefresh(boolean childRefresh) {
        this.canNonScrollableChildRefresh = childRefresh;
    }

    public void setCanNonScrollableChildLoading(boolean canLoading) {
        this.canNonScrollableChildLoading = canLoading;
    }

    public RefreshView getRefreshView() {
        return refreshView;
    }

    public void setRefreshView(RefreshView refreshView) {
        this.refreshView = refreshView;
    }

    public LoadingView getLoadingView() {
        return loadingView;
    }

    public void setLoadingView(LoadingView loadingView) {
        this.loadingView = loadingView;
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
     * Callback for refreshing
     */
    public interface OnRefreshListener {
        void onRefresh();
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        if (refreshView == null) {
            throw new NullPointerException("There is no refresh view.");
        }
        this.onRefreshListener = onRefreshListener;
    }

    /**
     * Callback for loading
     */
    public interface OnLoadingListener {
        void onLoading();
    }

    public void setOnLoadingListener(OnLoadingListener onLoadingListener) {
        if (loadingView == null) {
            throw new NullPointerException("There is no loading view.");
        }
        this.onLoadingListener = onLoadingListener;
    }
}

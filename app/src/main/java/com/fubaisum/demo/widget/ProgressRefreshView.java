package com.fubaisum.demo.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.fubaisum.ptrlayout.PtrLayout;
import com.fubaisum.ptrlayout.RefreshView;

/**
 * Created by sum on 8/6/16.
 */
public class ProgressRefreshView extends View implements RefreshView {

    private int height;
    private static final int EXIT_ANIM_DURATION = 600;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

    private PtrLayout ptrLayout;
    private View targetView;

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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (height == 0) {
            height = MeasureSpec.getSize(heightMeasureSpec);
            ptrLayout = (PtrLayout) getParent();
            targetView = ptrLayout.getTargetView();
        }
    }

    @Override
    public void onPullingDown(float offset) {
        // calculate the target view's translationY.
        offset = Math.min(height, offset);
        offset = Math.max(0, offset);
        int translationY = (int) (decelerateInterpolator.getInterpolation(offset / height) * offset);
        // move down target view.
        targetView.setTranslationY(translationY);
        // move down myself
        setTranslationY(translationY);
    }

    @Override
    public void onRelease() {
        if ((int) getTranslationY() < height) {
            startExitAnimation();
        } else {
            startRefreshingAnimation();
        }
    }

    private void startRefreshingAnimation() {
        ptrLayout.notifyRefreshAnimationStart();
    }

    private void startExitAnimation() {
        float crrTranslationY = getTranslationY();
        ValueAnimator exitAnimator = ValueAnimator.ofFloat(crrTranslationY, 0);
        exitAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // calculate the target view's translationY.
                float targetTranslationY = (float) animation.getAnimatedValue();
                float ratio = decelerateInterpolator.getInterpolation(targetTranslationY / height);
                targetTranslationY = ratio * targetTranslationY;
                // move up target view.
                targetView.setTranslationY(targetTranslationY);
                // move up myself.
                setTranslationY(targetTranslationY);

                if (targetTranslationY <= 0) {
                    // notify ptrLayout the refresh animation finished.
                    ptrLayout.notifyRefreshAnimationStop();
                }
            }
        });
        exitAnimator.setDuration(EXIT_ANIM_DURATION * (int) (crrTranslationY) / height);
        exitAnimator.start();
    }

    @Override
    public void onRefreshFinished() {
        startExitAnimation();
    }
}

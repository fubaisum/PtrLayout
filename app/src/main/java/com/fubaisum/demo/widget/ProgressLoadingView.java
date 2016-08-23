package com.fubaisum.demo.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import com.fubaisum.ptrlayout.LoadingView;
import com.fubaisum.ptrlayout.PtrLayout;

/**
 * Created by sum on 8/6/16.
 */
public class ProgressLoadingView extends ProgressBar implements LoadingView {

    private int height;
    private static final int EXIT_ANIM_DURATION = 600;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

    private PtrLayout ptrLayout;
    private View targetView;

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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (height == 0) {
            height = MeasureSpec.getSize(heightMeasureSpec);
            ptrLayout = (PtrLayout) getParent();
            targetView = ptrLayout.getContentView();
        }
    }

    @Override
    public void onPullingUp(float offset) {
        // calculate the target view's translationY.
        offset = -offset;
        offset = Math.min(height, offset);
        offset = Math.max(0, offset);
        int translationY = -(int) (decelerateInterpolator.getInterpolation(offset / height) * offset);
        // move up target view.
        targetView.setTranslationY(translationY);
        // move up myself.
        setTranslationY(translationY);
    }

    @Override
    public void onRelease() {
        if ((int) getTranslationY() > -height) {
            startExitAnimation();
        } else {
            startLoadingAnimation();
        }
    }

    private void startLoadingAnimation() {
        ptrLayout.notifyLoadingAnimationStart();
    }

    private void startExitAnimation() {
        float crrTranslationY = getTranslationY();
        ValueAnimator exitAnimator = ValueAnimator.ofFloat(crrTranslationY, 0);
        exitAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float targetTranslationY = (float) animation.getAnimatedValue();
                float ratio = decelerateInterpolator.getInterpolation(-targetTranslationY / height);
                targetTranslationY = ratio * targetTranslationY;
                // move down contentView.
                targetView.setTranslationY(targetTranslationY);
                // move down myself.
                setTranslationY(targetTranslationY);

                if (targetTranslationY >= 0) {
                    // notify ptrLayout the loading animation finished.
                    ptrLayout.notifyLoadingAnimationStop();
                }
            }
        });
        exitAnimator.setDuration(EXIT_ANIM_DURATION * (int) (-crrTranslationY) / height);
        exitAnimator.start();
    }

    @Override
    public void onLoadingFinished() {
        startExitAnimation();
    }

    @Override
    public void autoLoading() {
        ValueAnimator startAnimator = ValueAnimator.ofFloat(0, -height);
        startAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            private boolean isRelease;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offset = (float) animation.getAnimatedValue();
                // simulate pulling up
                onPullingUp(offset);
                Log.e("TAG", "offset = " + offset);
                if (offset <= -height && !isRelease) {
                    isRelease = true;
                    onRelease();
                }
            }
        });
        startAnimator.setDuration(EXIT_ANIM_DURATION);
        startAnimator.start();
    }
}

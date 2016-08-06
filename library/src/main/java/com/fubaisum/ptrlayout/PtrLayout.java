package com.fubaisum.ptrlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by sum on 8/6/16.
 */
public class PtrLayout extends FrameLayout {

    private View targetView;// the target of the gesture

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

    public void initialize(Context context, AttributeSet attributeSet) {

    }
}

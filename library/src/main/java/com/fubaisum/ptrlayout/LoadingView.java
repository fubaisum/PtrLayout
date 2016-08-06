package com.fubaisum.ptrlayout;

/**
 * Created by sum on 8/6/16.
 */
public interface LoadingView {

    void onPullingUp(int offset);

    void onRelease();

    void onLoadingFinished();
}

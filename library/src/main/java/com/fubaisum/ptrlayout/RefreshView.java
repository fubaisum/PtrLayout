package com.fubaisum.ptrlayout;

/**
 * Created by sum on 8/6/16.
 */
public interface RefreshView {

    void onPullingDown(int offset);

    void onRelease();

    void onRefreshFinished();
}

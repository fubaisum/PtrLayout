package com.scausum.ptrlayout;

/**
 * Created by sum on 8/6/16.
 */
public interface RefreshView {

    void onPullingDown(float offset);

    void onRelease();

    void onRefreshFinished();

    void autoRefresh();
}

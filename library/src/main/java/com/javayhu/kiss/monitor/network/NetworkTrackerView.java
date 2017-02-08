package com.javayhu.kiss.monitor.network;

import android.content.Context;
import android.graphics.Canvas;

import com.javayhu.kiss.monitor.base.BaseTracker;
import com.javayhu.kiss.monitor.base.BaseTrackerView;


/**
 * 网络监控数据view
 * <p>
 * Created by javayhu on 1/22/17.
 */
public class NetworkTrackerView extends BaseTrackerView {

    private NetworkTracker mNetworkTracker;

    public NetworkTrackerView(Context context, BaseTracker tracker) {
        super(context, tracker);
        mNetworkTracker = (NetworkTracker) tracker;
    }

    @Override
    public void updateDisplay(Canvas canvas, int x, int y) {
        String text = String.format("NETWORK: Rx=%.2f Tx=%.2f(KB/s)", mNetworkTracker.mRxRate, mNetworkTracker.mTxRate);
        drawText(canvas, text, x, y);
    }

    @Override
    public int viewHeight() {
        return mFontHeight;
    }
}

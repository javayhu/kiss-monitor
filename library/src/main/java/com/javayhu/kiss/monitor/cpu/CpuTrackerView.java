package com.javayhu.kiss.monitor.cpu;

import android.content.Context;
import android.graphics.Canvas;

import com.javayhu.kiss.monitor.base.BaseTracker;
import com.javayhu.kiss.monitor.base.BaseTrackerView;


/**
 * 显示cpu占用
 * <p>
 * Created by javayhu on 1/20/17.
 */
public class CpuTrackerView extends BaseTrackerView {

    private CpuTracker mCpuTracker;

    public CpuTrackerView(Context context, BaseTracker tracker) {
        super(context, tracker);
        mCpuTracker = (CpuTracker) tracker;
    }

    @Override
    public void updateDisplay(Canvas canvas, int x, int y) {
        String text = String.format("CPU: user %.2f%% / kernel %.2f%%", mCpuTracker.mUserPercentUsage, mCpuTracker.mKernelPercentUsage);
        drawText(canvas, text, x, y);
    }

    @Override
    public int viewHeight() {
        return mFontHeight;
    }
}

package com.javayhu.kiss.monitor.battery;

import android.content.Context;
import android.graphics.Canvas;

import com.javayhu.kiss.monitor.base.BaseTracker;
import com.javayhu.kiss.monitor.base.BaseTrackerView;


/**
 * 耗电量监控视图
 * <p>
 * Created by javayhu on 1/20/17.
 */
public class BatteryTrackerView extends BaseTrackerView {

    private BatteryTracker mBatteryTracker;

    public BatteryTrackerView(Context context, BaseTracker tracker) {
        super(context, tracker);

        this.mBatteryTracker = (BatteryTracker) tracker;
    }

    @Override
    public void updateDisplay(Canvas canvas, int x, int y) {
        String text = String.format("BATTERY: %.2f%%", mBatteryTracker.mBatteryPercent);
        drawText(canvas, text, x, y);
    }

    @Override
    public int viewHeight() {
        return mFontHeight;
    }
}

package com.javayhu.kiss.monitor.activity;

import android.content.Context;
import android.graphics.Canvas;

import com.javayhu.kiss.monitor.base.BaseTracker;
import com.javayhu.kiss.monitor.base.BaseTrackerView;


/**
 * 显示栈顶Activity
 * <p>
 * Created by javayhu on 2/13/17.
 */
public class ActivityTrackerView extends BaseTrackerView {

    private ActivityTracker mActivityTracker;

    public ActivityTrackerView(Context context, BaseTracker tracker) {
        super(context, tracker);

        this.mActivityTracker = (ActivityTracker) tracker;
    }

    @Override
    public void updateDisplay(Canvas canvas, int x, int y) {
        String text = String.format("PACKAGE: %s", mActivityTracker.mTopActivity.getPackageName());
        drawText(canvas, text, x, y);

        y += mFontHeight;
        text = String.format("ACTIVITY: %s", mActivityTracker.mTopActivity.getComponentName().getShortClassName());
        drawText(canvas, text, x, y);
    }

    @Override
    public int viewHeight() {
        return mFontHeight * 2;
    }
}

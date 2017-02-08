package com.javayhu.kiss.monitor.memory;

import android.content.Context;
import android.graphics.Canvas;

import com.javayhu.kiss.monitor.base.BaseTracker;
import com.javayhu.kiss.monitor.base.BaseTrackerView;


/**
 * 内存监控视图
 * <p>
 * Created by javayhu on 1/20/17.
 */
public class MemTrackerView extends BaseTrackerView {

    private MemTracker mMemTracker;

    public MemTrackerView(Context context, BaseTracker tracker) {
        super(context, tracker);
        this.mMemTracker = (MemTracker) tracker;
    }

    @Override
    public void updateDisplay(Canvas canvas, int x, int y) {
        String text = String.format("VMHEAP: %.2f(MB)=%.2f(used)+%.2f(free)", mMemTracker.mMemTotal, mMemTracker.mMemUsed, mMemTracker.mMemFree);
        drawText(canvas, text, x, y);

        y += mFontHeight;
        text = String.format("MEMORY: PSS=%.2f(MB) USS=%.2f(MB)", mMemTracker.mMemInfoPssTotal, mMemTracker.mMemInfoUssTotal);
        drawText(canvas, text, x, y);
    }

    @Override
    public int viewHeight() {
        return mFontHeight * 2;
    }
}

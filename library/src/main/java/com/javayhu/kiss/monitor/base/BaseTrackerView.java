package com.javayhu.kiss.monitor.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.javayhu.kiss.monitor.MonitorController;


/**
 * 监控视图的抽象类
 * <p>
 * Created by javayhu on 1/20/17.
 */
public abstract class BaseTrackerView extends View {

    /**
     * 与绘制数据有关的变量
     */
    protected int mTextSize;
    protected int mFontHeight;
    protected float mAscent;
    protected float mDisplayDensity;

    protected Paint mDefaultPaint;

    /**
     * 设置时间间隔发送消息来更新数据和显示
     */
    protected BaseTracker mTracker;
    protected long mMessageDelay = MonitorController.DEFAULT_INTERVAL * 1000;

    private UpdateHandler mHandler;
    private HandlerThread mHandlerThread;

    /**
     * 暂停更新数据
     */
    public void stopMonitor() {
        mHandler.removeMessages(1);
    }

    /**
     * 开始更新数据
     */
    public void startMonitor() {
        mHandler.removeMessages(1);
        mHandler.sendEmptyMessage(1);
    }

    public BaseTrackerView(Context context, BaseTracker tracker) {
        super(context);
        initTracker(tracker);
    }

    /**
     * 抽象方法：更新显示,左上角是(x,y)
     */
    public abstract void updateDisplay(Canvas canvas, int x, int y);

    /**
     * 抽象方法：组件高度
     */
    public abstract int viewHeight();

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHandler.sendEmptyMessage(1);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(1);
        mHandler = null;
        mHandlerThread.quit();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getResources().getDisplayMetrics().widthPixels, viewHeight());//宽度都是屏幕宽，高度由组件自定义
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int x = getPaddingLeft();
        int y = getPaddingTop() - (int) mAscent;
        updateDisplay(canvas, x, y);//调用子类具体的绘制方法
    }

    /**
     * 初始化Tracker
     */
    protected void initTracker(BaseTracker tracker) {
        mTracker = tracker;
        mHandlerThread = new HandlerThread("monitor-" + this.getClass().getSimpleName());//不同tracker对应的线程名称不同
        mHandlerThread.start();
        mHandler = new UpdateHandler(mHandlerThread.getLooper());

        mDisplayDensity = getContext().getResources().getDisplayMetrics().density;
        mTextSize = 10;
        if (mDisplayDensity < 1) {
            mTextSize = 9;
        } else {
            mTextSize = (int) (10 * mDisplayDensity);
            if (mTextSize < 10) {
                mTextSize = 10;
            }
        }
        //mTextSize = (int) (mTextSize * 1.1);
        mTextSize = (int) (mTextSize * 1.5);//显示内容不多，增大字体
        mDefaultPaint = getPaint();

        mAscent = mDefaultPaint.ascent();
        float descent = mDefaultPaint.descent();
        mFontHeight = (int) (descent - mAscent + .5f);
    }

    /**
     * 得到默认的Paint
     */
    protected Paint getPaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));//这里解决了用户自定义系统字体导致非等宽字体出现的不对齐问题
        paint.setAntiAlias(true);
        paint.setTextSize(mTextSize);
        //paint.setARGB(255, 255, 255, 255);//默认的白色
        paint.setARGB(255, 255, 0, 0);
        paint.setShadowLayer(4, 0, 0, 0xff000000);
        paint.setShadowLayer(2, 0, 0, 0xff000000);
        return paint;
    }

    /**
     * 使用默认的Paint写字符串
     */
    protected void drawText(Canvas canvas, String text, float x, float y) {
        drawTextWithPaint(canvas, text, x, y, mDefaultPaint);
    }

    /**
     * 使用指定的paint写字符串 （将这种连续绘制重复内容5次的绘制方式统一起来）
     * hujiawei 取消下面的多次绘制形式（为了增加阴影效果看起来更清楚），因为绘制太多比较耗时，相当影响性能
     */
    protected void drawTextWithPaint(Canvas canvas, String text, float x, float y, Paint paint) {
        canvas.drawText(text, x, y, paint);//
    }

    /**
     * 修改默认的paint颜色
     */
    public void setDefaultColor(int a, int r, int g, int b) {
        mDefaultPaint.setARGB(a, r, g, b);
    }

    /**
     * 修改默认的刷新时间间隔
     */
    public void setRefreshInterval(int interval) {
        mMessageDelay = interval;
    }

    /**
     * 处理刷新的Handler
     */
    private final class UpdateHandler extends Handler {

        public UpdateHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (mTracker.updateContent()) {//数据获取成功的话才会去更新显示
                    postInvalidate();
                    if (mHandler != null) {
                        sendMessageDelayed(obtainMessage(1), mMessageDelay);
                    }
                } else {//如果数据获取失败的话立即重新获取数据
                    if (mHandler != null) {
                        sendMessageDelayed(obtainMessage(1), 1000);
                        //sendEmptyMessage(1);
                    }
                }
            }
        }
    }

}

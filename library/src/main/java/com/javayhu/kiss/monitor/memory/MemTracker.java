package com.javayhu.kiss.monitor.memory;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.os.Process;

import com.javayhu.kiss.monitor.base.BaseTracker;
import com.javayhu.kiss.monitor.util.LocalMemoryInfo;


/**
 * 内存监控
 * <p>
 * Runtime.maxMemory是虚拟机堆最大能够扩展到的大小
 * totalMemory是堆当前大小下已经使用的内存大小
 * freeMemory是堆当前可用的大小 (当内存不足的时候堆有可能扩展其大小)
 * <p>
 * adb shell dumpsys meminfo [packageName]
 * ActivityManager -> getProcessMemoryInfo -> Debug.MemoryInfo -> PSS + USS
 * <p>
 * Created by javayhu on 1/20/17.
 */
public class MemTracker extends BaseTracker {

    public float mMemMax = 0.0f;//单位 MB
    public float mMemUsed = 0.0f;
    public float mMemFree = 0.0f;
    public float mMemTotal = 0.0f;
    public float mMemPercent = 0.0f;

    public float mMemInfoPssTotal = 0.0f;
    public float mMemInfoUssTotal = 0.0f;

    private ActivityManager mActivityManager;

    public MemTracker(Context context) {
        super(context);
        mMemMax = Runtime.getRuntime().maxMemory() / (1024.f * 1024.f);
        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    public boolean updateContent() {
        Runtime runtime = Runtime.getRuntime();
        mMemTotal = runtime.totalMemory() / ((1024.f * 1024.f));
        mMemFree = runtime.freeMemory() / ((1024.f * 1024.f));
        mMemUsed = mMemTotal - mMemFree;
        mMemPercent = mMemUsed / mMemMax * 100;

        Debug.MemoryInfo[] mMemInfo = mActivityManager.getProcessMemoryInfo(new int[]{Process.myPid()});
        mMemInfoPssTotal = mMemInfo[0].getTotalPss() / 1024.f;
        mMemInfoUssTotal = LocalMemoryInfo.getTotalUss(mMemInfo[0]) / 1024.f;
        return true;
    }

}

package com.javayhu.kiss.monitor.battery;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.javayhu.kiss.monitor.base.BaseTracker;

/**
 * 耗电量监控
 * <p>
 * 目前只是显示了当前手机的剩余电量，统计耗电量对权限要求较高
 * 可以考虑和PowerStat应用结合，通过广播的方式来开启和关闭监控
 * <p>
 * Created by javayhu on 1/20/17.
 */
public class BatteryTracker extends BaseTracker {

    public float mBatteryPercent;
    private IntentFilter mIntentFilter;

    public BatteryTracker(Context context) {
        super(context);
        mIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    }

    @Override
    public boolean updateContent() {
        Intent batteryStatus = mContext.registerReceiver(null, mIntentFilter);//sticky intent
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);//battery level
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);//battery scale
        mBatteryPercent = (float) ((level * 1.0 / scale) * 100);
        return true;
    }

}

package com.javayhu.kiss.monitor;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.javayhu.kiss.monitor.base.BaseTrackerView;
import com.javayhu.kiss.monitor.cpu.CpuTracker;
import com.javayhu.kiss.monitor.cpu.CpuTrackerView;
import com.javayhu.kiss.monitor.memory.MemTracker;
import com.javayhu.kiss.monitor.memory.MemTrackerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 监控服务
 * <p>
 * Created by javayhu on 1/20/17.
 */
public class MonitorService extends Service {

    private static final String TAG = "Wukong Monitor Service";
    private static final int NOTIFICATION_ID = 214;

    private LinearLayout mMonitorLayout;
    private List<BaseTrackerView> mTrackerViews;

    private CpuTrackerView mCpuTrackerView;
    private MemTrackerView mMemTrackerView;
    //private NetworkTrackerView mNetworkTrackerView;
    //private BatteryTrackerView mBatteryTrackerView;

    /**
     * 亮屏息屏回调
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                stopMonitor();
            } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                startMonitor();
            }
        }
    };

    /**
     * 暂停更新数据
     */
    private void stopMonitor() {
        for (BaseTrackerView trackerView : mTrackerViews) {
            trackerView.stopMonitor();
        }
    }

    /**
     * 开始更新数据
     */
    private void startMonitor() {
        for (BaseTrackerView trackerView : mTrackerViews) {
            trackerView.startMonitor();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "monitor service onCreate");

        createMonitorViews();

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.START | Gravity.TOP;
        params.setTitle("MonitorService");
        windowManager.addView(mMonitorLayout, params);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, intentFilter);
    }

    /**
     * 添加监控视图
     */
    private void createMonitorViews() {
        mMonitorLayout = new LinearLayout(this);
        mMonitorLayout.setOrientation(LinearLayout.VERTICAL);
        mMonitorLayout.setPadding(4, 4, 4, 4);

        CpuTracker cpuTracker = new CpuTracker(this);
        mCpuTrackerView = new CpuTrackerView(this, cpuTracker);

        MemTracker memTracker = new MemTracker(this);
        mMemTrackerView = new MemTrackerView(this, memTracker);

        //NetworkTracker networkTracker = new NetworkTracker(this);
        //mNetworkTrackerView = new NetworkTrackerView(this, networkTracker);

        //BatteryTracker batteryTracker = new BatteryTracker(this);
        //mBatteryTrackerView = new BatteryTrackerView(this, batteryTracker);

        mTrackerViews = new ArrayList<>();
        mTrackerViews.add(mCpuTrackerView);
        mTrackerViews.add(mMemTrackerView);
        //mTrackerViews.add(mNetworkTrackerView);
        //mTrackerViews.add(mBatteryTrackerView);

        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (BaseTrackerView trackerView : mTrackerViews) {
            mMonitorLayout.addView(trackerView, mLayoutParams);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "monitor service onStartCommand");
        startForeground();
        return START_REDELIVER_INTENT;
    }

    private void startForeground() {
        Intent monitorIntent = new Intent(this, PerformanceMonitorActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, monitorIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker(getString(R.string.notification_monitor_ticker));
        builder.setContentText(getString(R.string.notification_monitor_content)).setContentTitle(getString(R.string.notification_monitor_title));
        builder.setSmallIcon(R.drawable.ic_equalizer_white_24dp);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_equalizer_white_48dp));
        builder.setContentIntent(pendingIntent);
        startForeground(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "monitor service onDestroy");

        stopMonitor();
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.removeView(mMonitorLayout);
        mMonitorLayout = null;

        stopForeground(true);
        unregisterReceiver(mReceiver);
    }
}

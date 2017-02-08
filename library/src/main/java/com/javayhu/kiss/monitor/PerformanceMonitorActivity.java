package com.javayhu.kiss.monitor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.javayhu.kiss.monitor.permission.FloatWindowPermission;
import com.javayhu.kiss.monitor.util.Utils;

/**
 * 性能监控
 * <p>
 * Created by hujiawei on 2017/1/19.
 */
public class PerformanceMonitorActivity extends AppCompatActivity {

    private static final String TAG = PerformanceMonitorActivity.class.getSimpleName();

    private final String KEY_MONITOR_STATE = "monitor_state";

    private LinearLayout mLayoutParent;

    private boolean mChecked = false;
    private SharedPreferences mSharedPreferences;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PerformanceMonitorActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLayoutParent = new LinearLayout(this);
        mLayoutParent.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mLayoutParent.setOrientation(LinearLayout.VERTICAL);
        int padding = Utils.dp2px(16, getResources());
        mLayoutParent.setPadding(0, padding, 0, padding);
        setContentView(mLayoutParent);

        getSupportActionBar().setTitle(R.string.app_name);

        mSharedPreferences = getSharedPreferences("monitor", MODE_PRIVATE);
        mChecked = mSharedPreferences.getBoolean(KEY_MONITOR_STATE, false);
        initUI();

        if (!FloatWindowPermission.getInstance().checkPermission(this)) {
            FloatWindowPermission.getInstance().applyPermission(this);
        }

        if (mChecked) {
            startMonitorService();
        } else {
            stopMonitorService();
        }
    }

    private void initUI() {
        Switch monitorSwitch = addSwitchItem(getString(R.string.switch_monitor_text), mChecked);
        monitorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startMonitorService();
                    Toast.makeText(PerformanceMonitorActivity.this, R.string.monitor_on, Toast.LENGTH_SHORT).show();
                } else {
                    stopMonitorService();
                    Toast.makeText(PerformanceMonitorActivity.this, R.string.monitor_off, Toast.LENGTH_SHORT).show();
                }
                mChecked = isChecked;
                mSharedPreferences.edit().putBoolean(KEY_MONITOR_STATE, mChecked).apply();
            }
        });
        mLayoutParent.addView(monitorSwitch);
    }

    private void startMonitorService() {
        String packageName = getPackageName();
        Intent service = (new Intent()).setClassName(packageName, packageName + ".MonitorService");
        startService(service);
    }

    private void stopMonitorService() {
        String packageName = getPackageName();
        Intent service = (new Intent()).setClassName(packageName, packageName + ".MonitorService");
        stopService(service);
    }

    private Switch addSwitchItem(String text, boolean isCheck) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dp2px(45, getResources()));
        int margin = Utils.dp2px(10, getResources());
        lp.setMargins(margin, 0, margin, 0);
        Switch item = new Switch(this);
        item.setText(text);
        item.setTextSize(16);
        item.setTextOff(getString(R.string.switch_off));
        item.setTextOn(getString(R.string.switch_on));
        item.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        item.setChecked(isCheck);
        item.setLayoutParams(lp);
        return item;
    }
}

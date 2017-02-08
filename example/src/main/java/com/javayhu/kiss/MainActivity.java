package com.javayhu.kiss;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.javayhu.kiss.monitor.PerformanceMonitorActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void gotoMonitor(View view) {
        PerformanceMonitorActivity.startActivity(this);
    }
}

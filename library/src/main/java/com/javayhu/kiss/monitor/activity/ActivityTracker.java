package com.javayhu.kiss.monitor.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.ArrayMap;

import com.javayhu.kiss.monitor.base.BaseTracker;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 当前应用的栈顶Activity
 * <p>
 * Created by javayhu on 2/13/17.
 */
public class ActivityTracker extends BaseTracker {

    public Activity mTopActivity;

    public ActivityTracker(Context context) {
        super(context);
    }

    @Override
    public boolean updateContent() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = null;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                activities = (HashMap) activitiesField.get(activityThread);
            } else {
                activities = (ArrayMap) activitiesField.get(activityThread);
            }
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    mTopActivity = (Activity) activityField.get(activityRecord);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}

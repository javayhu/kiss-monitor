package com.javayhu.kiss.monitor.util;

import android.content.res.Resources;

/**
 * 工具类
 *
 * Created by javayhu on 2017/2/7.
 */
public class Utils {

    public static final int dp2px(float dp, Resources res) {
        return (int) (dp * res.getDisplayMetrics().density + 0.5f);
    }

}

package com.javayhu.kiss.monitor.base;

import android.content.Context;

/**
 * 数据获取的抽象类
 * <p>
 * Created by javayhu on 1/20/17.
 */
public abstract class BaseTracker {

    protected Context mContext;

    public BaseTracker(Context context) {
        mContext = context;
    }

    /**
     * 抽象方法：更新数据
     */
    public abstract boolean updateContent();

}

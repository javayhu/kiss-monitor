package com.javayhu.kiss.monitor.util;

import java.lang.reflect.Method;

/**
 * 反射版本的android.os.Debug.MemoryInfo
 * <p>
 * Created by hujiawei on 2017/1/20.
 */
public class LocalMemoryInfo {

    static final String CLASSNAME = "android.os.Debug$MemoryInfo";

    static final Class clazz;
    static final Method methodGetTotalUss;

    static {
        clazz = ReflectionUtil.getReflectClass(CLASSNAME);
        methodGetTotalUss = ReflectionUtil.getReflectMethod(clazz, "getTotalUss", (Class[]) null);
    }

    public static int getTotalUss(Object instance) {
        return (int) ReflectionUtil.invokeMethod(instance, methodGetTotalUss, (Object[]) null);
    }

}
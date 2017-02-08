package com.javayhu.kiss.monitor.util;

import java.lang.reflect.Method;

/**
 * 反射版本的android.os.Process
 * <p>
 * Created by hujiawei on 2017/1/20.
 */
public class LocalProcess {

    static final String CLASSNAME = "android.os.Process";

    static final Class clazz;
    static final Method methodGetPss;
    static final Method methodGetPids;
    static final Method methodReadProcFile;

    static {
        clazz = ReflectionUtil.getReflectClass(CLASSNAME);
        methodGetPss = ReflectionUtil.getReflectMethod(clazz, "getPss", new Class[]{int.class});
        methodGetPids = ReflectionUtil.getReflectMethod(clazz, "getPids", new Class[]{String.class, int[].class});
        methodReadProcFile = ReflectionUtil.getReflectMethod(clazz, "readProcFile", new Class[]{String.class, int[].class, String[].class, long[].class, float[].class});
    }

    public static boolean readProcFile(String file, int[] format, String[] outStrings, long[] outLongs, float[] outFloats) {
        return (boolean) ReflectionUtil.invokeStaticMethod(methodReadProcFile, new Object[]{file, format, outStrings, outLongs, outFloats});
    }

    public static long getPss(int pid) {
        return (long) ReflectionUtil.invokeStaticMethod(methodGetPss, new Object[]{pid});
    }

    public static int[] getPids(String path, int[] lastArray) {
        return (int[]) ReflectionUtil.invokeStaticMethod(methodGetPids, new Object[]{path, lastArray});
    }

}
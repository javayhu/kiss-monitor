package com.javayhu.kiss.monitor.util;

import java.lang.reflect.Method;

/**
 * 反射工具类
 * <p>
 * Created by hujiawei on 2017/1/20.
 */
public class ReflectionUtil {

    public static Class<?> getReflectClass(String className) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }

    public static Method getReflectMethod(String className, String methodName, Class<?>... objs) {
        Method method = null;
        Class<?> clazz = getReflectClass(className);
        if (clazz == null) {
            return null;
        }
        try {
            method = clazz.getMethod(methodName, objs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return method;
    }

    public static Method getReflectMethod(Class<?> className, String methodName, Class<?>... objs) {
        Method method = null;
        if (className == null) {
            return null;
        }
        try {
            method = className.getMethod(methodName, objs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return method;
    }

    public static Object invokeStaticMethod(Method method, Object[] args) {
        Object returnValue = null;
        try {
            returnValue = method.invoke(null, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    public static Object invokeMethod(Object receiver, Method method, Object[] args) {
        Object returnValue = null;
        try {
            returnValue = method.invoke(receiver, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue;
    }
}

package com.itheima.reggie.common;

/**
 * @author Jonydom
 * @description 基于ThreadLocal封装工具类，用户保存和获取当前用户id
 * @date 2024-04-10 12:50
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}

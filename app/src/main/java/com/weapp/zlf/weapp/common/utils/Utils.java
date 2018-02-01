package com.weapp.zlf.weapp.common.utils;

import com.weapp.zlf.weapp.MainApplication;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 16/12/08
 *     desc  : Utils初始化相关
 * </pre>
 */
public class Utils {

    private static MainApplication context;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(MainApplication context) {
//        Utils.context = context.getApplicationContext();
        Utils.context = context;
        AssertUtils.init(context);
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static MainApplication getContext() {
        if (context != null) return context;
        throw new NullPointerException("u should init first");
    }
}
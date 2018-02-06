package com.weapp.zlf.weapp.common.utils;


import com.weapp.zlf.weapp.R;

/**
 * Created by 郭攀峰 on 2015/9/15.
 */
public class Constant {
    public static final String WIFI_HOT_SPOT_SSID_PREFIX = Utils.getContext().getString(R.string.app_name);
    public static final String FREE_SERVER = "192.168.43.1";
    public static String DIR_DIARY_PHOTO;
    public static String DIR_DB;

    public interface MSG {
        public static final int PICTURE_OK = 0;
        public static final int APP_OK = 1;
    }

    public static final String DB_NAME = "weapp.db";
}

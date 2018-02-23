package com.weapp.zlf.weapp.common.utils;


import android.os.Environment;

import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.p2pmanager.p2pcore.P2PManager;

import java.io.File;

/**
 * Created by 郭攀峰 on 2015/9/15.
 */
public class Constant {
    public static final String WIFI_HOT_SPOT_SSID_PREFIX = Utils.getContext().getString(R.string.app_name);
    public static final String FREE_SERVER = "192.168.43.1";
    public static String DIR_DIARY_PHOTO; // 当前照片路径
    public static String DIR_DB; // 当前数据库路径
    public static final String[] DB_DIRS = new String[] {P2PManager.getSaveDir() + File.separator + "Database",
            P2PManager.getSaveDir() + File.separator + "Database1"};
    public static final String[] PHOTO_DIRS = new String[] {P2PManager.getSaveDir() + File.separator + Environment.DIRECTORY_PICTURES,
            P2PManager.getSaveDir() + File.separator + Environment.DIRECTORY_PICTURES + "1"};

    public interface MSG {
        public static final int PICTURE_OK = 0;
        public static final int APP_OK = 1;
        public static final int DATABASE_OK = 2;
    }

    public static final String DB_NAME = "weapp.db";
}

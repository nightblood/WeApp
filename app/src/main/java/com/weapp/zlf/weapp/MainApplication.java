package com.weapp.zlf.weapp;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.weapp.zlf.weapp.bean.UserInfo;
import com.weapp.zlf.weapp.common.utils.Constant;
import com.weapp.zlf.weapp.common.utils.FileUtils;
import com.weapp.zlf.weapp.common.utils.SPUtils;
import com.weapp.zlf.weapp.common.utils.Utils;
import com.weapp.zlf.weapp.event.UserInfoChangeEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;
import org.xutils.x;

import java.io.File;

/**
 * Created by zhuliangfei on 2018/1/5.
 */

public class MainApplication extends Application {

    private static final String TAG = MainApplication.class.getSimpleName();
    public static UserInfo mUserInfo;
    private DbManager mDbManager;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
        Utils.init(this);
        initConstants();
        initDb();
        initUserInfo();
    }

    private void initConstants() {
        Constant.DIR_DB = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        Constant.DIR_DIARY_PHOTO = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        FileUtils.createOrExistsDir(getFilesDir());
        FileUtils.createOrExistsDir(Constant.DIR_DB);
        FileUtils.createOrExistsDir(Constant.DIR_DIARY_PHOTO);
    }

    private void initUserInfo() {
        mUserInfo = new UserInfo();
        SPUtils spUtils = new SPUtils("user_info");
        mUserInfo.setHonor(spUtils.getString("honor"));
        mUserInfo.setPortrait(spUtils.getString("portrait"));
        mUserInfo.setName(spUtils.getString("name"));
        mUserInfo.setId(spUtils.getString("id"));
        Log.d(TAG, "onCreate: " + mUserInfo.getId() + ", " + mUserInfo.getName() + ", " + mUserInfo.getHonor()+ ", " + mUserInfo.getPortrait());
    }

    private void initDb() {
        /**
         * 初始化DaoConfig配置
         */
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                //设置数据库名，默认xutils.db
                .setDbName(Constant.DB_NAME)
                //设置数据库路径，默认存储在app的私有目录
                .setDbDir(new File(Constant.DIR_DB))
                //设置数据库的版本号
                .setDbVersion(1)
                //设置数据库打开的监听
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        //开启数据库支持多线程操作，提升性能，对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                //设置数据库更新的监听
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                    }
                })
                //设置表创建的监听
                .setTableCreateListener(new DbManager.TableCreateListener() {
                    @Override
                    public void onTableCreated(DbManager db, TableEntity<?> table){
//                        Log.i("JAVA", "onTableCreated：" + table.getName());
                    }
                });
        //设置是否允许事务，默认true
        //.setAllowTransaction(true)

        mDbManager = x.getDb(daoConfig);


    }
    public DbManager getDbManager() {
        return mDbManager;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserInfoChange(UserInfoChangeEvent event) {
        mUserInfo = event.mUserInfo;
    }
}

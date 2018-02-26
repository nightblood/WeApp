package com.weapp.zlf.weapp.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.guo.duoduo.randomtextview.RandomTextView;
import com.guo.duoduo.rippleoutlayout.RippleOutLayout;
import com.guo.duoduo.rippleoutview.RippleView;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.DiaryBean;
import com.weapp.zlf.weapp.bean.ImageBean;
import com.weapp.zlf.weapp.common.Cache;
import com.weapp.zlf.weapp.common.accesspoint.AccessPointManager;
import com.weapp.zlf.weapp.common.utils.Constant;
import com.weapp.zlf.weapp.common.utils.FileUtils;
import com.weapp.zlf.weapp.common.utils.NetworkUtils;
import com.weapp.zlf.weapp.common.utils.SPUtils;
import com.weapp.zlf.weapp.common.utils.ToastUtils;
import com.weapp.zlf.weapp.common.utils.Utils;
import com.weapp.zlf.weapp.p2pmanager.p2pconstant.P2PConstant;
import com.weapp.zlf.weapp.p2pmanager.p2pcore.P2PManager;
import com.weapp.zlf.weapp.p2pmanager.p2pentity.P2PFileInfo;
import com.weapp.zlf.weapp.p2pmanager.p2pentity.P2PNeighbor;
import com.weapp.zlf.weapp.p2pmanager.p2pinterface.Melon_Callback;
import com.weapp.zlf.weapp.p2pmanager.p2pinterface.ReceiveFile_Callback;
import com.weapp.zlf.weapp.ui.adapter.FileTransferAdapter;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;


@ContentView(R.layout.activity_receive)
public class ReceiveActivity extends BaseActivity implements AccessPointManager.OnWifiApStateChangeListener {

    @ViewInject(R.id.iv_title_right)
    private ImageView mIvRight;
    @ViewInject(R.id.tv_title_name)
    private TextView mTvTitle;
    private static final String tag = ReceiveActivity.class.getSimpleName();

    private AccessPointManager mWifiApManager = null;
    private Random random = new Random();
    private SweetAlertDialog progressDialog;
    @ViewInject(R.id.activity_receive_radar_wifi)
    private TextView wifiName;

    @ViewInject(R.id.activity_receive_ripple_layout)
    private RippleOutLayout rippleOutLayout;
    @ViewInject(R.id.activity_receive_rand_textview)
    private RandomTextView randomTextView;

    private P2PManager p2PManager;
    private String alias;

    @ViewInject(R.id.activity_receive_layout)
    private RelativeLayout receiveLayout;
    @ViewInject(R.id.activity_receive_listview)
    private ListView receiveListView;
    private FileTransferAdapter transferAdapter;

    @Override
    protected void initView() {
        super.initView();
        mIvRight.setVisibility(View.GONE);
        mTvTitle.setText(getString(R.string.share_diary));

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_receive_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view,
                        getResources().getString(R.string.file_transfering_exit),
                        Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(R.string.ok),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        finish();
                                    }
                                }).show();
            }
        });*/

//            alias = Build.DEVICE;
        SPUtils spUtils = new SPUtils("user_info");
        alias = spUtils.getString("name");
        if (TextUtils.isEmpty(alias))
            alias = Build.DEVICE;
        TextView radar_scan_name = (TextView) findViewById(R.id.activity_receive_scan_name);
        radar_scan_name.setText(alias);


        receiveLayout.setVisibility(View.VISIBLE);
        receiveListView.setVisibility(View.GONE);

        rippleOutLayout.startRippleAnimation();

        randomTextView.setMode(RippleView.MODE_IN);
        randomTextView
                .setOnRippleViewClickListener(new RandomTextView.OnRippleViewClickListener() {
                    @Override
                    public void onRippleViewClicked(View view) {
                        //接受了对方的发送请求
                        p2PManager.ackReceive();
                        receiveLayout.setVisibility(View.GONE);
                        receiveListView.setVisibility(View.VISIBLE);
                    }
                });

        if (!NetworkUtils.isWifiConnected(Utils.getContext())) { //create wifi hot spot
            Log.d(tag, "no WiFi init wifi hotspot");
            intWifiHotSpot();
        } else {
            Log.d(tag, "useWiFi");
            wifiName.setText(String.format(getString(R.string.send_connect_to), NetworkUtils.getCurrentSSID(ReceiveActivity.this)));
        }
        initP2P();
    }

    private void initP2P() {
        p2PManager = new P2PManager(getApplicationContext());
        P2PNeighbor melonInfo = new P2PNeighbor();
        melonInfo.alias = alias;
        String ip = null;
        try {
            ip = AccessPointManager.getLocalIpAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(ip))
            ip = NetworkUtils.getLocalIp(getApplicationContext());
        melonInfo.ip = ip;

        p2PManager.start(melonInfo, new Melon_Callback() {
            @Override
            public void Melon_Found(P2PNeighbor melon) {
            }

            @Override
            public void Melon_Removed(P2PNeighbor melon) {
            }
        });

        p2PManager.receiveFile(new ReceiveFile_Callback() {
            @Override
            public boolean QueryReceiving(P2PNeighbor src, P2PFileInfo[] files) {
                if (src != null) {
                    randomTextView.addKeyWord(src.alias);
                    randomTextView.show();
                    if (files != null) {
                        for (P2PFileInfo file : files) {
                            if (!Cache.selectedList.contains(file))
                                Cache.selectedList.add(file);
                        }
                        transferAdapter = new FileTransferAdapter(getApplicationContext());
                        receiveListView.setAdapter(transferAdapter);
                    }
                }
                return false;
            }

            @Override
            public void BeforeReceiving(P2PNeighbor src, P2PFileInfo[] files) {

            }

            @Override
            public void OnReceiving(P2PFileInfo file) {
                int index = -1;
                if (Cache.selectedList.contains(file)) {
                    index = Cache.selectedList.indexOf(file);
                }
                if (index != -1) {
                    P2PFileInfo fileInfo = Cache.selectedList.get(index);
                    fileInfo.percent = file.percent;
                    transferAdapter.notifyDataSetChanged();
                } else {
                    Log.d(tag, "onReceiving index error");
                }
            }

            @Override
            public void AfterReceiving() {
                ToastUtils.showLongToast(getString(R.string.file_receive_completed) + " " + Cache.selectedList.get(0));
                P2PFileInfo diaryInfo = null;
                P2PFileInfo imageInfo = null;
                for (P2PFileInfo info : Cache.selectedList) {
                    if (Constant.NAME_DIARY_DB.equals(info.name)) {
                        diaryInfo = info;
                    } else if (Constant.NAME_PHOTO_DB.equals(info.name)){
                        imageInfo = info;
                    }
                }
                add2Database(diaryInfo, imageInfo);
//                finish();
            }

            @Override
            public void AbortReceiving(int error, String name) {
                switch (error) {
                    case P2PConstant.CommandNum.SEND_ABORT_SELF:
                        ToastUtils.showLongToast(String.format(getString(R.string.send_abort_self), name));
                        finish();
                        break;
                }
            }
        });
    }

    private void add2Database(P2PFileInfo diaryInfo, P2PFileInfo imageInfo) {

        if (diaryInfo == null)
            return;
        List<ImageBean> imageBeans = null;
        if (imageInfo != null) {
            String imageContent = FileUtils.readFile2String(imageInfo.path, "utf-8");
            imageBeans = JSON.parseArray(imageContent, ImageBean.class);
        }

        String diaryContent = FileUtils.readFile2String(diaryInfo.path, "utf-8");
        List<DiaryBean> diaryBeans = JSON.parseArray(diaryContent, DiaryBean.class);
        DbManager dbManager = Utils.getContext().getDbManager();

        List<ImageBean> tempImageBeans = null;
        for (DiaryBean bean : diaryBeans) {
            try {
                if (null != imageBeans) {
                    tempImageBeans = new ArrayList<>();
                    for (ImageBean imageBean : imageBeans) {
                        if (imageBean.getDiaryId() == bean.getId()) {
                            tempImageBeans.add(imageBean);
                        }
                    }
                }
                bean.setGender(1);
                dbManager.saveBindingId(bean);
                if (tempImageBeans != null) {
                    for (ImageBean imageBean : tempImageBeans) {
                        imageBean.setDiaryId(bean.getId());
                        dbManager.saveBindingId(imageBean);
                    }
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stop wifi hot spot
        closeAccessPoint();
        if (rippleOutLayout != null)
            rippleOutLayout.stopRippleAnimation();

        if (p2PManager != null) {
            p2PManager.cancelReceive();
            p2PManager.stop();
        }

        Cache.selectedList.clear();
    }

    private void intWifiHotSpot() {
        progressDialog = new SweetAlertDialog(this);
//        progressDialog = new CommonProgressDialog(ReceiveActivity.this);
//        progressDialog.setMessage(getString(R.string.wifi_hotspot_creating));
//        progressDialog.show();

        mWifiApManager = new AccessPointManager(Utils.getContext());
        mWifiApManager.setWifiApStateChangeListener(this);
        createAccessPoint();
    }

    private void createAccessPoint() {
        mWifiApManager.createWifiApSSID(Constant.WIFI_HOT_SPOT_SSID_PREFIX + Build.MODEL + "-" + random.nextInt(1000));

        if (!mWifiApManager.startWifiAp()) {
            if (progressDialog != null)
                progressDialog.dismiss();

            ToastUtils.showLongToast(getString(R.string.wifi_hotspot_fail));
            onBackPressed();
        }
    }

    private void closeAccessPoint() {
        try {
            if (mWifiApManager != null && mWifiApManager.isWifiApEnabled()) {
                mWifiApManager.stopWifiAp(false);
                mWifiApManager.destroy(this);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWifiStateChanged(int state) {
        if (state == AccessPointManager.WIFI_AP_STATE_ENABLED) {
            onBuildWifiApSuccess();
        } else if (AccessPointManager.WIFI_AP_STATE_FAILED == state) {
            onBuildWifiApFailed();
        }
    }

    private void onBuildWifiApFailed() {
        ToastUtils.showLongToast(getString(R.string.wifi_hotspot_fail));

        if (progressDialog != null)
            progressDialog.dismiss();

        onBackPressed();
    }

    private void onBuildWifiApSuccess() {
        if (progressDialog != null)
            progressDialog.dismiss();

        wifiName.setText(String.format(getString(R.string.send_connect_to), mWifiApManager.getWifiApSSID()));
    }

    @Event(R.id.iv_title_left)
    private void leftClick(View view) {
        finish();
    }
    public static void launch(Context context) {
        Intent intent = new Intent(context, ReceiveActivity.class);
        context.startActivity(intent);
    }
}

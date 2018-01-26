package com.weapp.zlf.weapp.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.BtMacInfo;
import com.weapp.zlf.weapp.common.BluetoothChatService;
import com.weapp.zlf.weapp.ui.widge.CustomViewPager;
import com.weapp.zlf.weapp.ui.widge.FixedSpeedScroller;
import com.weapp.zlf.weapp.ui.widge.RadarViewGroup;
import com.weapp.zlf.weapp.ui.widge.ZoomOutPageTransformer;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by zhuliangfei on 2018/1/23.
 */
@ContentView(R.layout.activity_search)
public class SearchActivity extends BaseActivity implements ViewPager.OnPageChangeListener, RadarViewGroup.IRadarClickListener {

    private static final String TAG = SearchActivity.class.getSimpleName();
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_TOAST = 2;
    public static final int MESSAGE_DEVICE_NAME = 3;
    public static final String TOAST = "toast";
    public static final String DEVICE_NAME = "name";
    private int[] mResIds = {R.drawable.plant, R.drawable.plant_1, R.drawable.plant_2, R.drawable.plant_3,
            R.drawable.plant_4};
    @ViewInject(R.id.vp)
    private CustomViewPager viewPager;
    private RelativeLayout ryContainer;
    @ViewInject(R.id.radar)
    private RadarViewGroup radarViewGroup;
    @ViewInject(R.id.iv_title_right)
    private ImageView mIvRight;
    @ViewInject(R.id.tv_name)
    private TextView mTvName;

    private BluetoothAdapter mBluetoothAdapter;
    private List<BtMacInfo> mDatas = new ArrayList<>();
    private FixedSpeedScroller scroller;
    private int mPosition;
    private ViewpagerAdapter mViewPagerAdapter;
    private BluetoothChatService mChatService;
    private ListenerThread StartListenThread;
    @ViewInject(R.id.text)
    private TextView mTvText;
    private String mBluetoothType;
    private String mAddress;

    @Override
    protected void initView() {
        super.initView();
        mIvRight.setVisibility(View.GONE);

        initBluetooth();
        initViewPager();
        radarViewGroup.setiRadarClickListener(this);
    }

    private void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "本地蓝牙不可用", Toast.LENGTH_SHORT).show();
        }

        String address = mBluetoothAdapter.getAddress(); //获取本机蓝牙MAC地址
        String name = mBluetoothAdapter.getName();   //获取本机蓝牙名称
        // 若蓝牙没打开
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();  //打开蓝牙，需要BLUETOOTH_ADMIN权限
        }

        mTvName.setText(name + "\n" + address);

        //获取已配对蓝牙设备
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        Log.d(TAG, "bonded device size =" + devices.size());
        for (BluetoothDevice bonddevice : devices) {
            Log.d(TAG, "bonded device name =" + bonddevice.getName() + " address" + bonddevice.getAddress());
            mDatas.add(new BtMacInfo(bonddevice.getAddress(), bonddevice.getName()));
        }
    }


    private void notifyRefreshAdapter() {
        if (mViewPagerAdapter != null) {
            mViewPagerAdapter = null;
        }
        mViewPagerAdapter = new ViewpagerAdapter();
        viewPager.setAdapter(mViewPagerAdapter);
    }

    private void initViewPager() {
        mViewPagerAdapter = new ViewpagerAdapter();
        viewPager.setAdapter(mViewPagerAdapter);
        //设置缓存数为展示的数目
        viewPager.setOffscreenPageLimit(mResIds.length);
        viewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.viewpager_margin));
        //设置切换动画
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.addOnPageChangeListener(this);
        setViewPagerSpeed(250);
    }

    /**
     * 设置ViewPager切换速度
     *
     * @param duration
     */
    private void setViewPagerSpeed(int duration) {
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            scroller = new FixedSpeedScroller(SearchActivity.this, new AccelerateInterpolator());
            field.set(viewPager, scroller);
            scroller.setmDuration(duration);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mPosition = position;
    }

    @Override
    public void onPageSelected(int position) {
        radarViewGroup.setCurrentShowItem(position);
//        LogUtil.m("当前位置 " + mPosition);
//        LogUtil.m("速度 " + viewPager.getSpeed());
        //当手指左滑速度大于2000时viewpager右滑（注意是item+2）
        if (viewPager.getSpeed() < -1800) {

            viewPager.setCurrentItem(mPosition + 2);
//            LogUtil.m("位置 " + mPosition);
            viewPager.setSpeed(0);
        } else if (viewPager.getSpeed() > 1800 && mPosition > 0) {
            //当手指右滑速度大于2000时viewpager左滑（注意item-1即可）
            viewPager.setCurrentItem(mPosition - 1);
//            LogUtil.m("位置 " + mPosition);
            viewPager.setSpeed(0);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onRadarItemClick(int position) {
        viewPager.setCurrentItem(position);
    }

    @Event(value = R.id.iv_top_l)
    private void leftClick(View view) {
        finish();
    }

    class ViewpagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final BtMacInfo info = mDatas.get(position);
            //设置一大堆演示用的数据，麻里麻烦~~
            View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.viewpager_layout, null);
            ImageView ivPortrait = (ImageView) view.findViewById(R.id.iv);
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            TextView tvMac = (TextView) view.findViewById(R.id.tv_mac);
            tvMac.setText(info.mac);
            if (TextUtils.isEmpty(info.name)) {
                tvName.setText(info.mac.substring(0, 5));
            } else {
                tvName.setText(info.name);
            }
            ivPortrait.setImageResource(mResIds[position % mResIds.length]);
            view.findViewById(R.id.tv_send).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMsg(info.mac);
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

    }

    private void sendMsg(String mac) {
        mAddress = mac;
        StartListenThread = new ListenerThread();
        StartListenThread.start();
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                StartListenThread = new ListenerThread();
                StartListenThread.start();
            }
        }
    }

    private String strReceiveData;
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    strReceiveData += readMessage + "\n";
                    System.out.println("rev :" + readMessage);
                    mTvText.setText(strReceiveData);
                    // mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null)
            mChatService.stop();
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class ListenerThread extends Thread {
        // The local server socket

        public void run() {
            // Listen to the server socket if we're not connected
            for (int i = 0; i < 100; i++) {
                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    if ("server".equals(mBluetoothType)) {
                        mChatService.start();
                    } else if ("client".equals(mBluetoothType) && !TextUtils.isEmpty(mAddress)) {
                        if(mChatService.getState() != BluetoothChatService.STATE_CONNECTED)
                            Log.d(TAG, "run: connecting...");
                            connectDevice(mAddress);
                    }
                    break;
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "not connected!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            // mOutStringBuffer.setLength(0);
            //  mOutEditText.setText(mOutStringBuffer);
        }
    }

    private void connectDevice(String address ) {
        // Get the device MAC address
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, false);
    }

    @Event(value = R.id.btn_accept)
    private void acceptClick(View view) {
        mBluetoothType = "server";
        if (null == mChatService)
            mChatService = new BluetoothChatService(this, mHandler, "server");
    }
    @ViewInject(R.id.et_content)
    private EditText mEtContent;
    @Event(value = R.id.btn_send)
    private void sendClick(View view) {
        mBluetoothType = "client";
        if (null == mChatService)
            mChatService = new BluetoothChatService(this);
        String content = mEtContent.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            sendMessage(content);
        }
    }
}

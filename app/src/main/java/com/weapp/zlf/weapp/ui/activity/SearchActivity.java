package com.weapp.zlf.weapp.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.BtMacInfo;
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

/**
 * Created by zhuliangfei on 2018/1/23.
 */
@ContentView(R.layout.activity_search)
public class SearchActivity extends BaseActivity implements ViewPager.OnPageChangeListener, RadarViewGroup.IRadarClickListener {

    private static final String TAG = SearchActivity.class.getSimpleName();
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
    private BluetoothClient mClient;

    @Override
    protected void initView() {
        super.initView();
        mIvRight.setVisibility(View.GONE);
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
// 打印信息
        Log.i("getAddress() : ", address);
        Log.i("getName() : ", name);

        mTvName.setText(name + "\n" + address);
        mClient = new BluetoothClient(this);

        mClient.registerBluetoothBondListener(mBluetoothBondListener);
        mClient.registerBluetoothStateListener(mBluetoothStateListener);

        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 3)   // 先扫BLE设备3次，每次3s
                .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
                .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
                .build();

        mClient.search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {

            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                Beacon beacon = new Beacon(device.scanRecord);
                BluetoothLog.v(String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));
            }

            @Override
            public void onSearchStopped() {

            }

            @Override
            public void onSearchCanceled() {

            }
        });


/*
        mBtHelperClient = BtHelperClient.from(this);
        mBtHelperClient.searchDevices(new OnSearchDeviceListener() {

            @Override
            public void onStartDiscovery() {
                // Call before discovery devices
                Log.d(TAG, "onStartDiscovery()");

            }

            @Override
            public void onNewDeviceFounded(BluetoothDevice bluetoothDevice) {
                Log.d(TAG, "new device: " + bluetoothDevice.getName() + " " + bluetoothDevice.getAddress());

            }

            @Override
            public void onSearchCompleted(List<BluetoothDevice> bondedList, List<BluetoothDevice> newList) {
                // Call when the discovery process completed
                Log.d(TAG, "SearchCompleted: bondedList" + bondedList.toString());
                Log.d(TAG, "SearchCompleted: newList" + newList.toString());

                for (BluetoothDevice device : newList) {
                    mDatas.add(new BtMacInfo(device.getAddress(), device.getName()));
                }
                radarViewGroup.setDatas(mDatas);

                notifyRefreshAdapter();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

        });*/

        initViewPager();
        radarViewGroup.setiRadarClickListener(this);
    }
    private final BluetoothStateListener mBluetoothStateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {

        }

    };
    private final BluetoothBondListener mBluetoothBondListener = new BluetoothBondListener() {
        @Override
        public void onBondStateChanged(String mac, int bondState) {
            // bondState = Constants.BOND_NONE, BOND_BONDING, BOND_BONDED
            if (bondState == Constants.BOND_BONDED) {

            }
        }
    };

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClient.unregisterBluetoothStateListener(mBluetoothStateListener);
        mClient.unregisterBluetoothBondListener(mBluetoothBondListener);
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

    }

}

package com.weapp.zlf.weapp.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.common.BluetoothServerThread;
import com.weapp.zlf.weapp.common.utils.ToastUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

/**
 * Created by zhuliangfei on 2018/1/26.
 */

@ContentView(R.layout.activity_bluetooth)
public class CommunicateBluetoothActivity extends BaseActivity {

    public static final int MSG_RECEIVED = 1;
    @ViewInject(R.id.listview)
    private ListView mList;
    @ViewInject(R.id.tv_content)
    private TextView mTvContent;
    private BluetoothAdapter mBluetoothAdapter;
    @ViewInject(R.id.iv_received)
    private ImageView mIvReceived;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_RECEIVED) {
                String obj = (String) msg.obj;
                if (obj.startsWith("file")) {
                    Glide.with(CommunicateBluetoothActivity.this).load(obj).into(mIvReceived);
                } else {
                    mTvContent.setText((String) msg.obj);
                }
            }
            return false;
        }
    });
    private BluetoothServerThread mBluetoothServerThread;
    private BluetoothSocket clientSocket;
    private ArrayList<String> mImages;

    @Override
    protected void initView() {
        super.initView();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "本地蓝牙不可用", Toast.LENGTH_SHORT).show();
            finish();
        }
        // 若蓝牙没打开
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();  //打开蓝牙，需要BLUETOOTH_ADMIN权限
        }
    }


    @Event(R.id.btn_server)
    private void server(View view) {
        initServer();
    }

    @Event(R.id.btn_client)
    private void client(View view) {
        initClient();
    }
    @Event(R.id.btn_image_pick)
    private void imagePick(View view) {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);
    }
    @ViewInject(R.id.iv_image)
    private ImageView mIvImage;
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            if (data != null) {
                mImages = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                Glide.with(this).load(mImages.get(0)).into(mIvImage);
            }
        }
    }
    private void initServer() {
        mBluetoothServerThread = new BluetoothServerThread(mBluetoothAdapter, mHandler);
        mBluetoothServerThread.start();
    }

    private void initClient() {
        int count = 0;

        Set<BluetoothDevice> paireDevices = mBluetoothAdapter.getBondedDevices();//得到已经绑定的蓝牙设备
        if (paireDevices.size() > 0) {//若存在
            String[] data = new String[paireDevices.size()];
            for (BluetoothDevice bluetoothDevice : paireDevices) {
                data[count++] = bluetoothDevice.getName() + ":" + bluetoothDevice.getAddress();//得到绑定蓝牙设备的名称和地址
            }
            final ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, data);

            mList.setAdapter(adapter);


            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String s = (String) adapter.getItem(position);
                    //获得要连接的蓝牙设备的地址
                    String address = s.substring(s.indexOf(":") + 1).trim();
//                if (null == device) {
                    //获得蓝牙设备，相当于网路客户端制定的socketip地址
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
//                }
                    if (null == clientSocket) {
                        OutputStream os = null;
                        try {
                            clientSocket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("5dd231bf-d217-4e85-a26c-5e5cfda9aa0c"));
                            //开始连接蓝牙设备
                            clientSocket.connect();
                            os = clientSocket.getOutputStream();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (null != os) {
                            //向服务器端发送一个字符串
                            try {
                                if (mImages != null && !mImages.isEmpty()) {
                                    ContentValues cv = new ContentValues();
                                    String uri = "file://" + mImages.get(0);
                                    cv.put("uri", uri);
                                    cv.put("destination", address);
                                    cv.put("direction", 0);
                                    Long ts = System.currentTimeMillis();
                                    cv.put("timestamp", ts);
                                    getContentResolver().insert(Uri.parse("content://com.android.bluetooth.opp/btopp"), cv);
                                } else {
                                    os.write("这是另一台手机发送过来的数据".getBytes("utf-8"));
                                }
                                ToastUtils.showLongToast("send success...");
                            } catch (Exception e) {
                                ToastUtils.showLongToast("send fail..." + e.toString());
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, CommunicateBluetoothActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.disable();
    }
}

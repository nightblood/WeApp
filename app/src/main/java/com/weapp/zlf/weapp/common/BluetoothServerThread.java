package com.weapp.zlf.weapp.common;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.weapp.zlf.weapp.ui.activity.CommunicateBluetoothActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by zhuliangfei on 2018/1/26.
 */

public class BluetoothServerThread extends Thread {
    private Handler handler;
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;
    private InputStream is;
    private OutputStream os;
    public BluetoothServerThread(BluetoothAdapter bluetoothAdapter, Handler handler) {
        this.handler = handler;
        //创建BluetoothServerSocket对象
        try {
            serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("name", UUID.fromString("5dd231bf-d217-4e85-a26c-5e5cfda9aa0c"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        //等待接受蓝牙客户端的请求
        try {
            socket = serverSocket.accept();
            is = socket.getInputStream();
            os = socket.getOutputStream();
            while(true){
                byte[] buffer = new byte[128];
                int count = is.read(buffer);
                Message message = new Message();
                message.what = CommunicateBluetoothActivity.MSG_RECEIVED;
                message.obj = new String(buffer,0, count, "utf-8");
                handler.sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

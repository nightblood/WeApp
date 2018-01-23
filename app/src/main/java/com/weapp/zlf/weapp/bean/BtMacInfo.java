package com.weapp.zlf.weapp.bean;

/**
 * Created by zhuliangfei on 2018/1/23.
 */

public class BtMacInfo {
    public String mac;
    public String name;

    public BtMacInfo(String address, String name) {
        mac = address;
        this.name = name;
    }
}

package com.weapp.zlf.weapp.event;

import com.weapp.zlf.weapp.bean.AnniversaryBean;

/**
 * Created by zhuliangfei on 2018/1/31.
 */

public class AnniversaryEvent {
    public AnniversaryBean bean;
    public AnniversaryEvent(AnniversaryBean bean) {
        this.bean = bean;
    }
}

package com.weapp.zlf.weapp.event;

import com.weapp.zlf.weapp.bean.TodoBean;

/**
 * Created by zhuliangfei on 2018/1/31.
 */

public class TodoEvent {
    public TodoBean bean;
    public TodoEvent(TodoBean todoBean) {
        bean = todoBean;
    }
}

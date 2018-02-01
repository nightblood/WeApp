package com.weapp.zlf.weapp.event;

import com.weapp.zlf.weapp.bean.UserInfo;

/**
 * Created by zhuliangfei on 2018/2/1.
 */

public class UserInfoChangeEvent {

    public UserInfo mUserInfo;

    public UserInfoChangeEvent(UserInfo userInfo) {
        mUserInfo = userInfo;
    }
}

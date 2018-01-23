package com.weapp.zlf.weapp.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.common.utils.BarUtils;

import org.xutils.x;

/**
 * Created by zhuliangfei on 2018/1/9.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        initView();
    }

    protected void initView() {
        int statusBarHeight = BarUtils.getStatusBarHeight(this);
        View titlebar = findViewById(R.id.v_status_bar);
        if (titlebar != null) {
            ViewGroup.LayoutParams layoutParams = titlebar.getLayoutParams();
            layoutParams.height = statusBarHeight;
        }
    }
}

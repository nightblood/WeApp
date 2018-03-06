package com.weapp.zlf.weapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.common.utils.ToastUtils;
import com.weapp.zlf.weapp.common.utils.VibrationUtils;
import com.weapp.zlf.weapp.ui.widge.CountDownView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhuliangfei on 2018/2/27.
 */

@ContentView(R.layout.habit_developing_activity)
public class HabitDevelopingActivity extends BaseActivity implements CountDownView.OnFinishListener {

    @ViewInject(R.id.count_down_view)
    private CountDownView mCdv;
    @ViewInject(R.id.iv_bg)
    private ImageView mIvBg;
    @Override
    protected void initView() {
        super.initView();

        mIvBg.setBackgroundResource(R.drawable.huolieniao);
        mCdv.setTotalTime(10000);
        mCdv.setOnFinishListener(this);
        mCdv.start();
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, HabitDevelopingActivity.class));
    }

    @Override
    public void onFinished() {
        ToastUtils.showShortToast("on finish");
        VibrationUtils.vibrate(this, new long[] {0, 500,1000}, -1);
    }
}

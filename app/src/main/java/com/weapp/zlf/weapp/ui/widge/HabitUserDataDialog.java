package com.weapp.zlf.weapp.ui.widge;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.HabitBean;
import com.weapp.zlf.weapp.bean.HabitUserBean;
import com.weapp.zlf.weapp.common.utils.ConstUtils;
import com.weapp.zlf.weapp.common.utils.ConvertUtils;
import com.weapp.zlf.weapp.common.utils.TimeUtils;

/**
 * Created by zhuliangfei on 2018/2/28.
 */

public class HabitUserDataDialog extends Dialog{
    public HabitUserDataDialog(Context context, HabitUserBean data) {
        super(context, R.style.ActionSheetDialogStyle);
        initView(context, data);
    }

    private void initView(Context context, HabitUserBean data) {
        setContentView(R.layout.dialog_habit_user_data);
        if (data.getId() == 0)
            return;
        TextView tvNum = (TextView) findViewById(R.id.tv_tomato_num);
        TextView tvTime = (TextView) findViewById(R.id.tv_tomato_time);
        tvNum.setText(data.getTomatoNum());
        tvTime.setText("" + ConvertUtils.millis2TimeSpan(data.getTomatoTime(), ConstUtils.TimeUnit.MIN));
    }
}

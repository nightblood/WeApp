package com.weapp.zlf.weapp.ui.widge.alarmclock;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SimpleDialog extends Dialog {
    protected static int default_width = WindowManager.LayoutParams.WRAP_CONTENT; // 默认宽度
    protected static int default_height = WindowManager.LayoutParams.WRAP_CONTENT;// 默认高度
    public static int TYPE_TWO_BT = 2;
    public static int TYPE_NO_BT = 0;
    public TextView dialog_title;
    public EditText dialog_message;
    public Button bt_cancel, bt_confirm;
    private LinearLayout ll_button;
    protected Context mContext;
    private View.OnClickListener listener;
    private View customView;
    //	@Bind(R.id.icon)
    ImageView icon;


    public SimpleDialog(Context context, int style) {
        super(context, com.loonggg.lib.alarmmanager.clock.R.style.FullScreenDialog);
        mContext = context;
        customView = LayoutInflater.from(context).inflate(com.loonggg.lib.alarmmanager.clock.R.layout.dialog_simple, null);

        icon = (ImageView) customView.findViewById(com.loonggg.lib.alarmmanager.clock.R.id.icon);

        ll_button = (LinearLayout) customView.findViewById(com.loonggg.lib.alarmmanager.clock.R.id.ll_button);
        dialog_title = (TextView) customView.findViewById(com.loonggg.lib.alarmmanager.clock.R.id.dialog_title);
        setTitle("提示信息");
        dialog_message = (EditText) customView.findViewById(com.loonggg.lib.alarmmanager.clock.R.id.dialog_message);
        dialog_message.clearFocus();
        bt_confirm = (Button) customView.findViewById(com.loonggg.lib.alarmmanager.clock.R.id.dialog_confirm);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(customView);
        //ButterKnife  view绑定
        //ButterKnife.bind(this,customView);
    }

    public SimpleDialog setClickListener(View.OnClickListener listener) {
        this.listener = listener;
        bt_confirm.setOnClickListener(listener);
        return this;
    }

    public SimpleDialog setMessage(String message) {
        dialog_message.setText(message);
        return this;
    }

    public SimpleDialog setTitle(String title) {
        dialog_title.setText(title);
        return this;
    }

    public SimpleDialog setIcon(int iconResId) {
        dialog_title.setVisibility(View.GONE);
        icon.setVisibility(View.VISIBLE);
        icon.setBackgroundResource(iconResId);

        return this;
    }

}

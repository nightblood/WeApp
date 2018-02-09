package com.weapp.zlf.weapp.ui.widge;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.weapp.zlf.weapp.R;

/**
 * Created by zhuliangfei on 2018/2/9.
 */

public class LockScreenDialog extends Dialog{
    public LockScreenDialog(@NonNull Context context) {
        super(context, R.style.AlertDialogStyle);
        init(context);
    }
    private void init(Context context) {
        setContentView(R.layout.dialog_lock_screen);

    }
}

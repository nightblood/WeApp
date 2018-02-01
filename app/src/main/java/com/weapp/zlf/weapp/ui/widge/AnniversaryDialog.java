package com.weapp.zlf.weapp.ui.widge;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.common.utils.TimeUtils;

import org.xutils.x;

import java.util.Date;

/**
 * Created by zhuliangfei on 2018/1/31.
 */

public class AnniversaryDialog {
    private Builder mBuilder;
    private Dialog dialog;
    private AnniversaryDialog(Builder builder) {
        mBuilder = builder;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mBuilder.mContext).inflate(R.layout.ui_dialog_anniversary, null);
        dialog = new Dialog(mBuilder.mContext, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        if (TextUtils.isEmpty(mBuilder.mContent)) {
            content.setText("没有描述。。。");
        } else {
            content.setText(mBuilder.mContent);
        }
    }

    private void show() {
        dialog.show();
    }

    public static class Builder{
        private final Context mContext;
        private String mTitle;
        private String mContent;

        public Builder(Context context) {
            this.mContext = context;
        }
        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }
        public Builder setContent(String content) {
            this.mContent = content;
            return this;
        }

        public void show() {
            new AnniversaryDialog(this).show();
        }
    }
}

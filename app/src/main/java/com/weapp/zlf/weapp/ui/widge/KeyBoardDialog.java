package com.weapp.zlf.weapp.ui.widge;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.common.utils.KeyboardUtils;

import org.xutils.x;

/**
 * Created by zhuliangfei on 2018/1/15.
 */

public class KeyBoardDialog {
    private Dialog mDialog;
    private EditText mEtContent;
    private Builder mBuilder;

    private KeyBoardDialog(Builder builder) {
        this.mBuilder =  builder;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mBuilder.mContext).inflate(R.layout.ui_dialog_keyboard, null);
        mEtContent = (EditText) view.findViewById(R.id.et_content);
        TextView title = (TextView) view.findViewById(R.id.tv_title);
        title.setText(mBuilder.mTitle);
        final TextView tvNumber = (TextView) view.findViewById(R.id.tv_number);
        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvNumber.setText(s.toString().length() + "字");
            }
        });
        if (!TextUtils.isEmpty(mBuilder.mContent)) {
            mEtContent.setText(mBuilder.mContent);
            mEtContent.setSelection(mBuilder.mContent.length());
        }
        view.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBuilder.mListener != null)
                    mBuilder.mListener.onClick(mEtContent.getText().toString());
                KeyboardUtils.hideSoftInput((Activity) mBuilder.mContext);
                mDialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideSoftInput((Activity) mBuilder.mContext);
                mDialog.dismiss();
            }
        });

        WindowManager windowManager = (WindowManager) mBuilder.mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        view.setMinimumWidth(display.getWidth());

        mDialog = new Dialog(mBuilder.mContext, R.style.ActionSheetDialogStyle);
        mDialog.setContentView(view);
        Window window = mDialog.getWindow();
        window.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = 0;
        lp.y = 0;
        window.setAttributes(lp);
    }

    public void show() {
        mDialog.show();
        x.task().postDelayed(new Runnable() {
            @Override
            public void run() {
                showKeyboard();
            }
        }, 100);
    }

    private void showKeyboard() {
        if (mEtContent != null)
            KeyboardUtils.showSoftInput(mEtContent);
    }

    public interface ClickListener {
        void onClick(String s);
    }

    public static class Builder {
        private Context mContext;
        private String mTitle;
        private String mDesc;
        private String mContent;
        private ClickListener mListener;

        public Builder(Activity activity) {
            this.mContext = activity;
        }
        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }
        public Builder setDesc(String desc) {
            this.mDesc = desc;
            return this;
        }
        public Builder setContent(String content) {
            this.mContent = content;
            return this;
        }
        public Builder setOnClickListener(ClickListener listener) {
            mListener = listener;
            return this;
        }

        public void show() {
            KeyBoardDialog dialog = new KeyBoardDialog(this);
            dialog.show();
        }
    }
}

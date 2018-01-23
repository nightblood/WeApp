package com.weapp.zlf.weapp.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.liangmutian.randomtextviewlibrary.RandomTextView;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.TodoBean;
import com.weapp.zlf.weapp.common.utils.TimeUtils;

import java.util.Date;

/**
 * Created by zhuliangfei on 2018/1/17.
 */

public class NearestTodoDialog {


    private final Builder mBuilder;
    private Dialog dialog;

    public NearestTodoDialog(Builder builder) {
        this.mBuilder = builder;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mBuilder.mContext).inflate(R.layout.ui_dialog_nearest_todo, null);
        dialog = new Dialog(mBuilder.mContext, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);

        TextView time = (TextView) view.findViewById(R.id.tv_time);
        TextView title = (TextView) view.findViewById(R.id.tv_title);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
//        TextView desc = (TextView) view.findViewById(R.id.tv_desc);
        time.setText(TimeUtils.date2String(new Date(mBuilder.mData.getTimeMillis()), "MM-dd HH:mm:ss"));
        title.setText(mBuilder.mData.getTitle());
        if (TextUtils.isEmpty(mBuilder.mData.getContent())) {
            content.setText("您没有填写任何描述哦...");
        } else {
            content.setText(mBuilder.mData.getContent());
        }
//        desc.setText(mBuilder.mDesc);

        RandomTextView day = (RandomTextView) view.findViewById(R.id.rtv_day);
        day.setText("" + mBuilder.mDesc[0]);
        day.setPianyilian(RandomTextView.ALL);
        day.start();
        RandomTextView hour = (RandomTextView) view.findViewById(R.id.rtv_hour);
        hour.setText("" + mBuilder.mDesc[1]);
        hour.setPianyilian(RandomTextView.ALL);
        hour.start();
        RandomTextView minute = (RandomTextView) view.findViewById(R.id.rtv_minute);
        minute.setText("" + mBuilder.mDesc[2]);
        minute.setPianyilian(RandomTextView.ALL);
        minute.start();
    }

    public static class Builder {
        private final Context mContext;
        private TodoBean mData;
        private Long[] mDesc;

        public Builder(Context context) {
            mContext = context;
        }
        public Builder setData(TodoBean bean) {
            mData = bean;
            return this;
        }
        public void show() {
            new NearestTodoDialog(this).show();
        }

        public Builder setDesc(Long[] str) {
            mDesc = str;
            return this;
        }
    }

    private void show() {
        dialog.show();
    }
}

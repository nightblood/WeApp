package com.weapp.zlf.weapp.ui.widge;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.common.utils.TimeUtils;

import org.xutils.x;

import java.util.Date;


/**
 * Created by zhuliangfei on 2018/1/16.
 */

public class TagDetailDialog {


    private Builder mBuilder;
    private Dialog dialog;
    private View ivDismiss;

    private TagDetailDialog(Builder builder) {
        mBuilder = builder;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mBuilder.mContext).inflate(R.layout.ui_dialog_tag, null);
        dialog = new Dialog(mBuilder.mContext, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);

//        WindowManager windowManager = (WindowManager) mBuilder.mContext.getSystemService(Context.WINDOW_SERVICE);
//        Display display = windowManager.getDefaultDisplay();
//        view.setMinimumWidth(display.getWidth());

        TextView title = (TextView) view.findViewById(R.id.tv_title);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        TextView time = (TextView) view.findViewById(R.id.tv_time);
        title.setText(mBuilder.mTitle);
        content.setText(mBuilder.mContent);
        time.setText(TimeUtils.date2String(new Date(mBuilder.mTime)));
        ivDismiss = view.findViewById(R.id.iv_dismiss);
        ivDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.SlideOutDown)
                        .duration(500)
                        .withListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                dialog.dismiss();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        })
                        .playOn(ivDismiss);
            }
        });
    }

    private void show() {
        dialog.show();

        x.task().postDelayed(new Runnable() {
            @Override
            public void run() {
                ivDismiss.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInUp)
                        .duration(1000)
                        .playOn(ivDismiss);
            }
        }, 10);

    }

    public static class Builder{
        private final Context mContext;
        private String mTitle;
        private long mTime;
        private int mIsDone;
        private int mTagColor;
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
        public Builder setTime(long time) {
            this.mTime = time;
            return this;
        }
        public Builder setIsDone(int isDone) {
            this.mIsDone = isDone;
            return this;
        }
        public Builder setTagColor(int color) {
            this.mTagColor = color;
            return this;
        }

        public void show() {
            new TagDetailDialog(this).show();
        }
    }
}

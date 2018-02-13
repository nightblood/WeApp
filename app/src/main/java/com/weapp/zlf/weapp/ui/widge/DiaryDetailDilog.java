package com.weapp.zlf.weapp.ui.widge;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.DiaryBean;
import com.weapp.zlf.weapp.common.BannerImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.transformer.DepthPageTransformer;

import java.util.ArrayList;

import me.iwf.photopicker.PhotoPreview;

/**
 * Created by zhuliangfei on 2018/1/19.
 */

public class DiaryDetailDilog implements OnBannerListener {


    private Builder mBuilder;
    private Dialog mDialog;
    private Banner banner;

    private DiaryDetailDilog(Builder builder) {
        this.mBuilder = builder;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mBuilder.mContext).inflate(R.layout.ui_diary_detail_item, null);

        view.findViewById(R.id.iv_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        TextView name = (TextView) view.findViewById(R.id.tv_name);
        name.setText(mBuilder.mData.getTitle());
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        content.setText(mBuilder.mData.getContent());
        banner = (Banner) view.findViewById(R.id.banner);

        if (null != mBuilder.mData.getImages() && !mBuilder.mData.getImages().isEmpty()) {

            banner.setVisibility(View.VISIBLE);
            banner.setImages(mBuilder.mData.getImages())
                    .setImageLoader(new BannerImageLoader())
                    .setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                    .setPageTransformer(false, new DepthPageTransformer())
                    .setOnBannerListener(this);

        }
        WindowManager windowManager = (WindowManager) mBuilder.mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        view.setMinimumWidth(display.getWidth());

        mDialog = new Dialog(mBuilder.mContext, R.style.ActionSheetDialogStyle);
        mDialog.setContentView(view);
        Window window = mDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = 0;
        lp.y = 0;
        window.setAttributes(lp);
    }

    @Override
    public void OnBannerClick(int i) {
        PhotoPreview.builder()
                .setPhotos((ArrayList<String>) mBuilder.mData.getImages())
                .setCurrentItem(i)
                .setShowDeleteButton(false)
                .start((Activity) mBuilder.mContext);
    }

    public static class Builder {

        private final Context mContext;
        private DiaryBean mData;

        public Builder(Context context) {
            this.mContext =context;
        }
        public Builder setData(DiaryBean bean) {
            this.mData =bean;
            return this;
        }

        public void show() {
            new DiaryDetailDilog(this).show();
        }
    }

    private void show() {
        mDialog.show();
        banner.startAutoPlay();
    }
}

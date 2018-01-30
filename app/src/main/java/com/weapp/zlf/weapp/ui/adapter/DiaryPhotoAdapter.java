package com.weapp.zlf.weapp.ui.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.common.utils.SizeUtils;

import java.util.List;

import jp.wasabeef.glide.transformations.BitmapTransformation;

/**
 * Created by zhuliangfei on 2018/1/26.
 */

public class DiaryPhotoAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public DiaryPhotoAdapter(List<String> data) {
        super(R.layout.item_diary_photo, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        final ImageView imageView = helper.getView(R.id.iv_item);
//        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
//        layoutParams.height = SizeUtils.dp2px(100);
//        layoutParams.width = SizeUtils.dp2px(100);
        /* Glide.with(mContext).load(item).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                layoutParams.height = drawable.getIntrinsicHeight() * 3 / 4;
                layoutParams.width = drawable.getIntrinsicWidth() * 3 / 4;
                imageView.setImageDrawable(drawable);
            }
        });*/
        if (item.equals("NONE")) {
            Glide.with(mContext).load(R.drawable.icon_image).apply(RequestOptions.bitmapTransform(new FitCenter())).into(imageView);
        } else {
            Glide.with(mContext).load(item).apply(RequestOptions.bitmapTransform(new FitCenter())).into(imageView);
        }
    }
}

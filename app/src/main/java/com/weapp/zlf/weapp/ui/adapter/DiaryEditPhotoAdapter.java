package com.weapp.zlf.weapp.ui.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.weapp.zlf.weapp.R;

import java.util.List;

/**
 * Created by zhuliangfei on 2018/1/31.
 */

public class DiaryEditPhotoAdapter extends BaseQuickAdapter<String, BaseViewHolder>{
    public DiaryEditPhotoAdapter(List<String> data) {
        super(R.layout.item_diary_edit_photo, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        if (item.equals("btn_add")) {
            Glide.with(mContext).load(R.drawable.icon_add).apply(RequestOptions.bitmapTransform(new FitCenter())).into((ImageView)helper.getView(R.id.iv_item));
        } else {
            Glide.with(mContext).load(item).apply(RequestOptions.bitmapTransform(new FitCenter())).into((ImageView)helper.getView(R.id.iv_item));
        }
    }
}

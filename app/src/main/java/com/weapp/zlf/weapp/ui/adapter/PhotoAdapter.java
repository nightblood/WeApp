package com.weapp.zlf.weapp.ui.adapter;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.ImageBean;
import com.weapp.zlf.weapp.common.utils.FileUtils;
import com.weapp.zlf.weapp.common.utils.ScreenUtils;
import com.weapp.zlf.weapp.common.utils.SizeUtils;

import java.util.List;

/**
 * Created by zhuliangfei on 2018/1/23.
 */

public class PhotoAdapter extends BaseQuickAdapter<ImageBean, BaseViewHolder> {

    private final int mItemWidth;
    private final int mItemHeight;

    public PhotoAdapter(List<ImageBean> data) {
        super(R.layout.item_photo, data);
        mItemWidth = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(2) * 6) / 3;
        mItemHeight = ScreenUtils.getScreenHeight() * mItemWidth / ScreenUtils.getScreenWidth();
    }

    @Override
    protected void convert(BaseViewHolder helper, ImageBean item) {
        ImageView view = helper.getView(R.id.iv_item);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = mItemHeight;
        layoutParams.width = mItemWidth;
        if (FileUtils.isFileExists(item.getImage())) {
            Log.d(TAG, "convert: exist, " + item.getImage());
            Glide.with(mContext).load(item.getImage()).into(view);
        } else {
            Log.d(TAG, "convert: not exists, " + item.getImage());

        }
    }
}

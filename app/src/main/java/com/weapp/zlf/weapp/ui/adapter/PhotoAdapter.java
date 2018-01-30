package com.weapp.zlf.weapp.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.DiaryBean;
import com.weapp.zlf.weapp.bean.ImageBean;
import com.weapp.zlf.weapp.common.utils.AppUtils;
import com.weapp.zlf.weapp.common.utils.ScreenUtils;
import com.weapp.zlf.weapp.common.utils.SizeUtils;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

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
        view.getLayoutParams().height = mItemHeight;
        Glide.with(mContext).load(item.getImage()).into(view);
    }
}
